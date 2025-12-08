# Mutant Detector – Execution Guide

This document explains how to run the **Spring Boot** application locally and using **Docker**, under the `prod` profile.  
Requirements: **Java 21+**, **Docker** (if running containers).

---

## Required Environment Variables

The application **requires three environment variables** in all production executions:

- `DB_URL` — Postgres Database JDBC URL (e.g., `jdbc:postgresql://db-host:5432/dbname`)
- `DB_USER_NAME` — Database username
- `DB_PASSWORD` — Database password

Make sure these variables are defined **before running the application**, locally or in Docker.

---

## 1. Local Execution (without Docker)

### 1.1. Set environment variables (Linux/macOS example)

```bash
    export DB_URL='jdbc:postgresql://localhost:5432/db-name'
    export DB_USER_NAME='my_user'
    export DB_PASSWORD='my_password'
````

### 1.2. Build the application with the production profile

```bash
  ./mvnw -P prod clean install 
```

### 1.3. Run the JAR file

```bash
  java -jar target/mutant-detector-*.jar
```

The application will be available at:

```
http://localhost:8080
```

---

## 2. Execution with Docker

### 2.1. Build the Docker image

```bash
  docker build -t mutant-detector .
```

### 2.2. Run the container (passing the required environment variables)

```bash
    docker run -p 8080:8080 \
      -e DB_URL='jdbc:postgresql://db-host:5432/db-name' \
      -e DB_USER_NAME='my_user' \
      -e DB_PASSWORD='my_password' \
      --name mutant-detector \
      mutant-detector
```

### 2.3. Alternatively, use a `.env` file

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

## 3. Execution with Docker Compose (optional) (recommended)

From the root directory

Run:

```bash
  docker compose up
```

---

## 4. Main API Endpoints

| Method | Endpoint  | Description                             |
|--------|-----------|-----------------------------------------|
| POST   | `/mutant` | Checks whether a DNA sequence is mutant |
| GET    | `/stats`  | Returns analysis statistics             |

---

## 5. Running Unit Tests

```bash
  ./mvnw -P prod test
```

---

## 6. Important Notes

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