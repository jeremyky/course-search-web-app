package edu.virginia.sde.reviews;

import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.models.Review;
import edu.virginia.sde.reviews.services.DatabaseService;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseServiceIntegrationTest {
    private static DatabaseService database;
    private static final String TEST_USER = "testUser";
    private static final String TEST_PASSWORD = "password123";
    private static int userId;

    @BeforeAll
    static void setUp() {
        database = new DatabaseService();
        database.clearDatabase();
        assertTrue(database.createUser(TEST_USER, TEST_PASSWORD));
        userId = database.getUserId(TEST_USER);
        assertTrue(userId > 0);
    }

    @AfterAll
    static void tearDown() {
        database.clearDatabase();
    }

    @Test
    @Order(1)
    void testUserOperations() {
        // Test user creation and validation
        assertTrue(database.createUser("newUser", "password123"));
        assertTrue(database.validateUser("newUser", "password123"));
        assertFalse(database.validateUser("newUser", "wrongPassword"));
        assertFalse(database.validateUser("nonexistentUser", "password123"));
        
        // Test duplicate username
        assertFalse(database.createUser("newUser", "anotherPassword"));
        
        // Test getUserId
        int userId = database.getUserId("newUser");
        assertTrue(userId > 0);
        assertEquals(-1, database.getUserId("nonexistentUser"));
    }

    @Test
    @Order(2)
    void testCourseOperations() {
        // Test course creation
        Course course = database.addCourse("CS", 3140, "Software Development");
        assertNotNull(course);
        assertEquals("CS", course.getSubject());
        assertEquals(3140, course.getNumber());
        assertEquals("Software Development", course.getTitle());

        // Test course retrieval
        Course retrievedCourse = database.getCourse(course.getId());
        assertNotNull(retrievedCourse);
        assertEquals(course.getSubject(), retrievedCourse.getSubject());
        assertEquals(course.getNumber(), retrievedCourse.getNumber());

        // Test course search
        List<Course> searchResults = database.searchCourses("CS", 3140, null);
        assertFalse(searchResults.isEmpty());
        assertEquals(1, searchResults.size());
        assertEquals(course.getId(), searchResults.get(0).getId());

        // Test partial search
        searchResults = database.searchCourses(null, null, "Software");
        assertFalse(searchResults.isEmpty());
        assertTrue(searchResults.stream()
                .anyMatch(c -> c.getTitle().contains("Software")));

        // Test invalid course creation
        assertNull(database.addCourse("", 1234, "Invalid Course"));
        assertNull(database.addCourse("CS", -1, "Invalid Course"));
        assertNull(database.addCourse("CS", 3140, "")); // Empty title
    }

    @Test
    @Order(3)
    void testReviewOperations() {
        // Create a course to review
        Course course = database.addCourse("CS", 2100, "Data Structures");
        assertNotNull(course);

        // Test adding a review
        Review review = database.addReview(userId, course.getId(), 5, "Great course!");
        assertNotNull(review);
        assertEquals(5, review.getRating());
        assertEquals("Great course!", review.getComment());

        // Test getting course reviews
        List<Review> courseReviews = database.getReviewsForCourse(course.getId());
        assertFalse(courseReviews.isEmpty());
        assertEquals(1, courseReviews.size());
        assertEquals(5, courseReviews.get(0).getRating());

        // Test updating a review
        assertTrue(database.updateReview(review.getId(), 4, "Updated comment"));
        courseReviews = database.getReviewsForCourse(course.getId());
        assertEquals(4, courseReviews.get(0).getRating());
        assertEquals("Updated comment", courseReviews.get(0).getComment());

        // Test getting user reviews
        List<Review> userReviews = database.getUserReviews(userId);
        assertFalse(userReviews.isEmpty());
        assertEquals(1, userReviews.size());

        // Test deleting a review
        assertTrue(database.deleteReview(review.getId()));
        courseReviews = database.getReviewsForCourse(course.getId());
        assertTrue(courseReviews.isEmpty());
    }

    @Test
    @Order(4)
    void testAverageRating() {
        // Create a course
        Course course = database.addCourse("CS", 4720, "Mobile Development");
        assertNotNull(course);

        // Add multiple reviews
        database.addReview(userId, course.getId(), 5, "Excellent!");
        
        // Create additional test users
        assertTrue(database.createUser("user2", "password123"));
        assertTrue(database.createUser("user3", "password123"));
        int user2Id = database.getUserId("user2");
        int user3Id = database.getUserId("user3");
        
        database.addReview(user2Id, course.getId(), 3, "Average");
        database.addReview(user3Id, course.getId(), 4, "Good");

        // Test average rating calculation
        Course retrievedCourse = database.getCourse(course.getId());
        assertNotNull(retrievedCourse.getAverageRating());
        assertEquals(4.0, retrievedCourse.getAverageRating(), 0.01);
    }

    @Test
    @Order(5)
    void testInvalidOperations() {
        // Test invalid course creation
        assertNull(database.addCourse(null, 1234, "Invalid Course"), 
            "Should reject null subject");
        assertNull(database.addCourse("", 1234, "Invalid Course"), 
            "Should reject empty subject");
        assertNull(database.addCourse("CS", -1, "Invalid Course"), 
            "Should reject negative number");
        assertNull(database.addCourse("CS", 1234, null), 
            "Should reject null title");
        assertNull(database.addCourse("CS", 1234, ""), 
            "Should reject empty title");
        
        // Test invalid review operations
        assertNull(database.addReview(-1, 1, 5, "Invalid Review"), 
            "Should reject negative user ID");
        assertNull(database.addReview(1, -1, 5, "Invalid Review"), 
            "Should reject negative course ID");
        assertNull(database.addReview(999, 999, 5, "Invalid Review"), 
            "Should reject non-existent user/course");
        
        // Test invalid review updates
        assertFalse(database.updateReview(-1, 5, "Update non-existent review"), 
            "Should reject update with negative ID");
        assertFalse(database.updateReview(999, 5, "Update non-existent review"), 
            "Should reject update of non-existent review");
        
        // Test invalid review deletions
        assertFalse(database.deleteReview(-1), 
            "Should reject deletion with negative ID");
        assertFalse(database.deleteReview(999), 
            "Should reject deletion of non-existent review");
        
        // Test invalid course retrieval
        assertNull(database.getCourse(-1), 
            "Should return null for negative course ID");
        assertNull(database.getCourse(999), 
            "Should return null for non-existent course ID");
        
        // Test invalid user ID retrieval
        assertEquals(-1, database.getUserId(null), 
            "Should return -1 for null username");
        assertEquals(-1, database.getUserId(""), 
            "Should return -1 for empty username");
        assertEquals(-1, database.getUserId("nonexistentUser"), 
            "Should return -1 for non-existent username");
    }

    @Test
    @Order(6)
    void testSearchEdgeCases() {
        // Test empty search
        List<Course> results = database.searchCourses(null, null, null);
        assertNotNull(results);
        
        // Test search with invalid criteria
        results = database.searchCourses("", null, "");
        assertNotNull(results);
        
        // Test case insensitive search
        Course course = database.addCourse("CS", 1110, "Introduction to Programming");
        assertNotNull(course);
        
        results = database.searchCourses("cs", null, null);
        assertFalse(results.isEmpty());
        
        results = database.searchCourses(null, null, "PROGRAMMING");
        assertFalse(results.isEmpty());
    }
} 