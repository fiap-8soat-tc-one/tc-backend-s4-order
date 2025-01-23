package com.fiap.tc.application.usecases.order;

import com.fiap.tc.application.gateways.OrderGatewaySpec;
import com.fiap.tc.application.gateways.PaymentLinkGatewaySpec;
import com.fiap.tc.domain.entities.Order;
import com.fiap.tc.domain.entities.OrderItem;
import com.fiap.tc.infrastructure.amqp.dto.OrderCreatedMessage;
import com.fiap.tc.infrastructure.core.amqp.EventPublisher;
import com.fiap.tc.infrastructure.core.amqp.builder.EventMessageBuilder;
import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;
import com.fiap.tc.infrastructure.core.amqp.mapping.Queues;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterOrderUseCase {
    private final OrderGatewaySpec orderGateway;
    private final PaymentLinkGatewaySpec paymentLinkGateway;
    private final EventPublisher eventPublisher;

    public Order register(UUID idCustomer, List<OrderItem> listOfItems) {
        var order = orderGateway.register(idCustomer, listOfItems);
        paymentLinkGateway.generate(order).ifPresent(order::setPaymentLink);
        var orderCreatedMessage = OrderCreatedMessage.builder().id(order.getId().toString()).build();
        publishOrderCreated(order, orderCreatedMessage);
        return order;
    }

    private void publishOrderCreated(Order order, OrderCreatedMessage orderCreatedMessage) {
        EventMessage<OrderCreatedMessage> message = new EventMessageBuilder()
                .trackingId(String.format("order-created-queue-id-%s", order.getId().toString()))
                .content(List.of(orderCreatedMessage))
                .creationDate()
                .build();
        eventPublisher.execute(message, Queues.ORDER_CREATED_QUEUE);
    }
}
