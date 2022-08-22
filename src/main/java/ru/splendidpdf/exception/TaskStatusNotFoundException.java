package ru.splendidpdf.exception;

public class TaskStatusNotFoundException extends RuntimeException {

    public TaskStatusNotFoundException(String message) {
        super(message);
    }
}
