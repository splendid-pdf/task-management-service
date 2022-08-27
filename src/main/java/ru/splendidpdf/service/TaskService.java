package ru.splendidpdf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.api.dto.TaskInfoDto;
import ru.splendidpdf.config.properties.MqProperties;
import ru.splendidpdf.exception.NotFoundException;
import ru.splendidpdf.exception.TaskStatusNotFoundException;
import ru.splendidpdf.model.*;
import ru.splendidpdf.model.event.ImageConversionEvent;
import ru.splendidpdf.model.event.UpdatedTaskEvent;
import ru.splendidpdf.repository.TaskRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private static final String TASK_NOT_FOUND_EXCEPTION_MESSAGE = "Task status not found by given id %s";
    private static final String FAILED_CONVERSION_EXCEPTION_MESSAGE = "Failed to convert payload to string";

    private final ObjectMapper objectMapper;
    private final MqProperties mqProperties;
    private final RabbitTemplate rabbitTemplate;
    private final TaskRepository taskRepository;
    private final ImageValidationService validationService;

    @SneakyThrows
    public String createImageConversionTask(MultipartFile image, ImageFormat from, ImageFormat to) {
        validationService.validateFileAndInputParams(image, from, to);

        String taskId = UUID.randomUUID().toString();

        ImageConversionEvent event = createImageConversionEvent(image, from, to, taskId);

        Task task = createTask(image, taskId);

        log.info("Image conversion task with id '{}' was created.", taskId);

        sendMessage(event,
                mqProperties.getExchanges().getTasksExchange(),
                mqProperties.getRoutingKeys().getImageConversionKey());

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

    private ImageConversionEvent createImageConversionEvent(MultipartFile image,
                                                            ImageFormat from,
                                                            ImageFormat to,
                                                            String taskId) throws IOException {
        return ImageConversionEvent.builder()
                .taskId(taskId)
                .fileName(image.getOriginalFilename())
                .formatTo(to.getKey())
                .formatFrom(from.getKey())
                .encodedContent(Base64.getEncoder().encodeToString(image.getBytes()))
                .timestamp(LocalDateTime.now().toString())
                .build();
    }

    private Task createTask(MultipartFile image, String taskId) {
        return Task.builder()
                .id(taskId)
                .fileName(image.getOriginalFilename())
                .fileType(FileType.IMAGE)
                .taskType(TaskType.CONVERSION)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    private void sendMessage(Object payload, String exchange, String routingKey) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }
}
