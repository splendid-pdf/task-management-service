package ru.splendidpdf.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.api.dto.TaskInfoDto;
import ru.splendidpdf.config.properties.MqProperties;
import ru.splendidpdf.event.ImageCompressionEvent;
import ru.splendidpdf.event.ImageConversionEvent;
import ru.splendidpdf.event.ImageEvent;
import ru.splendidpdf.event.UpdatedTaskEvent;
import ru.splendidpdf.exception.NotFoundException;
import ru.splendidpdf.model.*;
import ru.splendidpdf.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private static final String TASK_NOT_FOUND_EXCEPTION_MESSAGE = "Task status not found by given id %s";
    private final MqProperties mqProperties;
    private final RabbitTemplate rabbitTemplate;
    private final TaskRepository taskRepository;
    private final ImageValidationService validationService;

    @SneakyThrows
    public String createImageConversionTask(MultipartFile image, ImageFormat from, ImageFormat to) {
        validationService.validateFileAndParametersForConversion(image, from, to);

        String taskId = UUID.randomUUID().toString();
        Task task = createTask(image, taskId, TaskType.CONVERSION);
        ImageEvent imageEvent = ImageEvent.builder()
                .taskType(TaskType.CONVERSION.name())
                .taskData(createImageConversionEvent(image, to, taskId))
                .build();

        runAsync(() -> rabbitTemplate.convertAndSend(
                mqProperties.getExchanges().getTasksExchange(),
                mqProperties.getRoutingKeys().getImageServiceKey(),
                imageEvent));

        taskRepository.save(task);

        return taskId;
    }

    @SneakyThrows
    public String createImageCompressionTask(MultipartFile image, CompressionType compressionType) {
        validationService.validateFileAndParametersForCompression(image, compressionType);

        String taskId = UUID.randomUUID().toString();
        Task task = createTask(image, taskId, TaskType.COMPRESSION);
        ImageEvent imageEvent = ImageEvent.builder()
                .taskType(TaskType.COMPRESSION.name())
                .taskData(createImageCompressionEvent(image, compressionType, taskId))
                .build();

        runAsync(() -> rabbitTemplate.convertAndSend(
                mqProperties.getExchanges().getTasksExchange(),
                mqProperties.getRoutingKeys().getImageServiceKey(),
                imageEvent));

        taskRepository.save(task);

        return taskId;
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(TASK_NOT_FOUND_EXCEPTION_MESSAGE.formatted(id)));
    }

    public TaskInfoDto getTaskInfoById(String id) {
        return taskRepository.findById(id)
                .map(task -> new TaskInfoDto(task.getStatus().name(), task.getResultUrl(), task.getCreatedAt().toString()))
                .orElseThrow(() ->
                        new NotFoundException("Task status not found by given id %s".formatted(id)));
    }

    public void updateTask(UpdatedTaskEvent event) {
        Task task = getTaskById(event.getTaskId());
        task.setStatus(event.getTaskStatus());
        task.setResultUrl(event.getResultUrl());
        task.setModifiedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @SneakyThrows
    private ImageConversionEvent createImageConversionEvent(MultipartFile image, ImageFormat to, String taskId) {
        return ImageConversionEvent.builder()
                .taskId(taskId)
                .fileName(image.getOriginalFilename())
                .convertTo(to.getKey())
                .encodedContent(Base64.getEncoder().encodeToString(image.getBytes()))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    @SneakyThrows
    private ImageCompressionEvent createImageCompressionEvent(MultipartFile image, CompressionType type, String taskId) {
        return ImageCompressionEvent.builder()
                .taskId(taskId)
                .fileName(image.getOriginalFilename())
                .compressFactor(type.name())
                .encodedContent(Base64.getEncoder().encodeToString(image.getBytes()))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    private Task createTask(MultipartFile image, String taskId, TaskType taskType) {
        return Task.builder()
                .id(taskId)
                .fileName(image.getOriginalFilename())
                .fileType(FileType.IMAGE)
                .taskType(taskType)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
