#!/bin/bash

set -e

usage() { echo "Usage: $0 [-u <SSH username to t02>] [-v <Maven version to deploy>] [-t <Snapshot or release s|r>] [-a <name of the app e.g. dashboard, csa>]" 1>&2; exit 1; }

while getopts ":u:v:t:a:" o; do
    case "${o}" in
        u)
            username=${OPTARG}
            [ -n "${username}" ]|| usage
            ;;
        v)
            version=${OPTARG}
            [ -n "${version}" ]|| usage
            ;;
        t)
            type=${OPTARG}
            [ -n "${type}" ]|| usage
            ;;
        a)
            app_name=${OPTARG}
            [ -n "${app_name}" ]|| usage
            ;;
        *)
            usage
            ;;
    esac
done

if [ -z "${username}" ] || [ -z "${version}" ] || [ -z "${type}" ] || [ -z "${app_name}" ]
then
  usage
fi

PROJECT_NAME="coin-selfservice-war"

if [ "${type}" == "s" ]
then
    MAVEN_REPO="https://build.surfconext.nl/repository/public/snapshots/org/surfnet/coin/${PROJECT_NAME}"
else
    MAVEN_REPO="https://build.surfconext.nl/repository/public/releases/org/surfnet/coin/${PROJECT_NAME}"
fi


SSH_COMMANDS=$(cat <<CMD

set -e

WORK_DIR="/tmp/${app_name}"
MAVEN_METADATA_XML="maven-metadata.xml"
TOMCAT_DIR="/opt/tomcat-low"
APP_NAME="${app_name}.test.surfconext.nl"

echo "\${WORK_DIR}"

sudo -u tomcat rm -Rf "\${WORK_DIR}"
sudo -u tomcat mkdir -p "\${WORK_DIR}"

cd "\${WORK_DIR}"
if [ "${type}" == "s" ]
then
    # get metadata from from maven repo
    echo "Retrieving full version information for snapshot: ${version}..."
    sudo -u tomcat curl "${MAVEN_REPO}/${version}/maven-metadata.xml" -o "\${MAVEN_METADATA_XML}"
    FULL_VERSION=\`xmllint --shell "\${MAVEN_METADATA_XML}" <<<"cat /metadata/versioning/snapshotVersions/snapshotVersion[1]/value/text()" | grep -v "^/ >"\`
else
    FULL_VERSION=${version}
fi

echo "Installing ${app_name} \${FULL_VERSION}..."

sudo -u tomcat curl "${MAVEN_REPO}/${version}/${PROJECT_NAME}-${app_name}-dist-\${FULL_VERSION}-bin.tar.gz" -o "${app_name}-dist-\${FULL_VERSION}-bin.tar.gz"
sudo -u tomcat tar xvfz "${app_name}-dist-\${FULL_VERSION}-bin.tar.gz"

sudo /etc/init.d/tomcat6-low stop

echo "Backup current installation"
sudo -u tomcat mv \${TOMCAT_DIR}/wars/${app_name}-war-* /opt/tomcat-low/backups

echo "Delete current app"
sudo -u tomcat rm -Rf \${TOMCAT_DIR}/work/Catalina/\${APP_NAME}
sudo -u tomcat rm -Rf \${TOMCAT_DIR}/webapps/${app_name}.test.surfconext.nl/*

echo "Copy new ROOT.xml"
sudo -u tomcat cp \${WORK_DIR}/${app_name}-dist-${version}/tomcat/conf/context/ROOT.xml \${TOMCAT_DIR}/conf/Catalina/\${APP_NAME}

echo "Copy new WAR"
sudo -u tomcat cp \${WORK_DIR}/${app_name}-dist-${version}/tomcat/webapps/${app_name}-war-${version}.war \${TOMCAT_DIR}/wars

sudo /etc/init.d/tomcat6-low start
echo "Done!"
CMD
)

ssh -t -l "${username}" t02.dev.coin.surf.net "${SSH_COMMANDS}" #'bash -s' < deploy.sh "${version}"
