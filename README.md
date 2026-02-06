<!-- TOC -->
* [Mutant Detector – Execution Guide](#mutant-detector--execution-guide)
  * [Problem statement](#problem-statement)
  * [Requirements](#requirements)
    * [Dependencies](#dependencies)
    * [Environment Variables](#environment-variables)
  * [Execution](#execution)
    * [Local Execution (without Docker)](#local-execution-without-docker)
      * [1. Set environment variables (Linux/macOS example)](#1-set-environment-variables-linuxmacos-example)
      * [2. Build the application with the production profile](#2-build-the-application-with-the-production-profile)
      * [3. Run the JAR file](#3-run-the-jar-file)
    * [Execution with Docker](#execution-with-docker)
      * [1. Build the Docker image](#1-build-the-docker-image)
      * [2. Run the container (passing the required environment variables)](#2-run-the-container-passing-the-required-environment-variables)
      * [3. Alternatively, use a `.env` file](#3-alternatively-use-a-env-file)
    * [Execution with Docker Compose (optional) (recommended for quick local evaluation)](#execution-with-docker-compose-optional-recommended-for-quick-local-evaluation)
      * [Caution](#caution)
    * [Database and migrations](#database-and-migrations)
      * [Benefits](#benefits)
    * [Running Unit Tests](#running-unit-tests)
  * [Main API Endpoints](#main-api-endpoints)
    * [Request / Response examples](#request--response-examples)
      * [POST Mutant example](#post-mutant-example)
      * [GET Stats example](#get-stats-example)
  * [Important Notes](#important-notes)
    * [Decisions against the problem statement](#decisions-against-the-problem-statement)
    * [Considerations on running with prod profile](#considerations-on-running-with-prod-profile)
<!-- TOC -->

---

# Mutant Detector – Execution Guide

This document explains how to run the **Spring Boot** application locally and using **Docker**, under a `prod` profile.
`prod` profile represents a production-like configuration, not a hardened or production-ready deployment.

## Problem statement

This service exposes a REST API that determines whether a DNA sample belongs to a mutant, based on predefined
pattern detection rules. The service stores analysis results and generates statistics of processed samples.

The full problem description is available here:
[Problem statement (PDF)](docs/problem-statement.pdf)

---

## Requirements

### Dependencies
  **Java 21+**, **Docker** (if running containers).

### Environment Variables

The application **requires three environment variables** in all production-**profile** executions:

- `DB_URL` — Postgres Database JDBC URL (e.g., `jdbc:postgresql://db-host:5432/dbname`)
- `DB_USER_NAME` — Database username
- `DB_PASSWORD` — Database password

Make sure these variables are defined **before running the application on prod profile**, locally or in Docker.

---

## Execution
### Local Execution (without Docker)

#### 1. Set environment variables (Linux/macOS example)

```bash
    export DB_URL='jdbc:postgresql://localhost:5432/db-name'
    export DB_USER_NAME='my_user'
    export DB_PASSWORD='my_password'
````

#### 2. Build the application with the production profile

```bash
  ./mvnw -P prod clean install 
```

#### 3. Run the JAR file

```bash
  java -jar target/mutant-detector-*.jar
```

The application will be available at:

```
http://localhost:8080
```

---

### Execution with Docker

#### 1. Build the Docker image

```bash
  docker build -t mutant-detector .
```

#### 2. Run the container (passing the required environment variables)

```bash
    docker run -p 8080:8080 \
      -e DB_URL='jdbc:postgresql://db-host:5432/db-name' \
      -e DB_USER_NAME='my_user' \
      -e DB_PASSWORD='my_password' \
      --name mutant-detector \
      mutant-detector
```

#### 3. Alternatively, use a `.env` file

Create a `.env` file:

```
DB_URL=jdbc:postgresql://db-host:5432/mutantdb
DB_USER_NAME=my_user
DB_PASSWORD=my_password
```

Run the container using the `.env` file:

```bash
  docker run --env-file .env -p 8080:8080 mutant-detector
```

---

### Execution with Docker Compose (optional) (recommended for quick local evaluation)

With docker compose, you can set up the full environment to execute the app on `prod` profile with no extra configuration.
It will use the latest image tag (intended for local evaluation only) of mutant-detector image on repo jp-dev273/mutant-detector, and provide a postgres service
which the application uses for data persistence.

Its data will be saved on its own anonymous volume. 

#### Caution

When starting again `docker compose up` it will create another anonymous volume, which involves separating data created 
previously on this docker compose up. Its made with this approach mainly for simplicity

If you run  `docker compose down -v` it will destroy all related volumes, therefore, your data will be destroyed too.

*Do not use* docker compose if you have a service using port 8080 on your computer.

From the root directory, run:

```bash
  docker compose up
```

---

### Database and migrations

Database schema creation and migrations are managed automatically using Liquibase.

On application startup, Liquibase compares the current database state with defined changelog files 
and applies any pending changes in a deterministic order. Each change is tracked using a unique identifier, 
ensuring that migrations are executed exactly once per database instance.

This process runs automatically on startup and does not require manual intervention. If a migration fails, the application
will not start.

#### Benefits

- Guarantees that database schema is always aligned with the application version.
- Eliminates manual SQL execution and environment-specific drift.
- Provides deterministic, repeatable database changes across environments.
- Allows safe schema evolution with traceability and rollback capability.

### Running Unit Tests

On default profile (dev) you can run tests without declaring environment variables using:
```bash
  ./mvnw test
```

if you want to run test with the production profile, which needs a different database,
you will need to declare the environment variables mentioned above:
[Set environment variables (Linux/macOS example)](#1-set-environment-variables-linuxmacos-example)
```bash
  ./mvnw -P prod test
```

## Main API Endpoints

| Method | Endpoint   | Description                             |
|--------|------------|-----------------------------------------|
| POST   | `/mutants` | Checks whether a DNA sequence is mutant |
| GET    | `/stats`   | Returns analysis statistics             |

### Request / Response examples

#### POST Mutant example

*Request*:

- Body:
```json
  {
    "dna": ["AGAAGG", "ATGCGA", "CAGTGC", "CCCCTA", "TCACTG", "GAGTCC" ]
  }
```

*Response*:

- Status: *204 NO CONTENT*

Notes:
- This endpoint does not require custom request headers, and returns no response body.

#### GET Stats example

*Response*:

- Status: *200 OK* 
- Body: 
```json
  {
    "count_mutant_dna": 40,
    "count_human_dna": 100,
    "ratio": 0.4
  }
```

Notes:
- This endpoint does not require custom request headers.

---

## Important Notes

### Decisions against the problem statement

In the [Problem statement](#problem-statement), was specified an HTTP CODE 200 OK, but no body content was included.
This response was not invalid, but was not completely clear since content was not being provided as response.
Therefore, two options were considered.

First option was to include a body containing a boolean variable saying whether isMutant was true or not. Second option
was to CHANGE HTTP CODE 200 OK for 204 NO CONTENT in order to be clearer about the response.

As a final choice, was decided to use the second option as my approach. The endpoint represents a command whose only
outcome is success or failure. Since no resource representation is returned, 204 No Content was chosen to make this explicit.

### Considerations on running with prod profile

* The application will not start in `prod` mode unless `DB_URL`, `DB_USER_NAME`, and `DB_PASSWORD` are provided.
* Ensure your `application-prod.yml` or `application-prod.properties` maps these variables, for example:

- application-prod.properties:
  ```.properties
  spring.datasource.url=${DB_URL}
  spring.datasource.username=${DB_USER_NAME}
  spring.datasource.password=${DB_PASSWORD}
  ```
- application-prod.yml:
  ```yaml
  spring:
    datasource:
        url: ${DB_URL}
        username: ${DB_USER_NAME}
        password: ${DB_PASSWORD}
  ```
