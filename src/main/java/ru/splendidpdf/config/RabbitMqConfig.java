package ru.splendidpdf.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.splendidpdf.config.properties.MqProperties;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {
    private final MqProperties properties;
    private final ConnectionFactory connectionFactory;

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory consumerBatchContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean("tasks-exchange")
    public TopicExchange topicExchange() {
        return new TopicExchange(properties.getExchanges().getTasksExchange());
    }

    @Bean("image-conversion-queue")
    public Queue queue() {
        return new Queue(properties.getQueues().getImageConversionQueue());
    }

    @Bean("image-conversion-binding")
    public Binding binding() {
        return BindingBuilder
                .bind(queue())
                .to(topicExchange())
                .with(properties.getRoutingKeys().getImageConversionKey());
    }
}
