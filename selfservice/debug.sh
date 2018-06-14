#!/bin/bash
# file for local development to start the application.
mvn spring-boot:run -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
