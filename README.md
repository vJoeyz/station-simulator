# OCPP Station Simulator

[Download](https://bintray.com/everon/maven/ocpp-station-simulator/_latestVersion)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=station-simulator&metric=bugs)](https://sonarcloud.io/dashboard?id=station-simulator)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=station-simulator&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=station-simulator)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=station-simulator&metric=coverage)](https://sonarcloud.io/dashboard?id=station-simulator)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=station-simulator&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=station-simulator)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=station-simulator&metric=security_rating)](https://sonarcloud.io/dashboard?id=station-simulator)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=station-simulator&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=station-simulator)

## Introduction

The simulator tool is designed to simulate charging station. It implements [OCPP protocol](https://en.wikipedia.org/wiki/Open_Charge_Point_Protocol) and talks to Charging Station Management 
System (CSMS) via WebSocket protocol.

## Pre-requisites
* JRE 8 or above
* A fully running charging station management system (CSMS)

## Installation

```bash
$ git clone https://github.com/everonhq/station-simulator.git
$ cd station-simulator/
$ ./gradlew build
```

If you wish to include the simulator in your project as a library:

### Maven

Add the following to your project `POM` file:
```xml
<dependency>
  <groupId>io.everon</groupId>
  <artifactId>ocpp-station-simulator</artifactId>
  <version>0.5.0</version>
  <type>pom</type>                                                                         
</dependency>
```

### Gradle

Add the following to your `build.gradle` file
```groovy
compile 'io.everon:ocpp-station-simulator:0.5.0'
```

## Build

To build:
```bash
$ ./gradlew build
```

## Usage
Starts the simulator with one station, which has a single EVSE and a single connector attached to it:

```bash
 $ ./gradlew run -Parguments="ws://{ocpp_endpoint_url} --configuration {'stations':[{'id':'EVB-P17390866','evse':{'count':1,'connectors':1}}]}"
````

## Full Documentation
See the [Wiki](https://github.com/everonhq/station-simulator/wiki) for more examples, operational details, supported use cases and other information.

## Bugs and Feedback
For bugs, questions and discussions please check for any existing issues created under the *[GitHub Issues](https://github.com/everonhq/station-simulator/issues)* section.
