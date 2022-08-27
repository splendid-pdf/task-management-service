package ru.splendidpdf.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.splendidpdf.model.event.UpdatedTaskEvent;
import ru.splendidpdf.service.TaskService;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskStatusChangeListener {
    private static final String ERROR_MESSAGE = "Failed to map a message to UpdatedTaskEvent.class";

    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            queues = "${app.mq.queues.task-status-change-queue}",
            containerFactory = "consumerBatchContainerFactory")
    public void updateTaskStatus(String message) {
        try {
            log.info("Get message to update task: {}", message);
            taskService.updateTask(objectMapper.readValue(message, UpdatedTaskEvent.class));
        } catch (JsonProcessingException e) {
            log.error(ERROR_MESSAGE);
            throw new AmqpRejectAndDontRequeueException(ERROR_MESSAGE);
        }
    }
}
