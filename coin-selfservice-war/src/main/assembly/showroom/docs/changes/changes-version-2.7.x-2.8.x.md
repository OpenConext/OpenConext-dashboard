# OpenConext Selfservice Configuration changes

## Versions
 - Current version: 2.8.x
 - Previous version: 2.7.x

## Instructions

The properties below are configured in

    /opt/tomcat/conf/classpath_properties/coin-selfservice.properties

### New properties:

The following properties were added. You can find their values in the property file for the specific environment.

    # Connection to Shindig database
    coin-shindig-db-driver=
    coin-shindig-db-url=
    coin-shindig-db-username=
    coin-shindig-db-password=

    # Location of the json file that contains the labels for the ARP attributes
    personAttributesLabels.location=

    # Location of the SURFfederatie config file, can be a either URL that starts with http://, https://, file://, ftp://
    # Otherwise the application assumes it's a classpath resource
    federationconfig.location=

    # Amount of miliseconds between reloading the SURFfederatie config file (900000 = 15 minutes)
    federationconfig.reload.interval=

    # Only members of the following team are admins
    admin.teamname=

### Remove file

Delete file `/opt/tomcat/conf/classpath_properties/fedcfg.xml`

## Instructions for SURFconext admins

### Add metadata in Janus module (Service Registry)


    coin:gadgetbaseurl = same value as api-consumerkey in coin-selfservice.properties for the environment
    coin:oauth:secret = same value as api-consumersecret in coin-selfservice.properties for the environment
    coin:no_consent_required = √
    coin:oauth:app_title = SURFconext | Self service | SURFnet (app title)
    coin:oauth:consent_not_required = √

### Add ACL to let Selfservice retrieve group information

  * Go to Manage.surfconext
  * Group providers
  * For grouper click on ACL
  * Allow selfservice.surfconext to retrieve group information from SURFconext grouper
