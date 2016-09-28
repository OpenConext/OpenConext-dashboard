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
- NodeJS 6.2.0 (best managed with `nvm`, current version in [.node-version](dashboard/.node-version)

### Building and running

#### Setup

Connect to your local mysql database: `mysql -uroot`

    CREATE DATABASE csa DEFAULT CHARACTER SET utf8;
    create user 'csa'@'localhost' identified by 'csa';
    grant all on csa.* to 'csa'@'localhost';

There is a `docker-compose` file available for mysql:

    docker-compose up -d

#### The Server

    cd selfservice

To build:

    mvn clean install

To run locally:

    mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev"

If you want to debug you can use

    ./debug.sh

#### The client

    cd dashboard

Initial setup if you do:

    nvm install
    npm install

Add new dependencies to `devDependencies`:

    npm install --save-dev ${dep}

To build:

    npm run webpack

To run locally:

    npm run webpack-dev-server

When you browse to the [application homepage](http://localhost:8001/) you will be prompted for a login.

A list of available log-ins can be found in the mocked implementation of the [VootClient](selfservice/src/main/java/selfservice/service/impl/VootClientMock.java).
