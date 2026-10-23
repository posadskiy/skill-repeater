package com.posadskiy.skillrepeater.core.exception;

public class ReviewConflictException extends RuntimeException {

    public ReviewConflictException() {
        super("The skill changed while it was being reviewed");
    }
}
