# OpenConext Selfservice Configuration changes

## Versions
 - Current version: 2.8.x
 - Previous version: 2.7.x

## Instructions

The properties below are configured in

    /opt/tomcat/conf/classpath_properties/coin-selfservice.properties

### New properties:

    # Location of the json file that contains the labels for the ARP attributes
    personAttributesLabels.location=classpath:attributes.json
