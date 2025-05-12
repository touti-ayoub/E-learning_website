
# 📝 Exam and Certificate Microservice

This microservice is part of a larger microservice-based system. It manages the full lifecycle of exams including creation, submission, grading, PDF certificate generation, and email delivery.

---

## 🚀 Features

- 📄 Create exams with PDF attachments
- 📤 Submit exam responses
- 🧮 Grade exams and mark as passed/failed
- 📜 Generate PDF certificates for passed exams
- 📧 Send certificates via email
- 🔍 Retrieve exams by user

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3+**
- **Spring Data JPA**
- **RESTful APIs**
- **Feign Client** (for User Microservice)
- **JavaMailSender** for email services
- **Angular** (for front-end integration)
- **PDF files only** (uploads limited to 10MB)

---

## 🗂️ Project Structure

```
src/
├── controllers        # REST endpoints
├── services           # Business logic
├── entities           # JPA entities
├── repositories       # Spring Data JPA
├── dto                # Data transfer objects
├── client             # Feign client for User microservice
```

---

## 📡 REST API Endpoints

### 🎓 Exam Endpoints `/api/exams`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/` | Create a new exam with a PDF file |
| POST   | `/{id}/submit` | Submit exam response file |
| POST   | `/{id}/grade?score=X` | Grade an exam |
| GET    | `/{id}` | Get exam by ID |
| GET    | `/user/{userId}` | Get all exams by user ID |
| GET    | `/{id}/certificate` | Download certificate for an exam |
| GET    | `/download/{filename}` | Download a file (exam or submission) |
| DELETE | `/{id}` | Delete an exam |

### 📜 Certificate Endpoints `/api/certificates`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/generate/{examId}` | Generate certificate for a passed exam |
| GET    | `/download/{examId}` | Download the generated certificate |
| POST   | `/send/{examId}` | Send certificate by email (requires email body) |

---

## 🧾 Entity Overview

### `Exam`
- `id`, `title`, `description`, `date`, `userId`
- `examFileUrl`, `submittedFileUrl`
- `score`, `passed`, `status` (CREATED, SUBMITTED, GRADED, PASSED, FAILED)
- `certificateGenerated`, `certificateUrl`

### `Certificate`
- `id`, `certificateUrl`, `issuedDate`
- One-to-one link with `Exam`

---

## 📦 File Upload

- Only `.pdf` files allowed
- Max file size: **10MB**
- Files are renamed using UUIDs
- Stored locally in the `/uploads` directory

---

## 📬 Email Notification

- Certificates are sent as attachments using `JavaMailSender`
- Email configuration must be provided in `application.properties`
- Example SMTP setup:
```properties
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🌍 Front-End Integration

### Angular Components (Examples):
- **Create Exam Form**: with file upload
- **Grade Exam Page**: assign scores and auto-trigger certificate generation
- **Certificate Download Button**: appears if exam is passed

---

## 🧪 Example API Usage

```bash
curl -X POST http://localhost:8080/api/exams \
  -F "exam={\"title\":\"Spring Boot Test\",\"userId\":42}" \
  -F "file=@/path/to/exam.pdf"
```

---

## 👥 Feign Client - User Microservice

Used to fetch user details:
- `GET /auth/{id}`
- `GET /auth/username/{email}`
- `GET /auth/all`

---

## 📌 Notes

- The backend ensures validations for file type and size
- Logging and error handling is done using SLF4J
- Grading automatically determines pass/fail based on score ≥ 70%
- Certificate generation is resilient to errors (doesn’t block grading)

---

## 📎 Authors

- Developed by: **Nessim Ayadi**
- Microservice: **Exam & Certificate Service**
- Part of: **Microservice-based Educational Platform**
