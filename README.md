# SURFconext SelfService

## About OpenConext

OpenConext is an OpenSource technology stack for creating and running Collaboration platforms. It uses technologies from Federated Identity Management, as is available in Research and Educational Access Federations, Group management and OpenSocial Social Networking Technology. The aim of the software is to provide a middleware platform that can combine generic and specialized collaboration tools and services, within Research and Education, and beyond, and make these available for collaboration over institutional and national borders. The features section describes the current and planned features for the platform.

OpenConext was developed by SURFnet as part of the SURFworks programme. SURFnet runs an instance of the platform for research and education in The Netherlands as SURFconext


OpenConext: [http://www.openconext.org](http://www.openconext.org)

SURFconext: [http://www.surfconext.nl](http://www.surfconext.nl)


## Getting started

### System Requirements

- Java 6
- Maven 3
- MySQL 5.5

### Building and running

[Maven 3](http://maven.apache.org) is needed to build and run this project.

To build:

    mvn clean install

To run locally:

    cd coin-selfservice-war
    mvn jetty:run

When you browse to the [application homepage](http://localhost:8082/selfservice) you will be prompted for a login.

A list of available log-ins can be found in the mocked [`Users` enum here](coin-selfservice-war/src/main/java/nl/surfnet/coin/selfservice/util/OpenConextOAuthClientMock.java).

###
If you run into this Maven error message: "TypeError: can't convert String into Array", it means you have run into a bug in the sass-maven-plugin.

At the time of writing, the fix for said bug was not released yet. See: https://github.com/Jasig/sass-maven-plugin/issues/47

Resolution: make sure there is no trace at all of a GEM_PATH on your machine, see: http://stackoverflow.com/questions/3558656/how-can-i-remove-rvm-ruby-version-manager-from-my-system

## Disclaimer

SelfService uses Highcharts JavaScript for rendering statistics. Highcharts is a Highsoft sotware product which is free for non-commercial use, but it is not free for commercial use. See [this page](http://shop.highsoft.com/highcharts.html#redist) for more details.