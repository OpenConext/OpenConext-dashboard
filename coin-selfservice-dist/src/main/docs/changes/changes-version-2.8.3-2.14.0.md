# OpenConext Selfservice Configuration changes

## Versions
 - Current version: 2.14.0
 - Previous version: 2.8.3

### Changed properties in coin-selfservice.properties

The following properties are obsolete and should be removed:

* federationconfig.location
* federationconfig.reload.interval
* admin.teamname

The following properties have been added:

* admin.distribution.channel.teamname
* admin.licentie.idp.teamname
* admin.surfconext.idp.teamname
* coin-lmng-active-mode
* coin-lmngClientCertificate
* coin-lmngClientCertificatePassword
* coin-lmngClientCertificatePrivateKey
* coin-lmng-debug
* coin-lmng-endpoint
* coin-lmngRootCertificate
* cacheManagerClass
* hibernate-selfservice.dialect
* hibernate-selfservice.format_sql
* hibernate-selfservice.hbm2ddl.auto
* hibernate-selfservice.show_sql
* janus.class
* keyStoreClass
* lmngArticleCacheSeconds
* lmngDeepLinkBaseUrl
* lmngLicenseCacheSeconds
* lmngServiceClass
* openConextApiClient
* saml-uuid-attribute

### New properties file: metadata.selfservice.properties
please copy the metadata.selfservice.properties.ENVIRONMENT to the conf/classpath_properties folder in tomcat

