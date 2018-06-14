# SURFconext Dashboard

[![Build Status](https://travis-ci.org/OpenConext/OpenConext-dashboard.svg)](https://travis-ci.org/OpenConext/OpenConext-dashboard)
[![codecov.io](https://codecov.io/github/OpenConext/OpenConext-dashboard/coverage.svg)](https://codecov.io/github/OpenConext/OpenConext-dashboard)

## About OpenConext

OpenConext is an OpenSource technology stack for creating and running Collaboration platforms. It uses technologies from Federated Identity Management, as is available in Research and Educational Access Federations, Group management and OpenSocial Social Networking Technology. The aim of the software is to provide a middleware platform that can combine generic and specialized collaboration tools and services, within Research and Education, and beyond, and make these available for collaboration over institutional and national borders. The features section describes the current and planned features for the platform.

OpenConext was developed by SURFnet as part of the SURFworks programme. SURFnet runs an instance of the platform for research and education in The Netherlands as SURFconext

OpenConext: [https://www.openconext.org](https://www.openconext.org)

SURFconext: [https://www.surfconext.nl](https://www.surfconext.nl)


## Getting started

### System Requirements

- Java 8
- Maven 3
- MySQL 5.5
- NodeJS v8.9.0 (best managed with `nvm`, current version in [.node-version](dashboard/.node-version)
- yarn 1.1.0

### Building and running

#### Setup

#### The Server

    cd selfservice

To build:

    mvn clean install

To run locally:

    mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev"

If you want to debug you can use

    ./debug.sh
    
To run without statistics - e.g. without any need for an internet connection - run    

    ./debug-local.sh

#### The client

    cd dashboard

Initial setup if you do:

    nvm install
To build:

    yarn run webpack

To run locally:

    yarn start

When you browse to the [application homepage](http://localhost:8001/dashboard/api/home) you will be prompted for a login.

A list of available log-ins can be found in the mocked implementation of the [VootClient](selfservice/src/main/java/selfservice/service/impl/VootClientMock.java).

#Manage queries
```
curl -H 'Content-Type: application/json' -u pdp:secret  -X POST -d '{"REQUESTED_ATTRIBUTES":["metaDataFields.coin:type_of_service:nl","metaDataFields.coin:type_of_service:en"],"metaDataFields.coin:type_of_service:en":".*"}' 'https://manage.test2.surfconext.nl//manage/api/internal/search/saml20_sp' | python -m json.tool 
```
