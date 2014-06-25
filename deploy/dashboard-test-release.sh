#!/bin/bash

set -e

usage() { echo "Usage: $0 [-u <SSH username to t02>] [-v <Maven version to deploy>]" 1>&2; exit 1; }

while getopts ":u:v:" o; do
    case "${o}" in
        u)
            username=${OPTARG}
            [ -n "${username}" ]|| usage
            ;;
        v)
            version=${OPTARG}
            [ -n "${version}" ]|| usage
            ;;
        *)
            usage
            ;;
    esac
done

if [ -z "${username}" ] || [ -z "${version}" ]
then
  usage
fi

SSH_COMMANDS=$(cat <<CMD

set -e

DASHBOARD_MAVEN_REPO="https://build.surfconext.nl/repository/public/releases/org/surfnet/coin/coin-selfservice-war"
WORK_DIR="/tmp/dashboard"
MAVEN_METADATA_XML="maven-metadata.xml"
TOMCAT_DIR="/opt/tomcat-low"
APP_NAME="dashboard.test.surfconext.nl"

echo "\${WORK_DIR}"

sudo -u tomcat rm -Rf "\${WORK_DIR}"
sudo -u tomcat mkdir -p "\${WORK_DIR}"

cd "\${WORK_DIR}"
FULL_VERSION="${version}"
echo "Installing dashboard \${FULL_VERSION}..."

sudo -u tomcat curl "\${DASHBOARD_MAVEN_REPO}/${version}/coin-selfservice-war-\${FULL_VERSION}-dashboard-dist.tar.gz" -o "dashboard-dist-\${FULL_VERSION}.tar.gz"
sudo -u tomcat tar xvfz "dashboard-dist-\${FULL_VERSION}.tar.gz"

sudo /etc/init.d/tomcat6-low stop

echo "Backup current installation"
sudo mv \${TOMCAT_DIR}/wars/dashboard-war-* /opt/tomcat-low/backups

echo "Delete current app"
sudo rm -Rf \${TOMCAT_DIR}/work/Catalina/\${APP_NAME}
sudo -u tomcat rm -Rf \${TOMCAT_DIR}/webapps/dashboard.test.surfconext.nl/*

echo "Copy new ROOT.xml"
sudo cp \${WORK_DIR}/dashboard-dist-${version}/tomcat/conf/context/ROOT.xml \${TOMCAT_DIR}/conf/Catalina/\${APP_NAME}

echo "Copy new WAR"
sudo cp \${WORK_DIR}/dashboard-dist-${version}/tomcat/webapps/dashboard-war-${version}.war \${TOMCAT_DIR}/wars

sudo /etc/init.d/tomcat6-low start
echo "Done!"
CMD
)

ssh -t -l "${username}" t02.dev.coin.surf.net "${SSH_COMMANDS}" #'bash -s' < deploy.sh "${version}"
