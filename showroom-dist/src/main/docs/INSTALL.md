<!--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->

# RELEASE INFORMATION

    Project:           Showrrom
    Date:              2013-03-18

    Content:
      1.  Unpack tarball
      2.  Prepare Tomcat
        2.1 Stop Tomcat
        2.2 Undeploy a previous version
        2.3 Copy / edit property files
      3.  Deploy war file
      4.  Start tomcat


## 1. UNPACK TARBALL

Unpack the provided tarball on the server that you want to deploy
the application on. e.g. extract the tarball in /tmp


## 2. PREPARE TOMCAT

This installation document only provides documentation for the Tomcat application server.

If you already have deployed a previous version of the showroom application
you must follow step 2.2a to undeploy the previous version.

If you have not deployed a previous version yet, follow 2.2b instead.

### 2.1 Stop Tomcat

Stop the tomcat application server

### 2.2a Undeploy a previous version (optional)
Navigate to the `<<CATALINA_HOME>>/wars/`
(e.g. /opt/tomcat/wars/)
delete the `showroom-war-<<VERION>>.war` file.
(e.g. showroom-war-2.6.0-SNAPSHOT.war)

Navigate to `<<CATALINA_HOME>>/work/showroom.{dev,test,acc}.surfconext.nl/`
Delete the entire selfservice directory listed there.

### 2.2b Prepare Tomcat for first time deployment
Create a webapps holder directory:
`<<CATALINA_HOME>>/webapps/showroom.{dev,test,acc}.surfconext.nl/`
and give ownership to tomcat:
`chown tomcat:tomcat <<CATALINA_HOME>>/webapps/showroom.{dev,test,acc}.surfconext.nl/`


### 2.3 Copy / edit property files

Out-of-the-box the tarball comes with a number of different property files.
A number of property files are delivered:

- showroom.properties.<<ENV>>
- selfservice-ehcache.xml.<<ENV>>


For different environments different property files are delivered. Pick the
appropriate property file for your environment from the following directory:
`<<EXTRACTED_TAR_BALL_PATH>>/tomcat/conf/classpath_properties`

Copy the chosen property files to:
`<<CATALINA_HOME>>/conf/classpath_properties/showroom.properties`
`<<CATALINA_HOME>>/conf/classpath_properties/selfservice-ehcache.xml`
`<<CATALINA_HOME>>/conf/classpath_properties/showroom-logback.xml`

Edit the values of the property files according to your environment.


## 3. DEPLOY WAR FILE

Copy the provided context descriptor from
`<<EXTRACTED_TAR_BALL_PATH>>/tomcat/conf/context`
to
`<<CATALINA_HOME>>/conf/Catalina/<<SELFSERVICE-VIRTUAL-HOST-DIRECTORY>>`
(e.g. /opt/tomcat/conf/Catalina/showroom.dev.surfconext.nl)

Now, copy the coin-portal war located at
`<<EXTRACTED_TAR_BALL_PATH>>/tomcat/webapps`
to
`<<CATALINA_HOME>>/wars/`
(e.g. /opt/tomcat/wars/


## 4. START TOMCAT

Start tomcat again.
