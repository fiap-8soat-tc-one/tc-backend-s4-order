package com.fiap.tc.infrastructure.presentation.workers.consumers;

import com.fiap.tc.application.usecases.order.UpdateStatusOrderUseCase;
import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;
import com.fiap.tc.infrastructure.core.amqp.mapping.Queues;
import com.fiap.tc.infrastructure.presentation.workers.dto.PaymentUpdatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import static com.fiap.tc.domain.enums.PaymentStatus.from;
import static java.util.UUID.fromString;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventUpdatedConsumer {

    private final UpdateStatusOrderUseCase updateStatusOrderUseCase;

    @RabbitListener(queues = Queues.PAYMENT_STATUS_UPDATED_QUEUE)
    public void dequeue(final Message<EventMessage<PaymentUpdatedMessage>> orderPaymentMessageMessage) {
        var message =
                orderPaymentMessageMessage.getPayload().getContent()
                        .stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("message without content"));

        log.info("payment updated payload: {}", message);

        updateStatusOrderUseCase.update(fromString(message.getTransactionNumber()),
                from(message.getStatus()).getOrderStatus());

    }
}
