package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     * @param message The detail message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message and cause.
     * @param message The detail message.
     * @param cause The cause (which is saved for later retrieval by the Throwable.getCause() method).
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
