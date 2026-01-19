package com.deployrr.api.task;

public class TaskResult {

    private final boolean success;
    private final Exception exception;

    public TaskResult(boolean success) {
        this.success = success;
        this.exception = null;
    }

    public TaskResult(boolean success, Exception exception) {
        this.success = success;
        this.exception = exception;
    }

    public static TaskResult success() {
        return new TaskResult(true);
    }

    public static TaskResult failure(Exception ex) {
        return new TaskResult(false, ex);
    }

    public boolean isSuccess() {
        return success;
    }

    public Exception getException() {
        return exception;
    }

}
