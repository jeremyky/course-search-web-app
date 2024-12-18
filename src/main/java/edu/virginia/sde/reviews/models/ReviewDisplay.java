package edu.virginia.sde.reviews.models;

import edu.virginia.sde.reviews.models.Course;
import edu.virginia.sde.reviews.models.Review;

public class ReviewDisplay {
    private final Review review;
    private final Course course;

    public ReviewDisplay(Review review, Course course) {
        this.review = review;
        this.course = course;
    }

    public Review getReview() { return review; }
    public Course getCourse() { return course; }

    @Override
    public String toString() {
        return String.format("%s %d: %s - Rating: %d/5 - Timestamp: %s%s",
            course.getSubject(),
            course.getNumber(),
            course.getTitle(),
            review.getRating(),
            review.getFormattedTimestamp(),
            review.getComment() != null && !review.getComment().isEmpty()
                ? "\nComment: " + review.getComment()
                : ""
        );
    }
}
