# 📄 Exam Management & Certificate Microservice

## 📌 Description

This microservice is part of a distributed system for **managing exams** and **generating certificates**. It is integrated with:

- 🎓 `Frontoffice` for students
- 👨‍🏫 `Backoffice` for teachers
- 🔍 **Eureka** for service discovery
- 🌐 **API Gateway** for routing and security

## 🛠️ Technologies Used

- Java 17 & Spring Boot 3
- Spring Data JPA & Hibernate
- Spring Cloud Eureka (Client/Server)
- Spring Cloud Gateway
- Spring Mail (Gmail SMTP)
- Feign Client (User Service)
- MySQL
- Angular (Front & Backoffice)
- PDF Generation

## 🧩 Project Structure

```
exam-microservice/
├── controllers/
├── entities/
├── services/
├── repositories/
├── client/
├── dto/
├── uploads/                # PDF storage
├── resources/
│   └── application.properties
├── frontend/
│   ├── frontoffice/        # Student interface (Angular)
│   └── backoffice/         # Teacher interface (Angular)
```

## 🚀 Features

- 📝 Create, submit, and grade exams.
- 🧮 Auto-detect pass/fail status.
- 🏆 Generate PDF certificate for passed exams.
- 📩 Send certificates by email.
- 📂 File upload and secure download.
- 🌐 Access endpoints via API Gateway.

## 💻 User Interfaces

### 🎓 Frontoffice (Student)
- Upload exam submission as PDF.
- View exam results and statuses.
- Download or receive certificate by email.

### 👨‍🏫 Backoffice (Teacher)
- View submitted exams.
- Assign grades and trigger certificate generation.
- Download and email certificates.

## 🧠 Eureka & Gateway Integration

### `application.properties`

```properties
spring.application.name=microservice4
server.port=8050

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true

# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/user?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Email Configuration (use environment variables in production!)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=nessimayadi4@gmail.com
spring.mail.password=YOUR_APP_PASSWORD_HERE
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# File Upload Limits
file.upload-dir=uploads/exams
file.certificate-dir=certificates
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

⚠️ **Security Tip**: Never hardcode credentials. Use environment variables or external config.

## 📡 API Endpoints (via Gateway)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/exams` | Create a new exam |
| POST   | `/api/exams/{id}/submit` | Submit an exam |
| POST   | `/api/exams/{id}/grade` | Grade an exam |
| GET    | `/api/exams/user/{userId}` | Get exams for a student |
| GET    | `/api/exams/{id}/certificate` | Download certificate |
| GET    | `/api/certificates/generate/{id}` | Generate certificate manually |
| POST   | `/api/certificates/send/{id}` | Send certificate by email |
| GET    | `/api/exams/download/{filename}` | Download PDF file |

## 📥 Example Request

```bash
curl -X POST http://localhost:8080/api/exams -F "exam={\"title\":\"Java Basics\",\"description\":\"Intro exam\",\"userId\":2}" -F "file=@/path/to/exam.pdf"
```

## 🔒 Validation Rules

- ✅ Only `.pdf` files allowed
- ⛔ Max file size: 10MB
- 🧠 State validation before grading or submission
- 🔐 Path cleaning & secure downloads

## 📁 File Storage

All PDFs (exam and certificate) are stored locally in `uploads/` and `certificates/` folders. Ensure these exist and are writable.

## 📝 License

Open-source under MIT License.
