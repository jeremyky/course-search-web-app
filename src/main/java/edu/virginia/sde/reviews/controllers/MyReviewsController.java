package edu.virginia.sde.reviews.controllers;

import edu.virginia.sde.reviews.models.Review;
import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.models.ReviewDisplay;
import edu.virginia.sde.reviews.services.DatabaseService;
import edu.virginia.sde.reviews.utils.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.io.IOException;
import java.util.List;
import java.util.Comparator;

public class MyReviewsController {
    private final DatabaseService database;
    private String currentUser;
    private ObservableList<ReviewDisplay> allReviews;

    @FXML
    private ListView<ReviewDisplay> reviewsListView;
    @FXML
    private Label messageLabel;
    @FXML
    private ComboBox<String> sortComboBox;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private TextField searchField;

    public MyReviewsController() {
        this.database = new DatabaseService();
        this.allReviews = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        // Setup sorting options
        sortComboBox.setItems(FXCollections.observableArrayList(
            "Most Recent", "Course Name", "Rating (High to Low)", "Rating (Low to High)"
        ));
        sortComboBox.setValue("Most Recent");
        sortComboBox.setOnAction(e -> sortReviews());

        // Setup filtering options
        filterComboBox.setItems(FXCollections.observableArrayList(
            "All Reviews", "5 Stars", "4+ Stars", "3+ Stars", "With Comments"
        ));
        filterComboBox.setValue("All Reviews");
        filterComboBox.setOnAction(e -> filterReviews());

        // Setup search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterReviews());
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
        loadUserReviews();
    }

    private void loadUserReviews() {
        int userId = database.getUserId(currentUser);
        List<Review> reviews = database.getUserReviews(userId);
        
        allReviews.clear();
        for (Review review : reviews) {
            Course course = database.getCourse(review.getCourseId());
            if (course != null) {
                allReviews.add(new ReviewDisplay(review, course));
            }
        }
        
        if (reviews.isEmpty()) {
            messageLabel.setText("You haven't written any reviews yet.");
        } else {
            messageLabel.setText("");
        }

        sortAndFilterReviews();
    }

    private void sortAndFilterReviews() {
        sortReviews();
        filterReviews();
    }

    private void sortReviews() {
        String sortOption = sortComboBox.getValue();
        Comparator<ReviewDisplay> comparator = switch (sortOption) {
            case "Course Name" -> Comparator.comparing(rd -> 
                rd.getCourse().getSubject() + rd.getCourse().getNumber());
            case "Rating (High to Low)" -> Comparator.comparing(
                (ReviewDisplay rd) -> rd.getReview().getRating()).reversed();
            case "Rating (Low to High)" -> Comparator.comparing(
                rd -> rd.getReview().getRating());
            default -> Comparator.comparing(rd -> rd.getReview().getTimestamp(),
                Comparator.reverseOrder());
        };

        SortedList<ReviewDisplay> sortedList = new SortedList<>(allReviews, comparator);
        reviewsListView.setItems(sortedList);
    }

    private void filterReviews() {
        String filterOption = filterComboBox.getValue();
        String searchText = searchField.getText().toLowerCase();

        FilteredList<ReviewDisplay> filteredList = new FilteredList<>(allReviews, review -> {
            // Apply rating filter
            boolean passesRatingFilter = switch (filterOption) {
                case "5 Stars" -> review.getReview().getRating() == 5;
                case "4+ Stars" -> review.getReview().getRating() >= 4;
                case "3+ Stars" -> review.getReview().getRating() >= 3;
                case "With Comments" -> review.getReview().getComment() != null && 
                    !review.getReview().getComment().trim().isEmpty();
                default -> true;
            };

            // Apply search filter
            boolean passesSearchFilter = searchText.isEmpty() || 
                review.toString().toLowerCase().contains(searchText);

            return passesRatingFilter && passesSearchFilter;
        });

        reviewsListView.setItems(filteredList);
        
        if (filteredList.isEmpty()) {
            messageLabel.setText("No reviews match your criteria.");
        } else {
            messageLabel.setText("");
        }
    }

    @FXML
    public void handleReviewSelected() {
        ReviewDisplay selectedReview = reviewsListView.getSelectionModel().getSelectedItem();
        if (selectedReview != null) {
            try {
                SceneManager.switchToScene(reviewsListView, 
                    "/edu/virginia/sde/reviews/courseReview.fxml", 
                    currentUser,
                    selectedReview.getCourse());
            } catch (IOException e) {
                messageLabel.setText("Error loading course review page.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleBackToSearch() {
        try {
            SceneManager.switchToScene(reviewsListView, 
                "/edu/virginia/sde/reviews/courseSearch.fxml", 
                currentUser);
        } catch (IOException e) {
            messageLabel.setText("Error returning to course search.");
            e.printStackTrace();
        }
    }

} 