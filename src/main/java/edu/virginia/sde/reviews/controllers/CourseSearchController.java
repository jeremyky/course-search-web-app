package edu.virginia.sde.reviews.controllers;

import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.services.DatabaseService;
import edu.virginia.sde.reviews.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.util.List;

public class CourseSearchController {
    private final DatabaseService database;
    private String currentUser;

    @FXML
    private TextField subjectSearchField;
    @FXML
    private TextField numberSearchField;
    @FXML
    private TextField titleSearchField;
    @FXML
    private ListView<Course> courseListView;
    @FXML
    private TextField newSubjectField;
    @FXML
    private TextField newNumberField;
    @FXML
    private TextField newTitleField;
    @FXML
    private Label messageLabel;

    public CourseSearchController() {
        this.database = new DatabaseService();
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        refreshCourseList(); // Load all courses initially
    }

    @FXML
    public void handleSearch() {
        String subject = subjectSearchField.getText().trim();
        String numberStr = numberSearchField.getText().trim();
        String title = titleSearchField.getText().trim();

        Integer number = null;
        if (!numberStr.isEmpty()) {
            try {
                number = Integer.parseInt(numberStr);
            } catch (NumberFormatException e) {
                messageLabel.setText("Course number must be a valid integer.");
                return;
            }
        }

        List<Course> courses = database.searchCourses(subject, number, title);
        courseListView.setItems(FXCollections.observableArrayList(courses));
        
        if (courses.isEmpty()) {
            messageLabel.setText("No courses found matching your criteria.");
        } else {
            messageLabel.setText("");
        }
    }

    @FXML
    public void handleAddCourse() {
        String subject = newSubjectField.getText().trim();
        String numberStr = newNumberField.getText().trim();
        String title = newTitleField.getText().trim();

        // Validate input
        if (!Course.isValidSubject(subject)) {
            messageLabel.setText("Subject must be 2-4 letters.");
            return;
        }

        int number;
        try {
            number = Integer.parseInt(numberStr);
            if (!Course.isValidNumber(number)) {
                messageLabel.setText("Number must be exactly 4 digits.");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Number must be a valid integer.");
            return;
        }

        if (!Course.isValidTitle(title)) {
            messageLabel.setText("Title must be between 1 and 50 characters.");
            return;
        }

        Course newCourse = database.addCourse(subject, number, title);
        if (newCourse != null) {
            messageLabel.setText("Course added successfully!");
            clearAddCourseFields();
            refreshCourseList();
        } else {
            messageLabel.setText("Failed to add course. It might already exist.");
        }
    }
    @FXML
    private void initialize() {
        courseListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleCourseSelected(newValue);
            }
        });
    }

    @FXML
    public void handleCourseSelected(Course selectedCourse) {
        if (selectedCourse != null) {
            try {
                SceneManager.switchToScene(courseListView, 
                    "/edu/virginia/sde/reviews/courseReview.fxml", 
                    currentUser,
                    selectedCourse);
            } catch (IOException e) {
                messageLabel.setText("Error loading course review page.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleMyReviews() {
        try {
            SceneManager.switchToScene(courseListView, 
                "/edu/virginia/sde/reviews/myReviews.fxml", 
                currentUser);
        } catch (IOException e) {
            messageLabel.setText("Error loading my reviews page.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        try {
            SceneManager.switchToScene(courseListView, 
                "/edu/virginia/sde/reviews/login.fxml", 
                null);
        } catch (IOException e) {
            messageLabel.setText("Error returning to login page.");
            e.printStackTrace();
        }
    }

    private void clearAddCourseFields() {
        newSubjectField.clear();
        newNumberField.clear();
        newTitleField.clear();
    }

    private void refreshCourseList() {
        List<Course> courses = database.searchCourses(null, null, null);
        courseListView.setItems(FXCollections.observableArrayList(courses));
    }
} 