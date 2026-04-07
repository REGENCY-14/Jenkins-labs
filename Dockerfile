# =============================================================================
# Dockerfile — Jenkins-labs API Test Runner
# =============================================================================
# Builds a container image that compiles and runs the REST Assured test suite.
# Uses a two-stage build to cache Maven dependencies separately from source code,
# so rebuilds are fast when only test files change.
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: dependency cache
# -----------------------------------------------------------------------------
# Uses the official Maven image with Eclipse Temurin JDK 11.
# Only copies pom.xml first so this layer is cached by Docker and skipped
# on rebuilds unless pom.xml changes (i.e. dependencies are added/removed).
# -----------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-11 AS dependencies

WORKDIR /app

# Copy only the POM to resolve and cache all dependencies
COPY pom.xml .

# Download all dependencies offline — cached as a separate Docker layer
RUN mvn dependency:go-offline -q

# -----------------------------------------------------------------------------
# Stage 2: test runner
# -----------------------------------------------------------------------------
# Builds on top of the cached dependency layer.
# Copies the test source code and runs the full test suite.
# -----------------------------------------------------------------------------
FROM dependencies AS test-runner

# Copy test source files and resources into the container
COPY src ./src

# Default command: clean previous build output and run all tests
CMD ["mvn", "clean", "test"]
