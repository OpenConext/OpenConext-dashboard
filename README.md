# SURFconext Dashboard

[![Build Status](https://travis-ci.org/OpenConext/OpenConext-dashboard.svg)](https://travis-ci.org/OpenConext/OpenConext-dashboard)
[![codecov.io](https://codecov.io/github/OpenConext/OpenConext-dashboard/coverage.svg)](https://codecov.io/github/OpenConext/OpenConext-dashboard)

### [About OpenConext](#about_openConext)

OpenConext is an OpenSource technology stack for creating and running Collaboration platforms. It uses technologies from Federated Identity Management, as is available in Research and Educational Access Federations, Group management and OpenSocial Social Networking Technology. The aim of the software is to provide a middleware platform that can combine generic and specialized collaboration tools and services, within Research and Education, and beyond, and make these available for collaboration over institutional and national borders. The features section describes the current and planned features for the platform.

OpenConext was developed by SURFnet as part of the SURFworks programme. SURFnet runs an instance of the platform for research and education in The Netherlands as SURFconext

OpenConext: [https://www.openconext.org](https://www.openconext.org)

SURFconext: [https://www.surfconext.nl](https://www.surfconext.nl)


## [Getting started](#getting_started)

### [System Requirements](#system_requirements)

- Java 8
- Maven 3
- NodeJS v14.17.3 (best managed with `nvm`, current version in [.nvmrc](dashboard-gui/.nvmrc)
- yarn 1.1.0

## [Building and running](#building_and_running)

### [Setup](#setup)

#### [The Server](#server)

    cd dashboard

To build:

    mvn clean install

To run locally either start the Application from your IDE or use the spring-boot maven plugin:

    mvn spring-boot:run

If you want to debug you can either debug the Application in your IDE or use:

    ./debug.sh
    
#### [Feature toggles](#feature_toggles)

In the [application.properties](dashboard-server/src/main/resources/application.properties) file you can disable / enable
all remote interfaces like JIRA, Mail, SAB, VOOT, Statistics, PDP, OIDC, Manage by setting the `dashboard.feature.X`
to `false` or `true`. Default they are all disabled and mock implementations are used. Using ansible for
deployment they can enabled.

#### [The client](#client)

    cd dashboard

Initial setup if you do:

    yarn install

To build:

    yarn run webpack

To run locally:

    yarn start

The browse to the [application homepage](http://localhost:3000/services?mockUser=admin).

A list of available log-ins can be found in the mocked implementation of the [VootClient](dashboard-server/src/main/java/dashboard/shibboleth/mock/MockShibbolethFilter.java).

### [Manage queries](#manage_queries)
```
curl -H 'Content-Type: application/json' -u pdp:secret  -X POST -d '{"REQUESTED_ATTRIBUTES":["metaDataFields.coin:ss:idp_visible_only"],"metaDataFields.coin:ss:idp_visible_only":"1"}' 'https://manage.test2.surfconext.nl//manage/api/internal/search/saml20_sp' | python -m json.tool
```

### [Testing](#testing)

To run all JavaScript tests:
```
cd client
yarn test
```
Or to run all the tests and do not watch:
```
cd client
CI=true yarn test
```
