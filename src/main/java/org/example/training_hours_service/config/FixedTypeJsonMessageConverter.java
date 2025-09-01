package org.example.training_hours_service.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.lang.NonNull;


public class FixedTypeJsonMessageConverter extends MappingJackson2MessageConverter {

    private final JavaType targetType;

    public FixedTypeJsonMessageConverter(ObjectMapper objectMapper, Class<?> targetClass) {
        setObjectMapper(objectMapper);
        setTargetType(MessageType.TEXT);                // JSON text message
        this.targetType = objectMapper.getTypeFactory().constructType(targetClass);
    }

    @Override
    @NonNull
    protected JavaType getJavaTypeForMessage(@NonNull Message message) {
        return this.targetType;                         // always deserialize as TrainerHoursEvent
    }
}

