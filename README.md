# Authentication Microservice

This is a standalone authentication microservice built with Spring Boot. It provides a robust foundation for handling user registration, login, and authorization using JSON Web Tokens (JWT).

## Features

- **User Registration**: Securely register new users.
- **User Login**: Authenticate users with username and password.
- **Stateless Authentication**: Uses JWT for stateless, scalable authentication.
- **Password Security**: Passwords are never stored in plaintext; they are securely hashed using BCrypt.
- **Protected Endpoints**: Easily protect API endpoints, allowing access only to authenticated users.
- **Role-Based Access**: Foundation for role-based access control is included in the JWT claims.

## Dependencies

- **Java 21**
- **Spring Boot 3**
- **Spring Security 6**
- **Spring Data JPA**
- **PostgreSQL**: Database for storing user information.
- **JWT (Java JWT - jjwt)**: For token generation and validation.
- **Lombok**: To reduce boilerplate code.
- **MapStruct**: For efficient DTO-to-Entity mapping.
- **Gradle**: For dependency management and building.

## Prerequisites

- JDK 21 or later
- A running PostgreSQL instance

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/ayushsrawat/auth.git
    cd auth
    ```

2.  **Configure the Database:**
    Open `src/main/resources/application.properties` and update the `spring.datasource` properties to point to your PostgreSQL instance:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/auth
    spring.datasource.username=your_db_user
    spring.datasource.password=your_db_password
    ```

3.  **Configure the JWT Secret :**
    This project is configured to use an environment variable for the JWT secret key for security. **Do not** store the secret in the `application.properties` file.

    Set the environment variable in your operating system:
    - **Linux/macOS:**
      ```bash
      export JWT_SECRET_KEY="$(openssl rand -base64 32)"
      ```
4.  **Build the project:**
    Use the Gradle wrapper to build the application.
    ```bash
    ./gradlew build
    ```

5.  **Run the application:**
    For development, you can run the application directly using the Spring Boot Gradle plugin's `bootRun` task. This will build and run the application without creating a separate JAR file.
    ```bash
    ./gradlew bootRun
    ```
    Alternatively, to run the already built executable JAR (e.g., for deployment):
    ```bash
    java -jar build/libs/auth-0.0.1-SNAPSHOT.jar
    ```
    The application will start on port `8181` by default.

## API Endpoints

### Authentication

#### `POST /api/v1/auth/register`

Registers a new user.

**Request Body:**
```json
{
  "username": "newuser",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User",
  "dob": "1990-01-01",
  "phone": "1234567890",
  "email": "newuser@example.com",
  "address": "123 Main St"
}
```

**Success Response (200 OK):**
```json
{
    "id": 1,
    "username": "newuser",
    "firstName": "Test",
    "lastName": "User",
    // ... other fields
}
```

#### `POST /api/v1/auth/login`

Authenticates a user and returns a JWT.

**Request Body:**
```json
{
  "username": "newuser",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
    "username": "newuser",
    "token": "ey......"
}
```

## Run Tests
```bash
./gradlew test
```
