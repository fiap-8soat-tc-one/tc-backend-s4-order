package com.fiap.tc.application.usecases.order;

import com.fiap.tc.application.gateways.OrderGatewaySpec;
import com.fiap.tc.application.gateways.PaymentLinkGatewaySpec;
import com.fiap.tc.domain.entities.Order;
import com.fiap.tc.domain.entities.OrderItem;
import com.fiap.tc.domain.exceptions.NotFoundException;
import com.fiap.tc.infrastructure.core.amqp.EventPublisher;
import com.fiap.tc.infrastructure.core.amqp.builder.EventMessageBuilder;
import com.fiap.tc.infrastructure.core.amqp.dto.EventMessage;
import com.fiap.tc.infrastructure.core.amqp.mapping.Queues;
import com.fiap.tc.infrastructure.presentation.workers.dto.OrderCreatedMessage;
import com.fiap.tc.infrastructure.services.CustomerServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class RegisterOrderUseCase {
    private final OrderGatewaySpec orderGateway;
    private final PaymentLinkGatewaySpec paymentLinkGateway;
    private final CustomerServiceClient customerServiceClient;
    private final EventPublisher eventPublisher;

    public Order register(String document, List<OrderItem> listOfItems) {
        Optional<UUID> idCustomer = Optional.empty();
        if (nonNull(document)) {
            var customer = customerServiceClient.load(document).orElseThrow(() ->
                    new NotFoundException(format("Customer %s not found!", document)));
            idCustomer = Optional.of(customer.getId());
        }

        var order = orderGateway.register(idCustomer, listOfItems);
        paymentLinkGateway.generate(order).ifPresent(order::setPaymentLink);
        var orderCreatedMessage = OrderCreatedMessage.builder().id(order.getId().toString()).build();
        publishOrderCreated(order, orderCreatedMessage);
        return order;
    }

    private void publishOrderCreated(Order order, OrderCreatedMessage orderCreatedMessage) {
        EventMessage<OrderCreatedMessage> message = new EventMessageBuilder()
                .content(List.of(orderCreatedMessage))
                .creationDate()
                .build();
        eventPublisher.execute(message, Queues.ORDER_CREATED_QUEUE, format("order-created-queue-id-%s", order.getId().toString()));
    }
}
