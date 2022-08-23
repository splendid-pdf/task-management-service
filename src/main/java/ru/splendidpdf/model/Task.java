package ru.splendidpdf.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@RedisHash("Task")
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    private String id;
    private String fileName;
    private FileType fileType;
    private TaskType taskType;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @SneakyThrows
    public static Task createNewTask(MultipartFile file, FileType fileType, TaskType taskType) {
        return new Task(
                UUID.randomUUID().toString(),
                file.getOriginalFilename(),
                fileType,
                taskType,
                TaskStatus.CREATED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
