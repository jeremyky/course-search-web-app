## To Run


Option 1: CLean Start (No Sample Data)
./gradlew run
This will start the applicaiton with an empty database. You will have to manually create your own user and courses to use the application.

Option 2: Run with Sample Data
./gradlew run --args="--init-db"
This will populate the database with sample users, courses, and reviews before starting the application.

Sample Accounts: When running the sample data, you can use these accounts to test the applicaiton:

| Username     | Password      | Description                    |
|-------------|---------------|--------------------------------|
| student1    | password123   | Has reviews across CS courses  |
| wahoo2024   | hoosgoingtowin| Mixed department reviews      |
| cs_major    | javaislife123 | Focused on CS courses         |
| math_lover  | calculus4ever | Math department reviews       |
| grad_student| research2023  | Graduate-level course reviews |

To reset the database to its initial state:
1. Delete the `course_reviews.db` file
2. Run the application with `--init-db` flag

To run the testing suite: ./gradlew test

## Issues

List any known issues (bugs, incorrect behavior, etc.) at the time of submission.

# Course Reviews Application

## Overview
A JavaFX application for UVA students to review courses and share their experiences.

## Building and Running the Application

### Prerequisites
- Java 17 or higher
- Gradle (included wrapper)
- JavaFX (included in dependencies)

### Building the Project

```bash
./gradlew build
