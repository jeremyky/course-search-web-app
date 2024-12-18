package edu.virginia.sde.reviews.controllers;

import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.models.Review;
import edu.virginia.sde.reviews.models.ReviewDisplay;
import edu.virginia.sde.reviews.services.DatabaseService;
import edu.virginia.sde.reviews.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import java.io.IOException;
import java.util.List;
import javafx.collections.ObservableList;

public class CourseReviewController {
    private final DatabaseService database;
    private String currentUser;
    private Course currentCourse;
    private Review userReview;

    @FXML
    private Label courseLabel;
    @FXML
    private Label averageRatingLabel;
    @FXML
    private ListView<ReviewDisplay> reviewsListView;
    @FXML
    private ComboBox<Integer> ratingComboBox;
    @FXML
    private TextArea commentArea;
    @FXML
    private Button submitButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label messageLabel;

    public CourseReviewController() {
        this.database = new DatabaseService();
    }

    public void initialize() {
        // Setup rating combo box
        ratingComboBox.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        // Initially hide delete button
        deleteButton.setVisible(false);
    }

    public void setData(String username, Course course) {
        this.currentUser = username;
        this.currentCourse = course;
        
        // Update UI with course info
        courseLabel.setText(String.format("%s %d: %s", 
            course.getSubject(), course.getNumber(), course.getTitle()));
        updateAverageRating();
        
        // Load all reviews
        loadReviews();
        
        // Check if user has already reviewed this course
        checkExistingReview();
    }

    private void loadReviews() {
        List<Review> reviews = database.getReviewsForCourse(currentCourse.getId());
        ObservableList<ReviewDisplay> displayReviews = FXCollections.observableArrayList();

        for (Review review : reviews) {
            displayReviews.add(new ReviewDisplay(review, currentCourse));
        }
        reviewsListView.setItems(displayReviews);
    }

    private void checkExistingReview() {
        int userId = database.getUserId(currentUser);
        List<Review> reviews = database.getReviewsForCourse(currentCourse.getId());
        
        for (Review review : reviews) {
            if (review.getUserId() == userId) {
                userReview = review;
                ratingComboBox.setValue(review.getRating());
                commentArea.setText(review.getComment());
                submitButton.setText("Update Review");
                deleteButton.setVisible(true);
                return;
            }
        }
        
        // No existing review found
        userReview = null;
        ratingComboBox.setValue(null);
        commentArea.clear();
        submitButton.setText("Submit Review");
        deleteButton.setVisible(false);
    }

    @FXML
    public void handleSubmit() {
        Integer rating = ratingComboBox.getValue();
        String comment = commentArea.getText().trim();
        
        if (rating == null) {
            messageLabel.setText("Please select a rating.");
            return;
        }

        int userId = database.getUserId(currentUser);
        
        if (userReview == null) {
            // Create new review
            Review newReview = database.addReview(userId, currentCourse.getId(), rating, comment);
            if (newReview != null) {
                messageLabel.setText("Review submitted successfully!");
                userReview = newReview;
                submitButton.setText("Update Review");
                deleteButton.setVisible(true);
            } else {
                messageLabel.setText("Failed to submit review.");
            }
        } else {
            // Update existing review
            if (database.updateReview(userReview.getId(), rating, comment)) {
                messageLabel.setText("Review updated successfully!");
            } else {
                messageLabel.setText("Failed to update review.");
            }
        }
        
        loadReviews();
        updateAverageRating();
    }

    @FXML
    public void handleDelete() {
        if (userReview != null) {
            if (database.deleteReview(userReview.getId())) {
                messageLabel.setText("Review deleted successfully!");
                userReview = null;
                ratingComboBox.setValue(null);
                commentArea.clear();
                submitButton.setText("Submit Review");
                deleteButton.setVisible(false);
                loadReviews();
                updateAverageRating();
            } else {
                messageLabel.setText("Failed to delete review.");
            }
        }
    }

    @FXML
    public void handleBack() {
        try {
            SceneManager.switchToScene(courseLabel, 
                "/edu/virginia/sde/reviews/courseSearch.fxml", 
                currentUser);
        } catch (IOException e) {
            messageLabel.setText("Error returning to course search.");
            e.printStackTrace();
        }
    }

    private void updateAverageRating() {
        Course updated = database.getCourse(currentCourse.getId());
        if (updated != null && updated.getAverageRating() != null) {
            averageRatingLabel.setText(String.format("Average Rating: %.2f", 
                updated.getAverageRating()));
        } else {
            averageRatingLabel.setText("No ratings yet");
        }
    }
} 