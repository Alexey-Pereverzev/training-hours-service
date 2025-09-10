package org.example.training_hours_service.service.impl;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DeadLetterConsumerTest {

    private DeadLetterConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new DeadLetterConsumer();
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }


    @Test
    void whenHandleDeadLetter_validMessage_shouldLogAndClearMDC() throws Exception {
        // given
        Message message = mock(Message.class);
        when(message.getBody(String.class)).thenReturn("bad message");
        Destination dest = mock(Destination.class);
        when(dest.toString()).thenReturn("DLQ.training.events.queue");
        when(message.getJMSDestination()).thenReturn(dest);
        // when
        consumer.handleDeadLetter(message);
        // then
        assertNull(MDC.get("queue"), "MDC should be cleared after processing");
        verify(message).getBody(String.class);
        verify(message).getJMSDestination();
    }

    @Test
    void whenHandleDeadLetter_messageThrowsException_shouldLogAndClearMDC() throws Exception {
        // given
        Message message = mock(Message.class);
        when(message.getBody(String.class)).thenThrow(new JMSException("fail"));
        // when
        consumer.handleDeadLetter(message);
        // then
        assertNull(MDC.get("queue"), "MDC should still be cleared after exception");
        verify(message).getBody(String.class);
    }
}

