package com.deployrr.api.task.validation;

public class TaskValidationException extends Exception {
    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException() {
    }

    public TaskValidationException(Throwable cause) {
        super(cause);
    }

    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
