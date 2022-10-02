package ru.splendidpdf.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@RedisHash(value = "Task", timeToLive = 3600)
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    private String id;
    private String fileName;
    private String resultUrl;
    private FileType fileType;
    private TaskType taskType;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}