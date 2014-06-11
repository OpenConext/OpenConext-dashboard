#!/bin/bash
set -e

sudo -u tomcat -s
whoami

VERSION="${1}"

DASHBOARD_MAVEN_REPO="https://build.surfconext.nl/repository/public/snapshots/org/surfnet/coin/dashboard-dist"
WORK_DIR="/tmp/dashboard"
MAVEN_METADATA_XML="maven-metadata.xml"

rm -Rf "${WORK_DIR}"
mkdir -p "${WORK_DIR}"

cd "${WORK_DIR}"
# get metadata from from maven repo
echo "Retrieving full version information for snapshot: ${VERSION}..."
curl "${DASHBOARD_MAVEN_REPO}/${VERSION}/maven-metadata.xml" -o "${MAVEN_METADATA_XML}"
#parse the version
FULL_VERSION=`xmllint --shell "${MAVEN_METADATA_XML}" <<<"cat /metadata/versioning/snapshotVersions/snapshotVersion[1]/value/text()" | grep -v "^/ >"`
echo "Installing dashboard ${FULL_VERSION}..."

curl "${DASHBOARD_MAVEN_REPO}/${VERSION}/dashboard-dist-${FULL_VERSION}-bin.tar.gz" -o "dashboard-dist-${FULL_VERSION}-bin.tar.gz"
tar xvfz "dashboard-dist-${FULL_VERSION}-bin.tar.gz"

# stop tomcat

# put war in place

# start tomcat
