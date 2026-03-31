# Jenkins-labs

A portfolio-grade CI/CD project demonstrating automated API testing with Jenkins,
REST Assured, Maven, Docker, and Slack notifications.

---

## Tech Stack

| Tool | Purpose |
|---|---|
| Java 11 | Programming language |
| Maven | Build and dependency management |
| REST Assured | API test automation |
| JUnit 4 | Test framework |
| Jenkins | CI/CD pipeline |
| Docker | Containerized test execution |
| GitHub Webhooks | Automatic pipeline triggers |
| Slack | Build notifications |

---

## API Under Test

[Fake Store API](https://fakestoreapi.com/) — a free public REST API simulating
an e-commerce store. No authentication required.

Base URI: `https://fakestoreapi.com`

---

## Project Structure

```
jenkins-labs/
├── src/test/
│   ├── java/com/jenkins/labs/
│   │   ├── config/
│   │   │   └── BaseTest.java              # Base URI config shared by all tests
│   │   └── tests/
│   │       ├── GetProductsTest.java       # GET /products (4 tests)
│   │       ├── GetProductByIdTest.java    # GET /products/{id} (5 tests)
│   │       ├── PostProductTest.java       # POST /products (4 tests)
│   │       └── ProductSchemaTest.java     # JSON schema validation (1 test)
│   └── resources/schemas/
│       └── product-schema.json           # Expected product response shape
├── docs/
│   ├── jenkins-setup.md                  # Jenkins installation guide
│   ├── webhook-setup.md                  # GitHub webhook configuration
│   └── slack-setup.md                    # Slack notifications setup
├── Jenkinsfile                           # Declarative CI/CD pipeline
├── Dockerfile                            # Container image for running tests
├── docker-compose.yml                    # Docker Compose for easy test execution
├── pom.xml                               # Maven project config and dependencies
└── README.md
```

---

## Running Tests Locally

### Prerequisites
- Java 11+
- Maven 3.6+

### Run all tests
```bash
mvn clean test
```

### Run a specific test class
```bash
mvn test -Dtest=GetProductsTest
mvn test -Dtest=GetProductByIdTest
mvn test -Dtest=PostProductTest
```

### Generate HTML report
```bash
mvn surefire-report:report site:site -DgenerateReports=false
```
Report is at `target/site/surefire-report.html`.

---

## Running Tests with Docker

### Prerequisites
- Docker Desktop running

### Build and run
```bash
docker-compose up --build
```

### Tear down
```bash
docker-compose down
```

---

## Jenkins Pipeline

The `Jenkinsfile` defines a declarative pipeline with these stages:

```
Checkout → Build → Run API Tests → Archive Artifacts → Publish Report
```

| Stage | What it does |
|---|---|
| Checkout | Pulls latest code from GitHub |
| Build | Compiles the project with `mvn clean compile` |
| Run API Tests | Runs all tests with `mvn test`, publishes JUnit results |
| Archive Artifacts | Saves Surefire XML reports as build artifacts |
| Publish Report | Generates and archives the HTML test report |

After every build, a Slack notification is sent to `#jenkins-builds` with:
- Build status (pass/fail)
- Test counts (total, passed, failed, skipped)
- Direct link to the build and console

### Setting up Jenkins

See [docs/jenkins-setup.md](docs/jenkins-setup.md) for full installation steps.

Quick start with Docker:
```bash
docker run -d --name jenkins \
  -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts
```

### Connecting to GitHub

See [docs/webhook-setup.md](docs/webhook-setup.md) — configure a GitHub webhook
to trigger the pipeline automatically on every push.

### Slack Notifications

See [docs/slack-setup.md](docs/slack-setup.md) — create a Slack bot and connect
it to Jenkins in under 5 minutes.

---

## Test Coverage

| Test Class | Endpoint | Tests | Validations |
|---|---|---|---|
| GetProductsTest | GET /products | 4 | Status 200, non-empty list, required fields, limit param |
| GetProductByIdTest | GET /products/{id} | 5 | Status 200, correct ID, all fields, price > 0, invalid ID |
| PostProductTest | POST /products | 4 | Status 201, new ID returned, echo fields, empty body |
| ProductSchemaTest | GET /products/{id} | 1 | Full JSON schema validation |

Total: 14 tests

---

## Phase Roadmap

- [x] Phase 1 — Maven project setup and API tests
- [x] Phase 3 — Dockerization
- [x] Phase 4 — Jenkins installation and plugin setup
- [x] Phase 5 — Jenkins declarative pipeline
- [x] Phase 6 — GitHub webhook integration
- [x] Phase 7 — Slack notifications
- [x] Phase 8 — Final cleanup and documentation
