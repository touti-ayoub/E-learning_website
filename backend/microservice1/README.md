# Microservice Documentation

## CourseService Methods

1. **findAll()**: Retrieves a list of all courses.

   - **Endpoint**: `GET /courses`
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "name": "Course Name",
         "description": "Course Description",
         "duration": "10 hours",
         "instructorId": 1,
         "category": "Category Name"
       }
     ]
     ```

2. **findById(Long id)**: Finds a course by its ID.

   - **Endpoint**: `GET /courses/{id}`
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "Course Name",
       "description": "Course Description",
       "duration": "10 hours",
       "instructorId": 1,
       "category": "Category Name"
     }
     ```

3. **save(Course course)**: Saves a new course.

   - **Endpoint**: `POST /courses`
   - **Request Body**:
     ```json
     {
       "title": "Course Name",
       "description": "Course Description",
       "coverImage": "image_url",
       "category": "Category Name",
       "status": "DRAFT",
       "type": "CourseType",
       "price": 100.0,
       "duration": 10,
       "language": "English",
       "level": "Beginner",
       "estimatedCompletionTime": 5,
       "targetAudience": "Students",
       "isAutomated": false,
       "difficultyScore": 0.5,
       "engagementScore": 0.8,
       "completionRate": 0.75,
       "recommendationTags": "tag1, tag2",
       "aiGeneratedTags": ["tag1", "tag2"],
       "aiGeneratedSummary": "AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill1": 0.5 }
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "title": "Course Name",
       "description": "Course Description",
       "coverImage": "image_url",
       "category": "Category Name",
       "status": "DRAFT",
       "type": "CourseType",
       "price": 100.0,
       "duration": 10,
       "language": "English",
       "level": "Beginner",
       "estimatedCompletionTime": 5,
       "targetAudience": "Students",
       "isAutomated": false,
       "difficultyScore": 0.5,
       "engagementScore": 0.8,
       "completionRate": 0.75,
       "recommendationTags": "tag1, tag2",
       "aiGeneratedTags": ["tag1", "tag2"],
       "aiGeneratedSummary": "AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill1": 0.5 }
     }
     ```

4. **update(Course course)**: Updates an existing course.

   - **Endpoint**: `PUT /courses/{id}`
   - **Request Body**:
     ```json
     {
       "title": "Updated Course Name",
       "description": "Updated Description",
       "coverImage": "updated_image_url",
       "category": "Updated Category",
       "status": "PUBLISHED",
       "type": "CourseType",
       "price": 120.0,
       "duration": 12,
       "language": "English",
       "level": "Intermediate",
       "estimatedCompletionTime": 6,
       "targetAudience": "Professionals",
       "isAutomated": true,
       "difficultyScore": 0.7,
       "engagementScore": 0.9,
       "completionRate": 0.85,
       "recommendationTags": "tag3, tag4",
       "aiGeneratedTags": ["tag3", "tag4"],
       "aiGeneratedSummary": "Updated AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill2": 0.6 }
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "title": "Updated Course Name",
       "description": "Updated Description",
       "coverImage": "updated_image_url",
       "category": "Updated Category",
       "status": "PUBLISHED",
       "type": "CourseType",
       "price": 120.0,
       "duration": 12,
       "language": "English",
       "level": "Intermediate",
       "estimatedCompletionTime": 6,
       "targetAudience": "Professionals",
       "isAutomated": true,
       "difficultyScore": 0.7,
       "engagementScore": 0.9,
       "completionRate": 0.85,
       "recommendationTags": "tag3, tag4",
       "aiGeneratedTags": ["tag3", "tag4"],
       "aiGeneratedSummary": "Updated AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill2": 0.6 }
     }
     ```

5. **deleteById(Long id)**: Deletes a course by its ID.

   - **Endpoint**: `DELETE /courses/{id}`
   - **Response**: `204 No Content`

6. **createAutomatedCourse(Course course)**: Creates an automated course.

   - **Endpoint**: `POST /courses/automated`
   - **Request Body**:
     ```json
     {
       "name": "Automated Course Name",
       "description": "Automated Course Description",
       "duration": "8 hours",
       "instructorId": 1,
       "category": "Automated Category",
       "coverImage": "automated_image_url",
       "price": 80.0,
       "language": "English",
       "level": "Beginner",
       "estimatedCompletionTime": 4,
       "targetAudience": "Everyone",
       "isAutomated": true,
       "difficultyScore": 0.5,
       "engagementScore": 0.7,
       "completionRate": 0.6,
       "recommendationTags": "tag5, tag6",
       "aiGeneratedTags": ["tag5", "tag6"],
       "aiGeneratedSummary": "Automated AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill3": 0.4 }
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "Automated Course Name",
       "description": "Automated Course Description",
       "duration": "8 hours",
       "instructorId": 1,
       "category": "Automated Category",
       "coverImage": "automated_image_url",
       "price": 80.0,
       "language": "English",
       "level": "Beginner",
       "estimatedCompletionTime": 4,
       "targetAudience": "Everyone",
       "isAutomated": true,
       "difficultyScore": 0.5,
       "engagementScore": 0.7,
       "completionRate": 0.6,
       "recommendationTags": "tag5, tag6",
       "aiGeneratedTags": ["tag5", "tag6"],
       "aiGeneratedSummary": "Automated AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill3": 0.4 }
     }
     ```

7. **getCoursesByInstructor(Long instructorId)**: Retrieves courses taught by a specific instructor.

   - **Endpoint**: `GET /courses/instructor/{instructorId}`
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "name": "Course Name",
         "description": "Course Description",
         "duration": "10 hours",
         "instructorId": 1,
         "category": "Category Name"
       }
     ]
     ```

8. **createCourse(Course course, Long instructorId)**: Creates a course with a specified instructor.

   - **Endpoint**: `POST /courses/instructor/{instructorId}`
   - **Request Body**:
     ```json
     {
       "name": "Course Name",
       "description": "Course Description",
       "duration": "10 hours",
       "category": "Category Name",
       "coverImage": "image_url",
       "price": 100.0,
       "language": "English",
       "level": "Beginner",
       "estimatedCompletionTime": 5,
       "targetAudience": "Students",
       "isAutomated": false,
       "difficultyScore": 0.5,
       "engagementScore": 0.8,
       "completionRate": 0.75,
       "recommendationTags": "tag1, tag2",
       "aiGeneratedTags": ["tag1", "tag2"],
       "aiGeneratedSummary": "AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill1": 0.5 }
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "Course Name",
       "description": "Course Description",
       "duration": "10 hours",
       "instructorId": 1,
       "category": "Category Name",
       "coverImage": "image_url",
       "price": 100.0,
       "language": "English",
       "level": "Beginner",
       "estimatedCompletionTime": 5,
       "targetAudience": "Students",
       "isAutomated": false,
       "difficultyScore": 0.5,
       "engagementScore": 0.8,
       "completionRate": 0.75,
       "recommendationTags": "tag1, tag2",
       "aiGeneratedTags": ["tag1", "tag2"],
       "aiGeneratedSummary": "AI generated summary",
       "learningObjectives": [],
       "prerequisites": [],
       "skillWeights": { "skill1": 0.5 }
     }
     ```

9. **getRecommendedCourses(Long userId)**: Retrieves recommended courses for a user.

   - **Endpoint**: `GET /courses/recommended/{userId}`
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "name": "Recommended Course Name",
         "description": "Recommended Course Description",
         "duration": "10 hours",
         "instructorId": 1,
         "category": "Recommended Category"
       }
     ]
     ```

10. **publishCourse(Long id)**: Publishes a course by its ID.

- **Endpoint**: `POST /courses/{id}/publish`
- **Response**:
  ```json
  {
    "id": 1,
    "name": "Published Course Name",
    "description": "Published Course Description",
    "duration": "10 hours",
    "instructorId": 1,
    "category": "Published Category"
  }
  ```

11. **enrollUser(Long courseId, Long userId)**: Enrolls a user in a course.

- **Endpoint**: `POST /courses/{courseId}/enroll`
- **Request Body**:
  ```json
  { "userId": 1 }
  ```
- **Response**:
  ```json
  { "success": true }
  ```

12. **getEnrolledUsers(Long courseId)**: Retrieves users enrolled in a specific course.

- **Endpoint**: `GET /courses/{courseId}/enrolled`
- **Response**:
  ```json
  [{ "id": 1, "name": "User Name", "email": "user@example.com" }]
  ```

13. **updateCourseMetrics(Long id)**: Updates metrics for a course.

- **Endpoint**: `POST /courses/{id}/metrics`
- **Request Body**:
  ```json
  { "metrics": { "views": 100, "enrollments": 50 } }
  ```
- **Response**:
  ```json
  { "success": true }
  ```

14. **getOverviewStats()**: Retrieves overview statistics for courses.

- **Endpoint**: `GET /courses/stats`
- **Response**:
  ```json
  { "totalCourses": 100, "totalEnrollments": 500, "averageRating": 4.5 }
  ```

15. **getCoursesByCategory()**: Retrieves courses categorized by type.

- **Endpoint**: `GET /courses/category`
- **Response**:
  ```json
  [
    { "category": "Category Name", "courses": [{ "id": 1, "name": "Course Name", "description": "Course Description", ... }] }
  ]
  ```

16. **getRecentCourses(int limit)**: Retrieves a limited number of recent courses.

- **Endpoint**: `GET /courses/recent?limit={limit}`
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "Recent Course Name",
      "description": "Recent Course Description",
      "duration": "10 hours",
      "instructorId": 1,
      "category": "Recent Category"
    }
  ]
  ```

17. **getTopPerformingCourses(int limit)**: Retrieves a limited number of top-performing courses.

- **Endpoint**: `GET /courses/top?limit={limit}`
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "Top Course Name",
      "description": "Top Course Description",
      "duration": "10 hours",
      "instructorId": 1,
      "category": "Top Category"
    }
  ]
  ```

## IntelligentCourseService Methods

1. **getPersonalizedRecommendations(Long userId, int limit)**: Generates personalized course recommendations based on user's learning history and preferences.

   - **Endpoint**: `GET /courses/recommendations/{userId}?limit={limit}`
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "name": "Recommended Course Name",
         "description": "Recommended Course Description",
         "duration": "10 hours",
         "instructorId": 1,
         "category": "Recommended Category"
       }
     ]
     ```

2. **enrichCourseWithAI(Course course)**: Analyzes course content and generates AI tags and summary.

   - **Endpoint**: `POST /courses/enrich`
   - **Request Body**:
     ```json
     { "courseId": 1 }
     ```
   - **Response**:
     ```json
     { "success": true }
     ```

3. **generateAdaptiveLearningPath(Long userId, Long courseId)**: Generates adaptive learning path based on student's progress.

   - **Endpoint**: `GET /courses/{courseId}/adaptive-path/{userId}`
   - **Response**:
     ```json
     [{ "objective": "Objective Name", "description": "Objective Description" }]
     ```

4. **identifyKnowledgeGaps(Long userId, Long courseId)**: Identifies knowledge gaps based on assessment results.

   - **Endpoint**: `GET /courses/{courseId}/knowledge-gaps/{userId}`
   - **Response**:
     ```json
     { "gaps": [{ "topic": "Topic Name", "score": 0.75 }] }
     ```

5. **generatePersonalizedQuestions(Long userId, Long courseId)**: Generates personalized practice questions based on identified weak areas.

   - **Endpoint**: `GET /courses/{courseId}/questions/{userId}`
   - **Response**:
     ```json
     [
       {
         "question": "What is ...?",
         "options": ["Option 1", "Option 2"],
         "answer": "Option 1"
       }
     ]
     ```

6. **analyzeEngagementPatterns(Long userId, Long courseId)**: Analyzes student engagement patterns.

   - **Endpoint**: `GET /courses/{courseId}/engagement/{userId}`
   - **Response**:
     ```json
     { "patterns": { "engagementRate": 0.85, "averageTimeSpent": 120 } }
     ```

7. **predictPerformance(Long userId, Long courseId)**: Predicts student performance and completion likelihood.
   - **Endpoint**: `GET /courses/{courseId}/predict/{userId}`
   - **Response**:
     ```json
     { "completionLikelihood": 0.9, "expectedScore": 85 }
     ```

## UserService Methods

1. **getAllUsers()**: Retrieves a list of all users.

   - **Endpoint**: `GET /users`
   - **Response**:
     ```json
     [
       {
         "id": 1,
         "name": "User Name",
         "email": "user@example.com",
         "role": "student"
       }
     ]
     ```

2. **getUserById(Long id)**: Finds a user by their ID.

   - **Endpoint**: `GET /users/{id}`
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "User Name",
       "email": "user@example.com",
       "role": "student"
     }
     ```

3. **createUser(User user)**: Creates a new user.

   - **Endpoint**: `POST /users`
   - **Request Body**:
     ```json
     { "name": "New User", "email": "newuser@example.com", "role": "student" }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "New User",
       "email": "newuser@example.com",
       "role": "student"
     }
     ```

4. **updateUser(Long id, User user)**: Updates an existing user.

   - **Endpoint**: `PUT /users/{id}`
   - **Request Body**:
     ```json
     {
       "name": "Updated User",
       "email": "updateduser@example.com",
       "role": "student"
     }
     ```
   - **Response**:
     ```json
     {
       "id": 1,
       "name": "Updated User",
       "email": "updateduser@example.com",
       "role": "student"
     }
     ```

5. **deleteUser(Long id)**: Deletes a user by their ID.
   - **Endpoint**: `DELETE /users/{id}`
   - **Response**: `204 No Content`
