# SURFconext SelfService

[![Build Status](https://travis-ci.org/OpenConext/OpenConext-dashboard.svg)](https://travis-ci.org/OpenConext/OpenConext-dashboard)

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
- Gruntjs

### Building and running

Install Ruby to make management of the dependencies needed easier.

#### Setup

    brew install rbenv
    rbenv install
    gem install bundler
    bundle install

#### The Server

    cd coin-selfservice-war

To build:

    mvn clean install

To run locally:

    mvn spring-boot:run -Drun.jvmArguments="-Dspring.profiles.active=dev"

If you want to debug you can use
    
    ./debug.sh

#### The client

    cd coin-selfservice-standalone

Initial setup if you do:

    brew install npm;
    npm install -g grunt-cli;
    npm install;

When new grunt dependencies are added:

    npm install

To build:

    grunt watch

To run locally:

    grunt server


#### Running all at once

    foreman start

When you browse to the [application homepage](http://localhost:8001/) you will be prompted for a login.

A list of available log-ins can be found in the mocked implementation of the [VootClient](coin-selfservice-war/src/main/java/nl/surfnet/coin/selfservice/service/impl/VootClientMock.java).
