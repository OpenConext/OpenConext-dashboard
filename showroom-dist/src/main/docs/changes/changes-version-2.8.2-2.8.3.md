# OpenConext Selfservice Configuration changes

## Versions
 - Current version: 2.8.2
 - Previous version: 2.7.3

## Instructions

The properties below are configured in

    /opt/tomcat/conf/classpath_properties/coin-selfservice.properties

### New properties:

The following properties were added. You can find their values in the property file for the specific environment.

	coin-api.jdbc.url=jdbc:mysql://db.surfconext.nl:3306/api
	coin-api.jdbc.user=??
	coin-api.jdbc.password=??

	coin-api.jdbc.driver=com.mysql.jdbc.Driver

Note that the property:

	coin-selfservice.jdbc.url=jdbc:mysql://db.acc.surfconext.nl:3306/selfservice

is changed to point to the (new) database of selfservice. SelfService now has it's own database (as it should) and does not piggyback on api's database. However it does need to read from the api database.