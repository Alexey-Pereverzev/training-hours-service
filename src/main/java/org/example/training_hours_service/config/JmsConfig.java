package org.example.training_hours_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;


@Configuration
@EnableJms
public class JmsConfig {
    public static final String TRAINING_QUEUE = "training.events.queue";
    private static final Logger jmsLog = LoggerFactory.getLogger("JMS");


    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        return new FixedTypeJsonMessageConverter(objectMapper, TrainerHoursEvent.class);
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setPubSubDomain(false);                                 // false = queue, true = topic
        jmsTemplate.setMessageConverter(messageConverter);                  // JSON via Jackson
        jmsTemplate.setDeliveryPersistent(true);
        return jmsTemplate;
    }

    @Bean                                                                   // centralize logging
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(ConnectionFactory connectionFactory,
                                                                          MessageConverter messageConverter) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setSessionTransacted(true);
        factory.setErrorHandler(throwable -> jmsLog.error("JMS listener error", throwable));
        return factory;
    }
}
