package ru.splendidpdf.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@RedisHash("Task")
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