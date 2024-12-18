package edu.virginia.sde.reviews.utils;

import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.controllers.CourseReviewController;
import edu.virginia.sde.reviews.controllers.CourseSearchController;
import edu.virginia.sde.reviews.controllers.MyReviewsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    public static void switchToScene(Control sourceControl, String fxmlPath, String currentUser) throws IOException {
        switchToScene(sourceControl, fxmlPath, currentUser, null);
    }

    public static void switchToScene(Control sourceControl, String fxmlPath, String currentUser, Course selectedCourse) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load());

        String css = SceneManager.class.getResource("/edu/virginia/sde/reviews/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        // Get the controller and set the current user
        Object controller = loader.getController();
        if (controller instanceof CourseSearchController) {
            ((CourseSearchController) controller).setCurrentUser(currentUser);
        } else if (controller instanceof MyReviewsController) {
            ((MyReviewsController) controller).setCurrentUser(currentUser);
        } else if (controller instanceof CourseReviewController) {
            ((CourseReviewController) controller).setData(currentUser, selectedCourse);
        }
        
        // Get the stage and set the new scene
        Stage stage = (Stage) sourceControl.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
} 