package ru.splendidpdf.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.splendidpdf.event.UpdatedTaskEvent;
import ru.splendidpdf.model.TaskStatus;

@Configuration
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(UpdatedTaskEvent.class, new UpdatedTaskEventDeserializer());
        objectMapper.registerModule(simpleModule);

        return objectMapper;
    }

    static class UpdatedTaskEventDeserializer extends JsonDeserializer<UpdatedTaskEvent> {

        @Override
        @SneakyThrows
        public UpdatedTaskEvent deserialize(JsonParser parser, DeserializationContext context) {
            JsonNode node = parser.getCodec().readTree(parser);
            String taskId = node.get("taskId").asText();
            String resultUrl = node.get("resultUrl").asText();
            String taskStatus = node.get("taskStatus").asText();
            return UpdatedTaskEvent.builder()
                    .taskId(taskId)
                    .resultUrl(resultUrl)
                    .taskStatus(Enum.valueOf(TaskStatus.class, taskStatus))
                    .build();
        }
    }
}
