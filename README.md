# Holiday API - Spring Boot Application

## Overview

This is a Spring Boot application that provides an API to retrieve public holiday information for different countries. It supports the following functionalities:

- Fetch past holidays for a specific country
- Retrieve the number of holidays for a given year and multiple countries
- Find common holidays between multiple countries in a given year

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java 17** or later
- **Maven 3.6+**
- **Spring Boot 3.4.2**
- **Internet access** (required to fetch holidays from an external API)

## Configuration

This application requires an external API for fetching holiday data. Update the `application.properties` file with the correct API URL.

### Example `application.properties`:

```properties
holiday.api.url=https://example-holiday-api.com
server.port=8080
```

## Running the Application

### 1. Clone the repository

```sh
git clone https://github.com/your-repository/holiday-api.git
cd holiday-api
```

### 2. Build the application

```sh
mvn clean install
```

### 3. Run the application

```sh
mvn spring-boot:run
```

The application should now be running on [**http://localhost:8080**](http://localhost:8080).

## API Endpoints

#### Refer to `countries.json` in `resources/static` to get valid country codes.

### 1. Get Past Holidays

**Endpoint:**

```
GET /holidays/past/{countryCode}
```

**Description:** Retrieves the last three holidays for the specified country.

**Example:**

```sh
curl -X GET "http://localhost:8080/holidays/past/US"
```

### 2. Get Holidays Count for a Year

**Endpoint:**

```
POST /holidays/count
```

**Description:** Retrieves the number of holidays for a given year and multiple countries.

**Request Body:**

```json
{
  "year": 2025,
  "countryCodes": ["US", "NL"]
}
```

**Example:**

```sh
curl -X POST "http://localhost:8080/holidays/count" -H "Content-Type: application/json" -d '{"year": 2025, "countryCodes": ["US", "NL"]}'
```

### 3. Get Common Holidays between Multiple Countries

**Endpoint:**

```
POST /holidays/common
```

**Description:** Retrieves holidays that are common between multiple countries in a given year.

**Request Body:**

```json
{
  "year": 2025,
  "countryCodes": ["US", "NL", "BR"]
}
```

**Example:**

```sh
curl -X POST "http://localhost:8080/holidays/common" -H "Content-Type: application/json" -d '{"year": 2025, "countryCodes": ["US", "NL", "BR"]}'
```

## Running Tests

This application includes unit tests. To run the tests, use:

```sh
mvn test
```

## Deployment

To package the application as a JAR file, run:

```sh
mvn package
```

The generated JAR file will be in the `target/` directory. Run it with:

```sh
java -jar target/holiday-api-0.0.1-SNAPSHOT.jar
```

## Contributing

If you wish to contribute to this project, feel free to submit a pull request.

## License

This project is licensed under the MIT License.

