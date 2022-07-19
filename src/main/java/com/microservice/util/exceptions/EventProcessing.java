package com.microservice.util.exceptions;

public class EventProcessing extends RuntimeException {
    public EventProcessing() {
    }

    public EventProcessing(String message) {
        super(message);
    }

    public EventProcessing(String message, Throwable cause) {
        super(message, cause);
    }

    public EventProcessing(Throwable cause) {
        super(cause);
    }
}