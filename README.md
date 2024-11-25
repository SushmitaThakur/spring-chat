# Chat Application

This is a simple chat application API built in Java using the **Spring Boot** Framework. It allows users to create groups, join them, and exchange messages within the groups. The application is structured as a RESTful API, using a MySQL database and is tested using JUnit.

The application supports the following core features:
- **User Management:** Create and list users.
- **Group Management:** Create groups, join groups, and list groups.
- **Messaging:** Send and retrieve messages in groups.

---

## Frameworks and Tools Used

- **Spring Boot:** Used as the primary framework for creating RESTful APIs.
- **Hibernate (JPA):** Manages database interaction with entities and repositories. Relationships between entities such as `@OneToMany` and `@ManyToMany` are used to model the data.
- **MySQL Database:** A database used for development.
- **Docker:** Used for containerizing and running the application along with the database.

---

## Testing Setup

The testing environment uses the following:
- **H2 Database:** The in-memory database is configured for the `test` profile to isolate tests from the development database.
- **JUnit 5:** For creating and running test cases.
- **MockMvc:** Provides a way to test the REST API endpoints by simulating HTTP

---

### Steps to Run
1. **Build the Project with Maven**
   - Open a terminal and navigate to the project directory.
   - Run the following command to build the project:
     ```bash
     mvn clean install
     ```

2. **Run the Application with Docker**
   - In the terminal, execute:
     ```bash
     docker-compose up --build
     ```
   - This will start the application along with a PostgreSQL database in Docker containers.

3. **Access the Application**
   - The application will be available at:
     ```
     http://localhost:8080
     ```
   - Verify the application by accessing the `/health` endpoint:
     ```
     http://localhost:8080/health
     ```

4. **Testing API Endpoints**
   - Use the Postman collection provided in the project folder to test the APIs.

---

## Database Schema

### User Table
| **Column** | **Type**   | **Constraints**          |
|------------|------------|--------------------------|
| id         | BIGINT     | Primary Key              |
| name       | VARCHAR    | Not Null                |
| email      | VARCHAR    | Not Null, Unique         |

### Group Table
| **Column** | **Type**   | **Constraints**          |
|------------|------------|--------------------------|
| id         | BIGINT     | Primary Key              |
| name       | VARCHAR    | Not Null, Unique         |

### Message Table
| **Column** | **Type**   | **Constraints**          |
|------------|------------|--------------------------|
| id         | BIGINT     | Primary Key              |
| group_id   | BIGINT     | Foreign Key to Group     |
| user_id    | BIGINT     | Foreign Key to User      |
| content    | TEXT       | Not Null                |

### Group Participants Table
| **Column** | **Type**   | **Constraints**          |
|------------|------------|--------------------------|
| group_id   | BIGINT     | Foreign Key to `group.id`|
| user_id    | BIGINT     | Foreign Key to `user.id` |

---

## API Endpoints

| Method    | Route                         | Description                     | Request Body                                             | Query Parameters            |
|-----------|-------------------------------|---------------------------------|----------------------------------------------------------|-----------------------------|
| **GET**   | `/user/list`                  | List all users                  | None                                                     | None                        |
| **POST**  | `/user/create`                | Create a new user               | `{ "name": "User Name", "email": "User Email" }`         | None                        |
| **POST**  | `/groups/create`              | Create a new group              | `{ "name": "Group Name" }`                               | None                        |
| **POST**  | `/groups/join/{group_id}`     | Join an existing group          | `{ "user_id": "User ID" }`                               | None                        |
| **GET**   | `/groups/list`                | List all available groups       | None                                                     | `{ "user_id": "User ID"}`   |
| **POST**  | `/messages/send/{group_id}`   | Send a message in a group       | `{ "user_id": "User ID", "message": "Message content" }` | None                        |
| **GET**   | `/messages/list/{group_id}`   | List all messages in a group    | None                                                     | `{ "user_id": "User ID"}`   |
| **GET**   | `/messages/unread/{group_id}` | List unread messages in a group | None                                                     | `{ "user_id": "User ID" }`  |
