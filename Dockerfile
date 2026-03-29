# Phase 1 — basic image to run Maven tests
# Full Jenkins integration will be added in a later phase

FROM maven:3.9.6-eclipse-temurin-11

WORKDIR /app

# Copy project files
COPY pom.xml .
COPY src ./src

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -q

# Default command: run all tests
CMD ["mvn", "test"]
