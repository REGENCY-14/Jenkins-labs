# ─────────────────────────────────────────────
# Stage 1: dependency cache
# Pre-download all Maven dependencies so they
# are cached in a separate layer. Rebuilds only
# when pom.xml changes.
# ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-11 AS dependencies

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -q

# ─────────────────────────────────────────────
# Stage 2: test runner
# Copies source code and runs the full test suite.
# ─────────────────────────────────────────────
FROM dependencies AS test-runner

COPY src ./src

CMD ["mvn", "clean", "test"]
