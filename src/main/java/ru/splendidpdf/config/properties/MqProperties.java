package ru.splendidpdf.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.mq")
public class MqProperties {
    private Queues queues;
    private Exchanges exchanges;
    private RoutingKeys routingKeys;

    @Getter
    @Setter
    public static class Exchanges  {
        private String tasksExchange;
    }

    @Getter
    @Setter
    public static class Queues {
        private String imageServiceQueue;
        private String documentServiceQueue;
        private String activityServiceQueue;
    }

    @Getter
    @Setter
    public static class RoutingKeys {
        private String imageServiceKey;
        private String documentServiceKey;
        private String activityServiceKey;
    }
}