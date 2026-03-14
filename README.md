# Devin Weekend Lab

Learning Devin AI agent experiments.

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.x**
- **Maven** (build tool)
- **Lombok** (boilerplate reduction)
- **JUnit 5** (testing)

## Planned Experiments

- AI text summarizer
- Simple REST API
- Data analysis scripts

## Getting Started

### Prerequisites

- Java 17+ installed
- Maven 3.8+ installed (or use the Maven wrapper)

### Build & Run

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

The application starts on [http://localhost:8080](http://localhost:8080).

### Run Tests

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/devinlab/
│   │   ├── DevinLabApplication.java   # Spring Boot entry point
│   │   ├── api/                       # REST API controllers
│   │   ├── summarizer/                # AI text summarizer module
│   │   └── analysis/                  # Data analysis module
│   └── resources/
│       └── application.properties     # Application configuration
└── test/
    └── java/com/devinlab/            # Unit & integration tests
```

## License

This project is licensed under the GNU Affero General Public License v3.0 - see the [LICENSE](LICENSE) file for details.
