# Jenkins-labs

A Maven-based Java project for API test automation using REST Assured and JUnit.
Built as part of a CI/CD learning series with Jenkins.

---

## API Under Test

[Fake Store API](https://fakestoreapi.com/) — a free, open REST API that simulates
an e-commerce store. No authentication required.

Base URI: `https://fakestoreapi.com`

---

## Project Structure

```
jenkins-labs/
+-- src/test/
¦   +-- java/com/jenkins/labs/
¦   ¦   +-- config/BaseTest.java            # Sets base URI for all tests
¦   ¦   +-- tests/
¦   ¦       +-- GetProductsTest.java        # GET /products
¦   ¦       +-- GetProductByIdTest.java     # GET /products/{id}
¦   ¦       +-- PostProductTest.java        # POST /products
¦   ¦       +-- ProductSchemaTest.java      # JSON schema validation
¦   +-- resources/schemas/
¦       +-- product-schema.json            # Expected product shape
+-- docs/
¦   +-- jenkins-setup.md                   # Jenkins installation guide
+-- pom.xml
+-- Dockerfile
+-- docker-compose.yml
+-- README.md
```

---

## How to Run Tests

### Prerequisites
- Java 11+
- Maven 3.6+

### Run all tests
```bash
mvn test
```

### Run a specific test class
```bash
mvn test -Dtest=GetProductsTest
```

### Run inside Docker
```bash
docker-compose up --build
```

---

## Test Coverage

| Test Class             | Endpoint             | Validations                                    |
|------------------------|----------------------|------------------------------------------------|
| GetProductsTest        | GET /products        | Status 200, non-empty list, field checks       |
| GetProductByIdTest     | GET /products/{id}   | Status 200, correct ID, all fields, price > 0  |
| PostProductTest        | POST /products       | Status 201, new ID returned, echo fields       |
| ProductSchemaTest      | GET /products/{id}   | Full JSON schema validation                    |

---

## Phase Roadmap

- [x] Phase 1 — Project setup & API tests
- [x] Phase 3 — Dockerization
- [x] Phase 4 — Jenkins setup (see [docs/jenkins-setup.md](docs/jenkins-setup.md))
- [x] Phase 5 — Jenkins pipeline (see [Jenkinsfile](Jenkinsfile))
- [ ] Phase 6 — Webhook integration (see [docs/webhook-setup.md](docs/webhook-setup.md))


