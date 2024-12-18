[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/QrU2hpdx)
# Homework 6 - Responding to Change

## Authors
1) Ashley Wu, bcu6cy, [ashwu0321]
2) Jeremy Ky, juh7hc, [jeremyky]
3) Name, netid, [GitHub profile name]

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


## Contributions

List the primary contributions of each author. It is recommended to update this with your contributions after each coding session.:

### [Ashley Wu]

* Author 1 contributions
* Frontend Implementation: GUI, scenebuilder
* Implemented review display and selection
* Stylization of application via CSS 

### [Jeremy Ky]

* Author 2 contributions
* Backend Implementation: core functionality, database service, controllers, database layer, testing
* Update Architecture: MVC
* Key Features: User auth and account management, course search with multiple filters, review system, average rating calculations, data persistence

### [Author 3 - replace this with their name]

* Author 3 contributions
* as a bulleted list
* each line starts with an asterisk and a space

## Issues

List any known issues (bugs, incorrect behavior, etc.) at the time of submission.

# Course Reviews Application

## Authors
1) Ashley Wu, bcu6cy, [ashwu0321]
2) Jeremy Ky, juh7hc, [jeremyky]

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