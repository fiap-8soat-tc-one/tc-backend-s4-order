package com.fiap.tc.application.usecases.order;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.application.gateways.OrderGatewaySpec;
import com.fiap.tc.application.gateways.PaymentLinkGatewaySpec;
import com.fiap.tc.domain.entities.Customer;
import com.fiap.tc.domain.entities.Order;
import com.fiap.tc.domain.entities.OrderItem;
import com.fiap.tc.domain.exceptions.NotFoundException;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.core.amqp.EventPublisher;
import com.fiap.tc.infrastructure.services.CustomerServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterOrderUseCaseTest extends FixtureTest {
    @Mock
    private OrderGatewaySpec orderGatewaySpecMock;
    @Mock
    private PaymentLinkGatewaySpec paymentLinkGatewaySpecMock;
    @Mock
    private CustomerServiceClient customerServiceClientMock;
    @Mock
    private EventPublisher eventPublisherMock;

    @InjectMocks
    private RegisterOrderUseCase registerOrderUseCase;

    private String document;
    private String paymentLink;
    private Customer customer;
    private List<OrderItem> orderItems;
    private Order order;

    @BeforeEach
    public void setUp() {
        document = "111111111";
        paymentLink = "payment-link";
        orderItems = Fixture.from(OrderItem.class).gimme(2, "valid");
        customer = Fixture.from(Customer.class).gimme("valid");
        order = Fixture.from(Order.class).gimme("valid");

    }

    @Test
    public void registerOrderTest() {
        // Arrange
        when(paymentLinkGatewaySpecMock.generate(any())).thenReturn(Optional.of(paymentLink));
        when(orderGatewaySpecMock.register(any(), any())).thenReturn(order);
        when(customerServiceClientMock.load(document)).thenReturn(Optional.of(customer));

        // Act
        var orderResult = registerOrderUseCase.register(document, orderItems);

        //Assertions
        assertEquals(order, orderResult);
        verify(eventPublisherMock, times(1)).execute(any(), any());
        verify(customerServiceClientMock, times(1)).load(document);
        verify(paymentLinkGatewaySpecMock, times(1)).generate(any());
        verify(orderGatewaySpecMock, times(1)).register(any(), any());

    }

    @Test
    public void registerOrderWhenCustomerNotFoundTest() {
        // Arrange
        when(customerServiceClientMock.load(document)).thenReturn(Optional.empty());

        // Act
        var assertThrows = Assertions.assertThrows(NotFoundException.class,
                () -> registerOrderUseCase.register(document, orderItems));


        //Assertions
        assertTrue(assertThrows.getMessage().contains("not found"));

    }

    @Test
    public void registerOrder_WhenDocumentIsEmptyTest() {
        // Arrange
        when(paymentLinkGatewaySpecMock.generate(any())).thenReturn(Optional.of(paymentLink));
        when(orderGatewaySpecMock.register(any(), any())).thenReturn(order);
        // Act
        var orderResult = registerOrderUseCase.register(null, orderItems);

        //Assertions
        assertEquals(order, orderResult);
        verify(eventPublisherMock, times(1)).execute(any(), any());
        verify(paymentLinkGatewaySpecMock, times(1)).generate(any());
        verify(orderGatewaySpecMock, times(1)).register(any(), any());

    }


}