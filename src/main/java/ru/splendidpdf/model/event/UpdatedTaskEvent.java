package ru.splendidpdf.model.event;

import lombok.Value;
import ru.splendidpdf.model.TaskStatus;

@Value
public class UpdatedTaskEvent {
    String taskId;
    TaskStatus taskStatus;
}