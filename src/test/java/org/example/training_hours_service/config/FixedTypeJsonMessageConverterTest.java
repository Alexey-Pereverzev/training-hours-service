package org.example.training_hours_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Message;
import jakarta.jms.Session;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.example.training_hours_service.dto.EventType;
import org.example.training_hours_service.dto.TrainerHoursEvent;
import org.example.training_hours_service.dto.TrainingUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
class FixedTypeJsonMessageConverterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FixedTypeJsonMessageConverter converter =
            new FixedTypeJsonMessageConverter(objectMapper, TrainerHoursEvent.class);

    @Test
    void roundTrip() throws Exception {
        // given
        TrainerHoursEvent event = TrainerHoursEvent.builder()
                .txId("tx-1").type(EventType.UPDATE)
                .trainingUpdate(new TrainingUpdateRequest())
                .build();
        Session session = mock(Session.class);          // mock JMS Session
        when(session.createTextMessage(anyString())).thenAnswer(i -> {
            var textMessage = new ActiveMQTextMessage();
            textMessage.setText(i.getArgument(0));
            return textMessage;
        });
        // when
        Message jms = converter.toMessage(event, session);
        TrainerHoursEvent back = (TrainerHoursEvent) converter.fromMessage(jms);
        // then
        assertEquals(event.getTxId(), back.getTxId());
        assertEquals(event.getType(), back.getType());
    }
}

