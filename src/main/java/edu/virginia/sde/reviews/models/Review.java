package edu.virginia.sde.reviews.models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Review {
    private int id;
    private int userId;
    private int courseId;
    private int rating;
    private String comment;
    private Timestamp timestamp;

    public Review(int id, int userId, int courseId, int rating, String comment, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    // Constructor for creating a new review (before it's added to database)
    public Review(int userId, int courseId, int rating, String comment) {
        this(-1, userId, courseId, rating, comment, new Timestamp(System.currentTimeMillis()));
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCourseId() { return courseId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Timestamp getTimestamp() { return timestamp; }

    // Setters for editable fields
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void updateTimestamp() {
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public String getFormattedTimestamp() {
        if (timestamp == null) return "";
        LocalDateTime local = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return local.format(formatter);
    }
} 