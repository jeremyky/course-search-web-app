package edu.virginia.sde.reviews.services;

import java.sql.*;
import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.models.Review;
import edu.virginia.sde.reviews.models.User;
import java.util.ArrayList;
import java.util.List;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DatabaseService {
    private static final String URL = "jdbc:sqlite:course_reviews.db";

    public DatabaseService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL
                )
            """);

            // Create courses table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject TEXT NOT NULL,
                    number INTEGER NOT NULL,
                    title TEXT NOT NULL,
                    UNIQUE(subject, number, title)
                )
            """);

            // Create reviews table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reviews (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    course_id INTEGER NOT NULL,
                    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
                    comment TEXT,
                    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (course_id) REFERENCES courses(id),
                    UNIQUE(user_id, course_id)
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(password);
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Username already exists or other database error
            return false;
        }
    }

    public Course addCourse(String subject, int number, String title) {
        if (subject == null || subject.isEmpty() || number <= 0 || title == null || title.isEmpty()) {
            return null;
        }
        
        String sql = "INSERT INTO courses (subject, number, title) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, subject.toUpperCase());
            pstmt.setInt(2, number);
            pstmt.setString(3, title);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            // Get the last inserted ID
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Course course = new Course(id, subject, number, title);
                    course.setAverageRating(calculateAverageRating(id));
                    return course;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Course getCourse(int courseId) {
        if (!isValidId(courseId)) {
            return null;
        }

        String sql = "SELECT * FROM courses WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Course course = new Course(
                    rs.getInt("id"),
                    rs.getString("subject"),
                    rs.getInt("number"),
                    rs.getString("title")
                );
                course.setAverageRating(calculateAverageRating(courseId));
                return course;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Double calculateAverageRating(int courseId) {
        String sql = "SELECT AVG(CAST(rating AS DOUBLE)) as avg_rating FROM reviews WHERE course_id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double avg = rs.getDouble("avg_rating");
                return rs.wasNull() ? null : avg;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Course> searchCourses(String subject, Integer number, String title) {
        StringBuilder sql = new StringBuilder("SELECT * FROM courses WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (subject != null && !subject.isEmpty()) {
            sql.append(" AND UPPER(subject) = UPPER(?)");
            params.add(subject);
        }
        if (number != null) {
            sql.append(" AND number = ?");
            params.add(number);
        }
        if (title != null && !title.isEmpty()) {
            sql.append(" AND UPPER(title) LIKE UPPER(?)");
            params.add("%" + title + "%");
        }

        List<Course> courses = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("id"),
                    rs.getString("subject"),
                    rs.getInt("number"),
                    rs.getString("title")
                );
                course.setAverageRating(calculateAverageRating(course.getId()));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Review addReview(int userId, int courseId, int rating, String comment) {
        // Validate inputs
        if (!isValidId(userId) || !isValidId(courseId) || rating < 1 || rating > 5) {
            return null;
        }
        
        // Check if user and course exist
        if (!userExists(userId) || !courseExists(courseId)) {
            return null;
        }

        String sql = "INSERT INTO reviews (user_id, course_id, rating, comment, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, rating);
            pstmt.setString(4, comment);
            // Use local time when inserting
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            // Get the last inserted ID and return with local time
            try (Statement stmt = conn.createStatement();  
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid(), timestamp FROM reviews WHERE id = last_insert_rowid()")) {
                if (rs.next()) {
                    return new Review(
                        rs.getInt(1),
                        userId,
                        courseId,
                        rating,
                        comment,
                        rs.getTimestamp("timestamp")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Review> getReviewsForCourse(int courseId) {
        String sql = "SELECT * FROM reviews WHERE course_id = ? ORDER BY timestamp DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(new Review(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public List<Review> getUserReviews(int userId) {
        String sql = "SELECT * FROM reviews WHERE user_id = ? ORDER BY timestamp DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                reviews.add(new Review(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("course_id"),
                    rs.getInt("rating"),
                    rs.getString("comment"),
                    rs.getTimestamp("timestamp")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public boolean updateReview(int reviewId, int rating, String comment) {
        if (!isValidId(reviewId) || rating < 1 || rating > 5) {
            return false;
        }

        String sql = "UPDATE reviews SET rating = ?, comment = ?, timestamp = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rating);
            pstmt.setString(2, comment);
            // Use local time when updating
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(4, reviewId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteReview(int reviewId) {
        if (!isValidId(reviewId)) {
            return false;
        }

        String sql = "DELETE FROM reviews WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, reviewId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM reviews");
            stmt.execute("DELETE FROM courses");
            stmt.execute("DELETE FROM users");
            // Reset auto-increment counters
            stmt.execute("DELETE FROM sqlite_sequence");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printTableContents() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            // Print users
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
                System.out.println("\nUsers table:");
                while (rs.next()) {
                    System.out.printf("ID: %d, Username: %s%n", 
                        rs.getInt("id"), rs.getString("username"));
                }
            }

            // Print courses
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM courses")) {
                System.out.println("\nCourses table:");
                while (rs.next()) {
                    System.out.printf("ID: %d, Subject: %s, Number: %d, Title: %s%n",
                        rs.getInt("id"), rs.getString("subject"), 
                        rs.getInt("number"), rs.getString("title"));
                }
            }

            // Print reviews
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM reviews")) {
                System.out.println("\nReviews table:");
                while (rs.next()) {
                    System.out.printf("ID: %d, UserID: %d, CourseID: %d, Rating: %d%n",
                        rs.getInt("id"), rs.getInt("user_id"), 
                        rs.getInt("course_id"), rs.getInt("rating"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidId(int id) {
        return id > 0;
    }

    private boolean userExists(int userId) {
        String sql = "SELECT 1 FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean courseExists(int courseId) {
        String sql = "SELECT 1 FROM courses WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCourseIdBySubjectAndNumber(String subject, int number) {
        String sql = "SELECT id FROM courses WHERE subject = ? AND number = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, subject);
            pstmt.setInt(2, number);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
