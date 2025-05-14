# E-Learning Website


A comprehensive E-learning platform designed to provide an interactive and engaging educational experience for students and instructors.
![infinity 2](https://github.com/user-attachments/assets/ac4b24f1-0297-4f43-991a-abab79a78aa2)


## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Screenshots](#screenshots)
- [Roadmap](#roadmap)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## 🌟 Overview

This E-learning platform serves as a comprehensive solution for online education, connecting students with instructors through a feature-rich, responsive interface. The system supports course creation, student enrollment, progress tracking, assessments, and interactive learning materials.

## ✨ Features

### For Students
- User-friendly dashboard to track enrolled courses and progress
- Interactive course content with videos, quizzes, and assignments
- Discussion forums for student-instructor and peer-to-peer interactions
- Progress tracking and achievement badges
- Course ratings and reviews
- Certificate generation upon course completion

### For Instructors
- Course creation and management tools
- Analytics dashboard to monitor student progress
- Assignment creation and grading system
- Live session scheduling and management
- Revenue tracking for paid courses

### Administrative Features
- User management (students, instructors, admins)
- Content moderation
- System analytics and reporting
- Payment gateway integration for premium courses

## 🛠️ Tech Stack

### Frontend
- **Angular**: For building the user interface
- **SCSS/CSS**: For styling components

### Backend
- **Spring Boot (Java)**: For server-side logic
- **Spring Security**: For authentication and authorization
- **JPA/Hibernate**: For database operations
- **RESTful API**: For client-server communication

### Database
- **MySQL**: For data storage

## 📁 Project Structure for one gestion

```
E-learning_website/
├── frontend/                  # React/TypeScript frontend application
│   ├── public/                # Static files
│   ├── src/                   # Source files
│   │   ├── components/        # Reusable UI components
│   │   ├── pages/             # Application pages
│   │   ├── services/          # API service integrations
│   │   ├── store/             # Redux store configuration
│   │   ├── styles/            # SCSS/CSS styles
│   │   └── utils/             # Utility functions
│   ├── package.json           # Frontend dependencies
│   └── tsconfig.json          # TypeScript configuration
│
├── backend/                   # Java Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/          # Java source code
│   │   │   │   ├── controllers/  # REST controllers
│   │   │   │   ├── models/       # Data models
│   │   │   │   ├── repositories/ # Data access layer
│   │   │   │   ├── services/     # Business logic
│   │   │   │   └── config/       # Application configuration
│   │   │   └── resources/     # Application resources
│   │   └── test/              # Test files
│   ├── pom.xml                # Project dependencies
│   └── application.properties # Application settings
│
├── docs/                      # Documentation files
├── .gitignore                 # Git ignore file
├── docker-compose.yml         # Docker composition
└── README.md                  # Project readme
```

## 🚀 Getting Started

### Prerequisites

- Node.js (v14.0.0 or later)
- npm (v6.0.0 or later)
- Java Development Kit (JDK) 11 or later
- Maven
- MySQL/PostgreSQL database
- Angukar (16)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/touti-ayoub/E-learning_website.git
   cd E-learning_website
   ```

2. **Set up the backend**
   ```bash
   cd backend
   mvn clean install
   ```

3. **Configure the database**
   - Create a database for the application
   - Update the database configuration in `application.properties` file

4. **Run the backend**
   ```bash
   mvn spring-boot:run
   ```

5. **Set up the frontend**
   ```bash
   cd ../frontend
   npm install
   ```

6. **Run the frontend**
   ```bash
   npm start
   ```

7. **Access the application**
   - The frontend should be running at `http://localhost:3000`
   - The backend API will be available at `http://localhost:8088`

## 💻 Usage

### Student Flow
1. Register/Login to the platform
2. Browse available courses
3. Enroll in courses (free or paid)
4. Access course materials and complete assignments
5. Participate in discussions
6. Track progress and earn certificates

### Instructor Flow
1. Register/Login as an instructor
2. Create and publish courses
3. Upload learning materials and assessments
4. Interact with enrolled students
5. Monitor student performance
6. Receive payments for premium courses

## 📝 API Documentation

Our RESTful API provides endpoints for all platform functionalities. The detailed API documentation is available at `/api/docs` when running the application or in the `docs/api` directory.

### Key Endpoints

- `/api/auth` - Authentication endpoints
- `/api/users` - User management
- `/api/courses` - Course operations
- `/api/enrollments` - Course enrollment
- `/api/assignments` - Assignment submission and grading
- `/api/discussions` - Forum discussions
- `/api/payments` - Payment processing

## 📸 Screenshots
![3](https://github.com/user-attachments/assets/0d23e786-a07e-4340-ae67-2fbf10a6e191)



## 🤝 Contributing

Contributions to improve the platform are welcome. Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

Please make sure to update tests as appropriate and adhere to the coding standards defined in the project.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.


---

Made with ❤️
```

#Esprit_school_of_engineering.
