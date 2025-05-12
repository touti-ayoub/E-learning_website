
# 🎓 E-Learning Website

## 📖 Overview

The E-Learning Website is a comprehensive platform designed to facilitate online education.
It allows instructors to create and manage courses, while students can enroll, learn, and track their progress.
The platform is built using PHP and MySQL, ensuring a robust and scalable solution for educational needs.

## 🛠️ Technologies Used

- **Frontend**: HTML5, CSS3, JavaScript
- **Backend**: PHP
- **Database**: MySQL
- **Styling Framework**: Bootstrap
- **Version Control**: Git

## 🚀 Features

- User Authentication: Secure login and registration for students and instructors.
- Course Management: Instructors can create, update, and delete courses.
- Enrollment System: Students can enroll in courses and access materials.
- Progress Tracking: Monitor course completion status.
- Responsive Design: Accessible on various devices.

## 📁 Project Structure

```
E-learning_website/
├── assets/
│   ├── css/
│   ├── js/
│   └── images/
├── includes/
├── pages/
├── database/
│   └── elearning_db.sql
├── index.php
├── config.php
└── README.md
```

## ⚙️ Installation

1. **Clone the Repository**

   ```bash
   git clone https://github.com/touti-ayoub/E-learning_website.git
   ```

2. **Set Up the Database**

   - Create a MySQL database named `elearning_db`.
   - Import the `elearning_db.sql` file located in the `database/` directory.

3. **Configure the Application**

   - Open `config.php` and update the database credentials:

     ```php
     define('DB_SERVER', 'localhost');
     define('DB_USERNAME', 'your_username');
     define('DB_PASSWORD', 'your_password');
     define('DB_NAME', 'elearning_db');
     ```

4. **Run the Application**

   - Start your local server (e.g., XAMPP, WAMP).
   - Navigate to `http://localhost/E-learning_website/` in your web browser.

## 🧑‍💼 User Roles

- **Student**
  - Browse available courses.
  - Enroll in courses.
  - Access course materials and track progress.

- **Instructor**
  - Create and manage courses.
  - Upload course content.
  - Monitor student enrollments.

## 📸 Screenshots

*Include relevant screenshots of the homepage, course page, and dashboard here.*

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch: `git checkout -b feature/YourFeature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/YourFeature`
5. Open a pull request.

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## 📬 Contact

For any inquiries or feedback, please contact [your email address].

---

*Happy Learning!*
