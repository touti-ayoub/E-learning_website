# Quiz Microservice

This Spring Boot microservice provides functionality to generate trivia quizzes using the [Open Trivia Database API](https://opentdb.com/), manage quizzes, evaluate user answers, and track user scores.

## Features

- **Generate Trivia Quizzes** dynamically from an external API
- **CRUD Operations** for managing quizzes
- **Quiz Evaluation** logic for scoring user-submitted answers
- **User Score Tracking** for individual quiz attempts
- **Support for Trivia Categories** via a category mapper utility

## Technologies Used

- Java 17+
- Spring Boot
- REST APIs (via `RestTemplate`)
- Open Trivia Database (external API)

## Structure

### `QuizService`
Handles:
- Creating, updating, deleting, and retrieving quizzes
- Associating users with quizzes
- Updating and retrieving user scores

### `TriviaService`
Handles:
- Fetching trivia questions from the Open Trivia Database API
- Dynamically building `Quiz`, `QuizQuestion`, and `QuizResult` entities

### `QuizEvaluationService`
Handles:
- Comparing user answers with correct answers
- Returning a calculated quiz score

## API Endpoints (Planned/Expected)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/quizzes` | Create a new quiz |
| GET    | `/quizzes` | Retrieve all quizzes |
| GET    | `/quizzes/{id}` | Retrieve a specific quiz |
| PUT    | `/quizzes/{id}` | Update quiz details |
| DELETE | `/quizzes/{id}` | Delete a quiz |
| POST   | `/quizzes/{id}/evaluate` | Submit user answers and get a score |
| POST   | `/quizzes/{id}/users/{userId}` | Associate a user with a quiz |
| GET    | `/quizzes/{id}/users/{userId}/score` | Get user's score for a quiz |
| PUT    | `/quizzes/{id}/users/{userId}/score` | Update user's score |

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd <your-repo-directory>
2. Build and run the application
   ./mvnw spring-boot:run
3. Test Trivia Quiz Generation
   Visit: http://localhost:8080/your-endpoint
   Use Swagger/Postman to test endpoints

Notes

  Make sure to define your TriviaCategoryMapper utility to translate category names to Open Trivia DB IDs.
  Quiz data is expected to be persisted using JPA repositorie

