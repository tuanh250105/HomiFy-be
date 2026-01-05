package com.homifybackend.exception;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(Long reviewId) {
        super("Review not found with ID: " + reviewId);
    }

    public ReviewNotFoundException(String message) {
        super(message);
    }
}