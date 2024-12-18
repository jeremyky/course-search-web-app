package edu.virginia.sde.reviews.models;

public class Course {
    private int id;
    private String subject;
    private int number;
    private String title;
    private Double averageRating;

    public Course(int id, String subject, int number, String title) {
        if (!isValidSubject(subject)) {
            throw new IllegalArgumentException("Subject must be 2-4 letters");
        }
        if (!isValidNumber(number)) {
            throw new IllegalArgumentException("Number must be exactly 4 digits");
        }
        if (!isValidTitle(title)) {
            throw new IllegalArgumentException("Title must be between 1 and 50 characters");
        }
        
        this.id = id;
        this.subject = subject.toUpperCase();
        this.number = number;
        this.title = title;
        this.averageRating = null;
    }

    // Getters
    public int getId() { return id; }
    public String getSubject() { return subject; }
    public int getNumber() { return number; }
    public String getTitle() { return title; }
    public Double getAverageRating() { return averageRating; }

    // Setter for average rating
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    // Validation methods
    public static boolean isValidSubject(String subject) {
        return subject != null && subject.matches("[A-Za-z]{2,4}");
    }

    public static boolean isValidNumber(int number) {
        return number >= 1000 && number <= 9999;
    }

    public static boolean isValidTitle(String title) {
        return title != null && !title.isEmpty() && title.length() <= 50;
    }

    // For displaying in ListView and debugging
    @Override
    public String toString() {
        String ratingStr = averageRating == null ? "No ratings" : 
            String.format("%.2f", averageRating);
        return String.format("%s %d: %s (Rating: %s)", 
            subject, number, title, ratingStr);
    }

    // For comparing courses (useful for testing and searching)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id == course.id &&
               number == course.number &&
               subject.equals(course.subject) &&
               title.equals(course.title);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        result = 31 * result + subject.hashCode();
        result = 31 * result + number;
        result = 31 * result + title.hashCode();
        return result;
    }
} 