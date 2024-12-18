package edu.virginia.sde.reviews.utils;

import edu.virginia.sde.reviews.services.DatabaseService;

public class DatabaseInitializer {
    private final DatabaseService database;

    public DatabaseInitializer() {
        this.database = new DatabaseService();
    }

    public void initializeDatabase() {
        // Create sample users
        database.createUser("student1", "password123");
        database.createUser("wahoo2024", "hoosgoingtowin");
        database.createUser("cs_major", "javaislife123");
        database.createUser("math_lover", "calculus4ever");
        database.createUser("grad_student", "research2023");
        database.createUser("engr_student", "bridges2024");
        database.createUser("physics_fan", "quantum2023");
        database.createUser("bio_student", "dna4life");

        // Add sample courses
        database.addCourse("CS", 3140, "Software Development Methods");
        database.addCourse("CS", 2100, "Data Structures and Algorithms");
        database.addCourse("CS", 4102, "Algorithms");
        database.addCourse("MATH", 3354, "Survey of Algebra");
        database.addCourse("PHYS", 2415, "Physics 1 for Engineers");
        database.addCourse("APMA", 3100, "Probability");
        database.addCourse("SYS", 2048, "System Design 1");
        database.addCourse("ECE", 2330, "Digital Logic Design");
        database.addCourse("CS", 4414, "Operating Systems");
        database.addCourse("CS", 3240, "Advanced Software Development");
        database.addCourse("MATH", 4310, "Real Analysis");
        database.addCourse("PHYS", 3420, "Quantum Physics");
        database.addCourse("CS", 4774, "Machine Learning");
        database.addCourse("APMA", 2120, "Multivariable Calculus");
        database.addCourse("ECE", 3430, "Introduction to Electronics");

        // Add sample reviews
        addSampleReviews();
    }

    private void addSampleReviews() {
        // Get user IDs
        int student1Id = database.getUserId("student1");
        int wahooId = database.getUserId("wahoo2024");
        int csMajorId = database.getUserId("cs_major");
        int mathLoverId = database.getUserId("math_lover");
        int gradStudentId = database.getUserId("grad_student");
        int engrStudentId = database.getUserId("engr_student");
        int physicsId = database.getUserId("physics_fan");
        int bioId = database.getUserId("bio_student");

        // CS 3140 Reviews (diverse opinions)
        addReview(student1Id, "CS", 3140, 4, "Great course! Learned a lot about software development practices.");
        addReview(wahooId, "CS", 3140, 5, "Really enjoyed the group project! Great team experience.");
        addReview(bioId, "CS", 3140, 2, "Challenging for non-CS majors. Need more programming background.");

        // CS 2100 Reviews
        addReview(student1Id, "CS", 2100, 5, "Essential course for CS majors. Challenging but rewarding.");
        addReview(csMajorId, "CS", 2100, 5, "Best CS course I've taken! Great foundation for other courses.");
        addReview(engrStudentId, "CS", 2100, 3, "Heavy workload, but concepts are useful.");

        // Physics Courses
        addReview(physicsId, "PHYS", 2415, 5, "Excellent introduction to physics concepts.");
        addReview(wahooId, "PHYS", 2415, 3, "Tough course, but the professor was helpful.");
        addReview(engrStudentId, "PHYS", 2415, 4, "Important for engineering students. Good lab components.");
        addReview(physicsId, "PHYS", 3420, 5, "Fascinating introduction to quantum mechanics!");
        addReview(gradStudentId, "PHYS", 3420, 4, "Complex material but well-taught.");

        // Math Courses
        addReview(mathLoverId, "MATH", 3354, 5, "Beautiful introduction to abstract algebra.");
        addReview(gradStudentId, "MATH", 3354, 5, "Excellent preparation for graduate algebra.");
        addReview(mathLoverId, "MATH", 4310, 4, "Rigorous but rewarding. Be prepared to write proofs!");
        addReview(csMajorId, "MATH", 4310, 3, "Very theoretical. Requires strong math background.");

        // Advanced CS Courses
        addReview(csMajorId, "CS", 4414, 4, "Fascinating material about OS internals.");
        addReview(gradStudentId, "CS", 4414, 5, "Deep dive into operating systems. Great projects!");
        addReview(csMajorId, "CS", 4774, 5, "Excellent introduction to ML concepts.");
        addReview(gradStudentId, "CS", 4774, 4, "Good balance of theory and practice.");

        // Engineering Courses
        addReview(engrStudentId, "ECE", 2330, 4, "Very practical. Lots of hands-on experience.");
        addReview(csMajorId, "ECE", 2330, 3, "Useful for understanding computer architecture.");
        addReview(engrStudentId, "ECE", 3430, 5, "Great lab experiments!");
        addReview(physicsId, "ECE", 3430, 4, "Interesting applications of physics concepts.");

        // Applied Math
        addReview(mathLoverId, "APMA", 3100, 5, "Essential for understanding statistics and ML.");
        addReview(bioId, "APMA", 3100, 2, "Very difficult for biology students.");
        addReview(student1Id, "APMA", 2120, 4, "Challenging but well-structured course.");
        addReview(engrStudentId, "APMA", 2120, 3, "Heavy workload, but necessary for engineers.");

        // Systems Engineering
        addReview(engrStudentId, "SYS", 2048, 4, "Good introduction to systems thinking.");
        addReview(bioId, "SYS", 2048, 5, "Surprisingly useful for non-engineering majors!");
        
        // More CS Course Reviews
        addReview(wahooId, "CS", 3240, 4, "Good follow-up to 3140.");
        addReview(csMajorId, "CS", 3240, 5, "Great practical experience with Agile.");
        addReview(student1Id, "CS", 4102, 4, "Challenging algorithms course, but very rewarding.");
        addReview(gradStudentId, "CS", 4102, 5, "Essential for technical interviews.");
    }

    private void addReview(int userId, String subject, int number, int rating, String comment) {
        int courseId = database.getCourseIdBySubjectAndNumber(subject, number);
        if (courseId != -1) {
            database.addReview(userId, courseId, rating, comment);
        }
    }

    public static void main(String[] args) {
        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.initializeDatabase();
        System.out.println("Database initialized successfully!");
    }
} 