# SURFconext SelfService

## About OpenConext

OpenConext is an OpenSource technology stack for creating and running Collaboration platforms. It uses technologies from Federated Identity Management, as is available in Research and Educational Access Federations, Group management and OpenSocial Social Networking Technology. The aim of the software is to provide a middleware platform that can combine generic and specialized collaboration tools and services, within Research and Education, and beyond, and make these available for collaboration over institutional and national borders. The features section describes the current and planned features for the platform.

OpenConext was developed by SURFnet as part of the SURFworks programme. SURFnet runs an instance of the platform for research and education in The Netherlands as SURFconext


OpenConext: [http://www.openconext.org](http://www.openconext.org)
SURFconext: [http://www.surfconext.nl](http://www.surfconext.nl)


## Disclaimer

See the NOTICE file

## Release Notes

- To be done
- ..


## Getting Started

To be done

## System Requirements

- Java 6
- Maven 3

## Building and running

[Maven 3](http://maven.apache.org) is needed to build and run this project.

This project may depend on artifacts (poms, jars) from open source projects that are not available in a public Maven
repository. Dependencies with groupId org.surfnet.coin can be built from source from the following locations:

  - coin-master: git://github.com/OpenConext/OpenConext-parent.git
  - coin-test: git://github.com/OpenConext/OpenConext-test.git
  - coin-shared: git://github.com/OpenConext/OpenConext-shared.git
  - coin-api: git://github.com/OpenConext/OpenConext-api.git


To build:

    mvn clean install

To run locally:

    cd coin-selfservice-war
    mvn jetty:run
