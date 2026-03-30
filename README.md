# api-service-permit-loader

_Microservice for loading permits._

## Getting Started

### Prerequisites

- **Java 25 or higher**
- **Maven**
- **MariaDB**
- **Git**
- **[Dependent Microservices](#dependencies)**

### Installation

1. **Clone the repository:**

   ```bash
   git clone git@github.com:Sundsvallskommun/api-service-permit-loader.git
   cd api-service-permit-loader
   ```
2. **Configure the application:**

   Before running the application, you need to set up configuration settings.
   See [Configuration](#Configuration)

   **Note:** Ensure all required configurations are set; otherwise, the application may fail to start.

3. **Ensure dependent services are running:**

   If this microservice depends on other services, make sure they are up and accessible.
   See [Dependencies](#dependencies) for more details.

4. **Build and run the application:**

   ```bash
   mvn spring-boot:run
   ```

## Dependencies

This microservice depends on the following services:

- **Party**
  - **Purpose:** Provides legal ID lookups by party identifier.
  - **Repository:** [Sundsvallskommun/api-service-party](https://github.com/Sundsvallskommun/api-service-party)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.
- **PartyAssets**
  - **Purpose:** Manages party assets such as permits and their statuses.
  - **Repository:** [Sundsvallskommun/api-service-party-assets](https://github.com/Sundsvallskommun/api-service-party-assets)
  - **Setup Instructions:** Refer to its documentation for installation and configuration steps.

Ensure that these services are running and properly configured before starting this microservice.

## API Documentation

Access the API documentation via Swagger UI:

- **Swagger UI:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

Alternatively, refer to the `openapi.yml` file located in the project's root directory for the OpenAPI specification.

## Usage

### API Endpoints

Refer to the [API Documentation](#api-documentation) for detailed information on available endpoints.

### Example Requests

**Fetch party IDs for all unprocessed records:**

```bash
curl -X POST http://localhost:8080/2281/permits/fetch-party-ids
```

**Create party assets for records with party IDs:**

```bash
curl -X POST http://localhost:8080/2281/permits/create-party-assets
```

### Database

The service reads from and writes to the `procapita_raw` table. Most columns are **read-only** (loaded externally) and only the following columns are writable by the service:

|      Column       | Writable |          Description           |
|-------------------|----------|--------------------------------|
| `personal_number` | No       | Personal number from Procapita |
| `assistance_type` | No       | Type of assistance             |
| `duration`        | No       | Duration of the permit         |
| `start_date`      | No       | Permit start date              |
| `end_date`        | No       | Permit end date                |
| `permit_group`    | No       | FARDTJANST or RIKSFARDTJANST   |
| `party_id`        | Yes      | Set by fetch-party-ids         |
| `party_asset_id`  | Yes      | Set by create-party-assets     |
| `status`          | Yes      | Processing status              |

## Configuration

Configuration is crucial for the application to run successfully. Ensure all necessary settings are configured in
`application.yml`.

### Key Configuration Parameters

- **Server Port:**

  ```yaml
  server:
    port: 8080
  ```
- **Database Settings (MariaDB):**

  ```yaml
  spring:
    datasource:
      url: jdbc:mariadb://${DB_HOST}:${DB_PORT}/${DB_NAME}
      username: ${DB_USERNAME}
      password: ${DB_PASSWORD}
  ```

  **`.env` file:**

  ```properties
  spring.datasource.url=jdbc:mariadb://localhost:3306/permit_loader
  spring.datasource.username=root
  spring.datasource.password=your_password
  ```
- **External Service URLs:**

  ```yaml
  integration:
    service:
      url: http://dependency_service_url
      oauth2:
        client-id: some-client-id
        client-secret: some-client-secret

  service:
    oauth2:
      token-url: http://dependecy_service_token_url
  ```

### Database Initialization

The project is set up with [Flyway](https://github.com/flyway/flyway) for database migrations. Flyway is disabled by
default so you will have to enable it to automatically populate the database schema upon application startup.

```yaml
spring:
  flyway:
    enabled: true
```

- **No additional setup is required** for database initialization, as long as the database connection settings are
  correctly configured.

### Additional Notes

- **Application Profiles:**

  Use Spring profiles (`dev`, `prod`, etc.) to manage different configurations for different environments.

- **Logging Configuration:**

  Adjust logging levels if necessary.

## Contributing

Contributions are welcome! Please
see [CONTRIBUTING.md](https://github.com/Sundsvallskommun/.github/blob/main/.github/CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the [MIT License](LICENSE).

## Code status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-permit-loader&metric=alert_status)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-permit-loader)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-permit-loader&metric=reliability_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-permit-loader)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-permit-loader&metric=security_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-permit-loader)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-permit-loader&metric=sqale_rating)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-permit-loader)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-permit-loader&metric=vulnerabilities)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-permit-loader)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Sundsvallskommun_api-service-permit-loader&metric=bugs)](https://sonarcloud.io/summary/overall?id=Sundsvallskommun_api-service-permit-loader)

---

© 2026 Sundsvalls kommun
