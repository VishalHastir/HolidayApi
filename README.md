# **Holiday API - Spring Boot Application**

## **Overview**

This is a **Spring Boot Application** that provides an API to retrieve public holiday information for different
countries. The project is structured into the following modules:

- **`parent`**: The main parent module (Maven Multi-Module Project).
- **`app`**: The core application module containing the Spring Boot API.
- **`functional-tests (FT)`**: The module for functional and integration testing using **Cucumber + RestAssured**.

### **Features**

- Fetch past holidays for a specific country.
- Retrieve the number of holidays for a given year and multiple countries.
- Find common holidays between multiple countries in a given year.

---

## **Project Structure**

```
holiday-api/
│── parent/                     # Parent Module (Maven Multi-Module)
│   ├── pom.xml                  # Parent POM
│
├── app/                         # Application Module
│   ├── src/main/java/com.holiday.api # Spring Boot Application
│   ├── src/main/resources       # Configuration files
│   ├── pom.xml                  # Application-specific dependencies
│
├── functional-tests/            # Functional Tests Module
│   ├── src/test/java/com.holiday.api.cucumber
│   │   ├── cucumber/steps        # Step Definitions for Cucumber
│   │   ├── CucumberSpringConfig  # Cucumber Spring Configuration
│   │   ├── RunCucumberTest       # Cucumber Test Runner
│   ├── src/test/resources/       # Cucumber Feature Files
│   │   ├── holiday_api.feature
│   ├── pom.xml                   # FT-specific dependencies (Cucumber, RestAssured)
│
├── pom.xml                      # Main Parent POM
```

---

## **Prerequisites**

Before running the application, ensure you have:

- **Java 21**
- **Maven 3.6+**
- **Spring Boot 3.4.2**
- **Internet access** (required for fetching holidays from an external API)

---

## **Configuration**

Update the `application.yml` file in the `app` module with the correct API URL.

### **Example (`app/src/main/resources/application.yml`)**

```properties
holiday.api.url=https://example-holiday-api.com
server.port=8084
```

---

## **Running the Application**

### **Step 1: Clone the Repository**

```sh
git clone https://github.com/VishalHastir/HolidayApi.git
cd holiday-api
```

### **Step 2: Build the Application**

Since this is a **multi-module project**, use the following command to build all modules:

```sh
mvn clean install -pl '!functional-tests' -am
```
- **-pl '!functional-test-module'**: Skips the FT module from running tests as the application is not running.
- **-am**: Builds dependencies of the skipped module

### **Step 3: Run the Application**

Navigate to the `app` module and start the Spring Boot application:

```sh
cd app
mvn spring-boot:run
```

The application will now be running at:  
👉 **http://localhost:8084**

---

## **Running Functional Tests (FT Module)**

Functional Tests are written using **Cucumber + RestAssured**.

### **Step 1: Start the Application**

Make sure the `app` module is running before executing tests:

```sh
cd app
mvn spring-boot:run
```

### **Step 2: Run Functional Tests**

Navigate to the `functional-tests` module and execute:

```sh
cd functional-tests
mvn test
```

This will run **Cucumber** feature tests located in:

```
functional-tests/src/test/resources/com.holiday.api.cucumber/holiday_api.feature
```

You can check HTML report in **target/cucumber-report.html**

---

## **API Endpoints**

#### Refer to `countries.json` in `resources/static` to get valid country codes.

### **1. Get Past 3 Holidays**

**Endpoint:**

```
GET /holidays/past/{countryCode}
```

**Example:**

```sh
curl -X GET "http://localhost:8084/holidays/past/US"
```

---

### **2. Get Holidays Count for Multiple Countries**

**Endpoint:**

```
POST /holidays/count
```

**Request Body:**

```json
{
  "year": 2025,
  "countryCodes": [
    "US",
    "NL"
  ]
}
```

**Example:**

```sh
curl -X POST "http://localhost:8084/holidays/count" -H "Content-Type: application/json" -d '{"year": 2025, "countryCodes": ["US", "NL"]}'
```

---

### **3. Get Common Holidays Between Multiple Countries**

**Endpoint:**

```
POST /holidays/common
```

**Request Body:**

```json
{
  "year": 2025,
  "countryCodes": [
    "US",
    "NL",
    "BR"
  ]
}
```

**Example:**

```sh
curl -X POST "http://localhost:8084/holidays/common" -H "Content-Type: application/json" -d '{"year": 2025, "countryCodes": ["US", "NL", "BR"]}'
```

---

## **Deployment**

To package the application into a JAR file:

```sh
mvn package
```

The JAR file will be generated inside the `app/target/` directory. Run it using:

```sh
java -jar app/target/holiday-api-0.0.1-SNAPSHOT.jar
```

## **License**

This project is licensed under the **MIT License**.

---
