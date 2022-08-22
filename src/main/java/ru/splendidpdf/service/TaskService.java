package ru.splendidpdf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mapping.MappingException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.splendidpdf.config.properties.MqProperties;
import ru.splendidpdf.exception.TaskStatusNotFoundException;
import ru.splendidpdf.model.FileType;
import ru.splendidpdf.model.ImageFormat;
import ru.splendidpdf.model.Task;
import ru.splendidpdf.model.TaskStatus;
import ru.splendidpdf.model.TaskType;
import ru.splendidpdf.model.event.ImageConversionEvent;
import ru.splendidpdf.model.event.UpdatedTaskEvent;
import ru.splendidpdf.repository.TaskRepository;

import java.time.LocalDateTime;

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

    public String createImageConversionTask(MultipartFile image, ImageFormat from, ImageFormat to) {
        validationService.validateFileAndInputParams(image, from, to);

        Task task = Task.createNewTask(image, FileType.IMAGE, TaskType.CONVERSION);

        String taskId = task.getId();

        log.info("Image conversion task with id '{}' was created.", taskId);

        sendMessage(
                ImageConversionEvent.createEvent(image, taskId, from, to),
                mqProperties.getExchanges().getTasksExchange(),
                mqProperties.getRoutingKeys().getImageConversionKey());

        taskRepository.save(task);

        return taskId;
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() ->
                        new TaskStatusNotFoundException(TASK_NOT_FOUND_EXCEPTION_MESSAGE.formatted(id)));
    }

    public TaskStatus getTaskStatusById(String id) {
        return taskRepository.findById(id)
                .map(Task::getStatus)
                .orElseThrow(() ->
                        new TaskStatusNotFoundException("Task status not found by given id %s".formatted(id)));
    }

    public void updateTask(UpdatedTaskEvent event) {
        Task task = getTaskById(event.getTaskId());
        task.setStatus(event.getTaskStatus());
        task.setModifiedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    private void sendMessage(Object payload, String exchange, String routingKey) {
        try {
            String message = objectMapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (JsonProcessingException e) {
            log.error(FAILED_CONVERSION_EXCEPTION_MESSAGE);
            throw new MappingException(FAILED_CONVERSION_EXCEPTION_MESSAGE);
        }
    }
}
