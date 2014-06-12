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

DASHBOARD_MAVEN_REPO="https://build.surfconext.nl/repository/public/snapshots/org/surfnet/coin/dashboard-dist"
WORK_DIR="/tmp/dashboard"
MAVEN_METADATA_XML="maven-metadata.xml"
TOMCAT_DIR="/opt/tomcat-low"

echo "\${WORK_DIR}"

rm -Rf "\${WORK_DIR}"
mkdir -p "\${WORK_DIR}"

cd "\${WORK_DIR}"
# get metadata from from maven repo
echo "Retrieving full version information for snapshot: ${version}..."
curl "\${DASHBOARD_MAVEN_REPO}/${version}/maven-metadata.xml" -o "\${MAVEN_METADATA_XML}"
FULL_VERSION=\`xmllint --shell "\${MAVEN_METADATA_XML}" <<<"cat /metadata/versioning/snapshotVersions/snapshotVersion[1]/value/text()" | grep -v "^/ >"\`
echo "Installing dashboard \${FULL_VERSION}..."

curl "\${DASHBOARD_MAVEN_REPO}/${version}/dashboard-dist-\${FULL_VERSION}-bin.tar.gz" -o "dashboard-dist-\${FULL_VERSION}-bin.tar.gz"
tar xvfz "dashboard-dist-\${FULL_VERSION}-bin.tar.gz"

sudo /etc/init.d/tomcat6-low stop
sudo -u tomcat mv \${TOMCAT_DIR}/wars/dashboard-war-* /opt/tomcat-low/backups
sudo -u tomcat rm -Rf \${TOMCAT_DIR}/work/Catalina/dashboard.test.surfconext.nl
sudo -u tomcat cp \${TOMCAT_DIR}/
CMD
)

ssh -t -l "${username}" t02.dev.coin.surf.net "${SSH_COMMANDS}" #'bash -s' < deploy.sh "${version}"
