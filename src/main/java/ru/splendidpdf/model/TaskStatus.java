package ru.splendidpdf.model;

public enum TaskStatus {
    CREATED,

    ON_CONVERSION,
    ON_RESIZING,
    ON_EDITING,

    FAILED,
    FINISHED;
}