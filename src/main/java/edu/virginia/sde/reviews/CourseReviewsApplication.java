package edu.virginia.sde.reviews;

import edu.virginia.sde.reviews.utils.DatabaseInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CourseReviewsApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Check if we need to initialize the database
        Parameters params = getParameters();
        if (params.getRaw().contains("--init-db")) {
            System.out.println("Initializing database with sample data...");
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.initializeDatabase();
            System.out.println("Database initialization complete!");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String css = this.getClass().getResource("/edu/virginia/sde/reviews/css/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Course Reviews");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
