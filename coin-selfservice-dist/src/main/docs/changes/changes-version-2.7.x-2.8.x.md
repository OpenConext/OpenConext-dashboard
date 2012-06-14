# OpenConext Selfservice Configuration changes

## Versions
 - Current version: 2.8.x
 - Previous version: 2.7.x

## Instructions

The properties below are configured in

    /opt/tomcat/conf/classpath_properties/coin-selfservice.properties

### New properties:

The following properties were added. You can find their values in the property file for the specific environment.

    # Location of the json file that contains the labels for the ARP attributes
    personAttributesLabels.location=

    # Location of the SURFfederatie config file, can be a either URL that starts with http://, https://, file://, ftp://
    # Otherwise the application assumes it's a classpath resource
    federationconfig.location=

    # Amount of miliseconds between reloading the SURFfederatie config file (900000 = 15 minutes)
    federationconfig.reload.interval=

