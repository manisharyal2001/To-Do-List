# 📝 To-Do List Application

A secure, scalable Java-based To-Do List application with **MongoDB**, **JWT-based authentication**, **Redis token management**, and **asynchronous email notifications**, built using **Vert.X**.

![Landing Page](src/images/login.png)  
*Figure 1: Landing page with registration and login options*

---

## 🚀 Features

- 🔐 Secure user authentication using JWT and Redis
- ✉️ Asynchronous email notifications (registration, password reset, reminders)
- 📝 Full CRUD operations for to-do tasks
- 🔄 Token refresh mechanism for session management
- ⏰ Task reminders sent via email
- 📊 Paginated, filterable, and sortable task views

---

## 🛠 Tech Stack

| Layer         | Technology           |
|---------------|----------------------|
| **Backend**   | Java 17, Spring Boot |
| **Database**  | MongoDB              |
| **Auth**      | JWT, Redis           |
| **Email**     | Spring Mail (Async)  |
| **API**       | RESTful Design       |

---

## 📸 Screenshots

### 🔐 Registration Page
![Register](src/images/ssregister.png)  
*Figure 2: User registration screen*

### 📋 Task Management
![Tasks](src/images/sstask.png)  
*Figure 3: Add or edit tasks*

### 🗃️ MongoDB Collections
![Users Collection](src/images/mongousers.png)  
![Tasks Collection](src/images/mongotask.png)  
*Figure 4: MongoDB document structure for users and tasks*

---

### ✅ Prerequisites

- Java 17+
- MongoDB 5.0+
- Redis 6.0+
- SMTP credentials (or [MailHog](https://github.com/mailhog/MailHog) for local testing)

---

### 📦 Installation

```bash
git clone https://github.com/manisharyal2001/To-Do-List.git
cd To-Do-List
Copy the example config and update credentials:

bash
Copy
Edit
cp src/main/resources/application.example.properties src/main/resources/application.properties
Update your application.properties with MongoDB, Redis, and SMTP credentials.

▶️ Run the Application
bash
Copy
Edit
./mvnw spring-boot:run
📘 API Documentation
🔐 Authentication
Endpoint	Method	Description
/api/auth/register	POST	Register new user
/api/auth/login	POST	Authenticate and get JWT
/api/auth/refresh	POST	Refresh JWT token
/api/auth/logout	POST	Logout and invalidate JWT
/api/auth/password-reset	POST	Initiate password reset
/api/auth/password-reset/confirm	POST	Complete password reset

📝 Task Management
Endpoint	Method	Description
/api/tasks	GET	Retrieve all tasks (paginated)
/api/tasks	POST	Create new task
/api/tasks/{id}	GET	Get task by ID
/api/tasks/{id}	PUT	Update task by ID
/api/tasks/{id}	DELETE	Delete task by ID
/api/tasks/{id}/complete	PATCH	Mark task as complete
/api/tasks/{id}/incomplete	PATCH	Mark task as incomplete

⚙️ Configuration
In src/main/resources/application.properties:

properties
Copy
Edit
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/tododb

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# JWT
jwt.secret=your-secret-key
jwt.expiration.ms=86400000 # 24 hours

# Email
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=user@example.com
spring.mail.password=password
🧪 Running Tests
bash
Copy
Edit
./mvnw test
🚀 Deployment
Build the JAR
bash
Copy
Edit
./mvnw clean package
Run with Production Profile
bash
Copy
Edit
java -jar target/todo-list-app.jar --spring.profiles.active=prod
🤝 Contributing
Pull requests are welcome!
For major changes, please open an issue first to discuss what you’d like to improve or add.

📄 License
This project is licensed under the MIT License. See the LICENSE file for details.

yaml
Copy
Edit

