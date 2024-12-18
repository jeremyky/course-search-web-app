package edu.virginia.sde.reviews.services;

import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.models.Review;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseServiceTest {
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PASSWORD = "testPass123";
    private DatabaseService database;
    private int testUserId;

    @BeforeEach
    void setUp() {
        database = new DatabaseService();
        database.clearDatabase(); // Clear database before each test
        System.out.println("\n=== Starting new test ===");
        assertTrue(database.createUser(TEST_USERNAME, TEST_PASSWORD), "Failed to create test user");
        testUserId = database.getUserId(TEST_USERNAME);
        System.out.println("Created test user with ID: " + testUserId);
        assertTrue(testUserId > 0, "Failed to get valid user ID");
        database.printTableContents();
    }

    @Test
    void testUserCreationAndValidation() {
        // Test creating a new user
        String username = "newUser";
        String password = "password123";
        assertTrue(database.createUser(username, password));
        
        // Test user validation
        assertTrue(database.validateUser(username, password));
        assertFalse(database.validateUser(username, "wrongPassword"));
        assertFalse(database.validateUser("nonexistentUser", password));

        // Test duplicate username
        assertFalse(database.createUser(username, "anotherPassword"));
    }

    @Test
    void testCourseOperations() {
        System.out.println("\n=== Testing course operations ===");
        // Test adding a course
        Course course = database.addCourse("CS", 3140, "Software Development");
        System.out.println("Added course: " + course);
        database.printTableContents();
        assertNotNull(course, "Course should not be null after creation");
        System.out.println("Created course with ID: " + course.getId());
        
        assertEquals("CS", course.getSubject());
        assertEquals(3140, course.getNumber());
        assertEquals("Software Development", course.getTitle());

        // Test getting the course
        Course retrievedCourse = database.getCourse(course.getId());
        assertNotNull(retrievedCourse, "Retrieved course should not be null");
        assertEquals(course.getSubject(), retrievedCourse.getSubject());
        assertEquals(course.getNumber(), retrievedCourse.getNumber());
        assertEquals(course.getTitle(), retrievedCourse.getTitle());

        // Test course search
        List<Course> searchResults = database.searchCourses("CS", 3140, null);
        assertFalse(searchResults.isEmpty(), "Search results should not be empty");
        assertEquals(1, searchResults.size());
        Course foundCourse = searchResults.get(0);
        assertEquals(course.getId(), foundCourse.getId());
    }

    @Test
    void testReviewOperations() {
        // First, add a course to review
        Course course = database.addCourse("CS", 3140, "Software Development");
        assertNotNull(course);

        // Add a review
        Review review = database.addReview(testUserId, course.getId(), 5, "Great course!");
        assertNotNull(review);
        assertEquals(5, review.getRating());
        assertEquals("Great course!", review.getComment());

        // Get reviews for the course
        List<Review> courseReviews = database.getReviewsForCourse(course.getId());
        assertFalse(courseReviews.isEmpty());
        assertEquals(1, courseReviews.size());
        assertEquals(5, courseReviews.get(0).getRating());

        // Update the review
        assertTrue(database.updateReview(review.getId(), 4, "Updated comment"));
        courseReviews = database.getReviewsForCourse(course.getId());
        assertEquals(4, courseReviews.get(0).getRating());
        assertEquals("Updated comment", courseReviews.get(0).getComment());

        // Delete the review
        assertTrue(database.deleteReview(review.getId()));
        courseReviews = database.getReviewsForCourse(course.getId());
        assertTrue(courseReviews.isEmpty());
    }

    @Test
    void testAverageRating() {
        System.out.println("Testing average rating...");
        // Add a course
        Course course = database.addCourse("CS", 3140, "Software Development");
        assertNotNull(course, "Course should not be null");
        System.out.println("Created course with ID: " + course.getId());

        // Create additional test users
        assertTrue(database.createUser("testUser2", "password123"));
        assertTrue(database.createUser("testUser3", "password123"));
        int user2Id = database.getUserId("testUser2");
        int user3Id = database.getUserId("testUser3");
        System.out.println("Created additional users with IDs: " + user2Id + ", " + user3Id);

        // Add reviews
        Review review1 = database.addReview(testUserId, course.getId(), 5, "Excellent!");
        Review review2 = database.addReview(user2Id, course.getId(), 3, "Average");
        Review review3 = database.addReview(user3Id, course.getId(), 4, "Good");
        
        assertNotNull(review1, "Review 1 should not be null");
        assertNotNull(review2, "Review 2 should not be null");
        assertNotNull(review3, "Review 3 should not be null");

        // Get the course and check its average rating
        Course retrievedCourse = database.getCourse(course.getId());
        assertNotNull(retrievedCourse, "Retrieved course should not be null");
        Double avgRating = retrievedCourse.getAverageRating();
        System.out.println("Average rating: " + avgRating);
        assertNotNull(avgRating, "Average rating should not be null");
        assertEquals(4.0, avgRating, 0.01);
    }

    @Test
    void testUserReviews() {
        System.out.println("Testing user reviews...");
        // Add courses
        Course course1 = database.addCourse("CS", 3140, "Software Development");
        Course course2 = database.addCourse("CS", 2100, "Data Structures");
        assertNotNull(course1, "Course 1 should not be null");
        assertNotNull(course2, "Course 2 should not be null");
        System.out.println("Created courses with IDs: " + course1.getId() + ", " + course2.getId());

        // Add reviews
        Review review1 = database.addReview(testUserId, course1.getId(), 5, "Great course!");
        Review review2 = database.addReview(testUserId, course2.getId(), 4, "Good course!");
        assertNotNull(review1, "Review 1 should not be null");
        assertNotNull(review2, "Review 2 should not be null");

        // Get user reviews
        List<Review> userReviews = database.getUserReviews(testUserId);
        System.out.println("Found " + userReviews.size() + " reviews for user");
        assertEquals(2, userReviews.size(), "Should have 2 reviews");
        assertTrue(userReviews.stream()
                .allMatch(r -> r.getUserId() == testUserId), "All reviews should belong to test user");
    }
} 