# Store Application

**Capstone Project** for a **Store Application** to manage inventory, orders, and user authentication.

## Features
- User registration & login
- Product management (add, update, delete)
- Order management


## Prerequisites

- **Java 17+**
- **Maven**
- **MySQL**
- **IDE** (IntelliJ IDEA, Eclipse, etc.)

## Database Schema

```properties
CREATE DATABASE IF NOT EXISTS store;
USE store;
```

## Setup Process

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/store-application.git
cd store-application
```

### 2. Configure the Database

- **MySQL**:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/store_db
  spring.datasource.username=root
  spring.datasource.password=password
  ```


### 3. Build the Project

#### With Maven:
```bash
mvn clean install
```


### 4. Run the Application

#### With Maven:
```bash
mvn spring-boot:run
```


The application will be available at [http://localhost:8080](http://localhost:8080).

- **Swagger URL**: Point to where the Swagger UI can be accessed by http://localhost:8080/swagger-ui/index.html.


## Testing

Run all tests:

#### With Maven:
```bash
mvn test
```



## Troubleshooting

1. **Public Key Retrieval is not allowed**:  
   Add `allowPublicKeyRetrieval=true` to your MySQL URL.

2. **Port already in use**:  
   Change port in `application.properties`:
   ```properties
   server.port=8081
   ```

lights the key setup and usage details. Let me know if you need more adjustments!