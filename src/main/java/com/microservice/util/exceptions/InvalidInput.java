package com.microservice.util.exceptions;

public class InvalidInput extends RuntimeException {
    public InvalidInput() {}

    public InvalidInput(String message) {
        super(message);
    }

    public InvalidInput(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInput(Throwable cause) {
        super(cause);
    }
}
