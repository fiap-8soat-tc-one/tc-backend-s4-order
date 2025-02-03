package com.fiap.tc.infrastructure.presentation.workers.consumers;

import com.fiap.tc.application.usecases.order.UpdateStatusOrderUseCase;
import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;
import com.fiap.tc.infrastructure.presentation.workers.dto.PaymentUpdatedMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentEventUpdatedConsumerTest {

    @Mock
    private UpdateStatusOrderUseCase updateStatusOrderUseCaseMock;

    @InjectMocks
    private PaymentEventUpdatedConsumer paymentEventUpdatedConsumer;

    @Test
    public void shouldUpdateOrderStatus_WhenPaymentUpdatedMessageReceived() {
        // Given
        String trackingId = "12345";
        PaymentUpdatedMessage paymentUpdatedMessage = new PaymentUpdatedMessage();
        paymentUpdatedMessage.setTransactionNumber("d44c20f4-abd1-4e45-a891-fda503de8cc7");
        paymentUpdatedMessage.setStatus("APPROVED");

        EventMessage<PaymentUpdatedMessage> eventMessage = new EventMessage<>();
        eventMessage.setContent(List.of(paymentUpdatedMessage));

        Message<EventMessage<PaymentUpdatedMessage>> message = mock(Message.class);
        when(message.getPayload()).thenReturn(eventMessage);


        // When
        paymentEventUpdatedConsumer.dequeue(message, trackingId);

        // Then
        verify(updateStatusOrderUseCaseMock).update(Mockito.any(), Mockito.any());
    }

    @Test
    public void shouldThrowRuntimeException_WhenMessageContentIsEmpty() {
        // Given
        EventMessage<PaymentUpdatedMessage> emptyMessage = new EventMessage<>();
        emptyMessage.setContent(Collections.emptyList());

        Message<EventMessage<PaymentUpdatedMessage>> message = mock(Message.class);
        when(message.getPayload()).thenReturn(emptyMessage);

        // When
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            paymentEventUpdatedConsumer.dequeue(message, "trackingId");
        });

        // Then
        assertEquals("message without content", exception.getMessage());
    }

}