[![Build and test](https://github.com/simerda/homebrew-dash/actions/workflows/test.yml/badge.svg)](https://github.com/simerda/homebrew-dash/actions/workflows/test.yml)
[![codecov](https://codecov.io/gh/simerda/homebrew-dash/graph/badge.svg?token=T2IDD92GL9)](https://codecov.io/gh/simerda/homebrew-dash)


# Homebrew Dash REST API

> Version 1.0.0

## Overview

Homebrew Dash is a Spring Boot-based Backend project exposing REST API designed for managing various resources
related to home-brewing, including *malts*, *hops* and *yeasts* as well
as *beers* (planned / in the process of making / history of brewed ones).

The other major part of the project is to revolves around IoT devices used in home-brewing. The project provides
the interface for the [iSpindel](https://github.com/universam1/iSpindel) hydrometer, such that the device
can periodically read and store measurements of temperature and gravity in the brewing vessel.
The other IoT part of the project is the usage of the [Meross IoT library](https://github.com/albertogeniola/MerossIot)
to control the [Meross](https://www.meross.com) smart plugs. This is used for switching of a heating or cooling element 
based on the temperature measured by the iSpindel device to keep the optimal set temperature during fermentation.

This project provides a robust backend system for home-brewing enthusiasts to organize and track their stock
as well as their brewing activities and integration of useful IoT devices.

## Table of Contents

- [Features](#features)
- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)
    - [Authentication](#authentication)
    - [Endpoints](#endpoints)
- [Contributing](#contributing)
- [License](#license)

---

## Features

### Users Authentication
- [x] Implement user sessions and authentication
- [x] Enforce user-friendly response upon request failures
- [x] Add authentication and user authorization for various resources

### Malts and Malt Changes
- [x] Manage malts and malt changes
- [x] Implement CRUD operations for malts
- [x] Store malt characteristics
- [x] Track changes in malt stock over time

### Hops and Hop Changes
- [x] Handle hops and hop changes
- [x] Enable CRUD operations for hops
- [x] Store hop characteristics
- [x] Record changes in hop stock over time

### Yeasts and Yeast Changes
- [x] Support yeasts and yeast changes
- [x] Provide CRUD operations for yeasts
- [x] Store yeast characteristics
- [x] Track changes in yeast stock over time

### Beers
- [x] Manage beer history or beer stat are planned or in the brewing stage
- [x] Define a set brewing temperature
- [x] Implement CRUD operations for beers
- [x] Track states of beers and the remaining amount

### Hydrometers
- [x] Manage hydrometers (iSpindel)
- [x] Implement CRUD operations for hydrometers

### Measurements
- [x] Handle measurements of provided hydrometers
- [x] Implement CRUD operations for measurements

### Thermostats
- [x] Support thermostats (Meross smart plugs)
- [x] Implement CRUD operations for thermostats
- [x] Attach thermostat to a hydrometer to access measurements
- [x] Switch cooling/heating elements based on set fermenting temperature


---

## Getting Started

For a smooth set-up, it is recommended to run the app via Docker.
Alternatively you will need to have JDK supporting Java 17,
Python 3.8 (for the Meross IoT library) and PostgreSQL installed on your machine.

### Prerequisites

- Docker

**OR**

- Java 17
- Python 3.8
- PostgreSQL

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/homebrew-dash.git
   ```

2. Set project variables:

   ```bash
   cp .env.example .env
   ```

   This will initialize the project variables to the recommended default.
   You may customize the setting using your preferred text editor:

   ```bash
   nano .env
   ```

3. Build the project:

   ```bash
   cd homebrew-dash
   ./gradlew bootJar
   ```

4. Run the application:

   ```bash
   java -jar build/libs/*.jar
   ```

   Or, using Docker:

   ```bash
   docker-compose up
   ```

---

## Usage

The app is hosted on Heroku at the URL
[https://homebrew-dash-3919a7b12f63.herokuapp.com/api/v1/](https://homebrew-dash-3919a7b12f63.herokuapp.com/api/v1/)


### Authentication

The API uses authentication to secure access to resources.
Authentication details are required in the `Authorization` request header in the form of a `Bearer {token}`.
The token is tied to a time limited user-session which you can create at the `/user-sessions`
endpoint upon providing valid user log-in credentials.
You can create a new used by using the unprotected `POST` method on the `/users` endpoint. 

### Documentation

Detailed information about available endpoints and their usage can be found in the browsable *Swagger UI* [API Documentation](https://simerda.dev/api/docs-ui).

Or the *OpenAPI* specification can be downloaded at [https://simerda.dev/api/docs](https://simerda.dev/api/docs)

---

## Tech stack

A non-exhaustive list of software technologies / features used is as follows:
- Java 17
- Spring Boot 3
- Scheduling
- JUnit
- Mockito
- Hibernate
- Gradle
- Python
- PostgreSQL
- H2 DB (in-memory for tests)
- Liquibase
- Docker (and Docker Compose)
- CI via GitHub Actions
- CD via Heroku
- JaCoCo + Codecov for code coverage
- Dependabot

---

## License

This project is licensed under the [MIT License](LICENSE). See the [LICENSE](LICENSE) file for details.

---
