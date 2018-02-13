#!/bin/bash
# file for local development to start the application.
mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev,local -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
