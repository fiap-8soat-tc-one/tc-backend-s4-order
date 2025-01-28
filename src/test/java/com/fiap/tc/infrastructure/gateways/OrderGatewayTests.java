package com.fiap.tc.infrastructure.gateways;

import br.com.six2six.fixturefactory.Fixture;
import com.fiap.tc.domain.entities.Product;
import com.fiap.tc.domain.enums.OrderStatus;
import com.fiap.tc.domain.exceptions.NotFoundException;
import com.fiap.tc.fixture.FixtureTest;
import com.fiap.tc.infrastructure.persistence.entities.OrderEntity;
import com.fiap.tc.infrastructure.persistence.repositories.OrderRepository;
import com.fiap.tc.infrastructure.presentation.requests.OrderRequest;
import com.fiap.tc.infrastructure.presentation.requests.OrderStatusRequest;
import com.fiap.tc.infrastructure.services.ProductServiceClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.fiap.tc.infrastructure.presentation.mappers.base.MapperConstants.ORDER_ITEM_MAPPER;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderGatewayTests extends FixtureTest {

    public static final UUID UUID = randomUUID();

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderGateway orderGateway;

    private OrderStatusRequest statusRequest;

    private List<String> status;

    private OrderEntity orderEntity;

    private OrderRequest orderRequest;

    private Product product;

    private Optional<UUID> idCustomerOptional;

    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        orderEntity = Fixture.from(OrderEntity.class).gimme("valid");
        product = Fixture.from(Product.class).gimme("valid");
        orderRequest = Fixture.from(OrderRequest.class).gimme("valid");
        pageable = Mockito.mock(Pageable.class);
        statusRequest = Fixture.from(OrderStatusRequest.class).gimme("valid");
        idCustomerOptional = Optional.of(randomUUID());
    }

    @Test
    public void listOrdersByStatusTest() {

        final var orderEntities = new PageImpl<>(List.of(orderEntity));
        when(orderRepository.findByStatus(status, pageable)).thenReturn(orderEntities);

        var ordersPageable = orderGateway.list(status, pageable);

        assertEquals(1, ordersPageable.getSize());
        verify(orderRepository).findByStatus(status, pageable);

    }

    @Test
    public void loadOrderTest() {
        when(orderRepository.findByUuid(UUID)).thenReturn(Optional.of(orderEntity));

        var order = orderGateway.load(UUID);
        assertNotNull(order);
        verify(orderRepository).findByUuid(UUID);

    }

    @Test
    public void loadOrderWhenOrderNotFoundTest() {

        when(orderRepository.findByUuid(UUID)).thenReturn(Optional.empty());

        var assertThrows = Assertions.assertThrows(NotFoundException.class,
                () -> orderGateway.load(UUID));

        assertTrue(assertThrows.getMessage().contains("not found"));
    }

    @Test
    public void updateOrderStatusWhenOrderNotFoundTest() {

        when(orderRepository.findByUuid(statusRequest.getId())).thenReturn(Optional.empty());

        var assertThrows = Assertions.assertThrows(NotFoundException.class,
                () -> orderGateway.updateStatus(statusRequest.getId(), statusRequest.getStatus()));

        assertTrue(assertThrows.getMessage().contains("not found"));
    }

    @Test
    public void updateOrderStatusTest() {
        orderEntity.setStatus(OrderStatus.RECEIVED);
        statusRequest.setStatus(OrderStatus.PREPARING);

        when(orderRepository.findByUuid(statusRequest.getId())).thenReturn(Optional.of(orderEntity));
        when(orderRepository.save(Mockito.any())).thenReturn(orderEntity);

        orderGateway.updateStatus(statusRequest.getId(), statusRequest.getStatus());

        verify(orderRepository).findByUuid(statusRequest.getId());
        verify(orderRepository).save(Mockito.any());
    }

    @Test
    public void saveOrderTest() {
        var idProduct = orderRequest.getOrderItems().get(0).getIdProduct();
        when(orderRepository.save(Mockito.any())).thenReturn(orderEntity);
        when(productServiceClient.load(Mockito.any())).thenReturn(Optional.of(product));

        var orderList = orderRequest.getOrderItems().stream().map(ORDER_ITEM_MAPPER::toDomain).toList();
        var order = orderGateway.register(idCustomerOptional, orderList);

        assertNotNull(order);
        verify(productServiceClient, times(2)).load(idProduct);
        verify(orderRepository).save(Mockito.any());

    }

    @Test
    public void saveOrderWhenEmptyItemsTest() {
        orderRequest.setOrderItems(Collections.emptyList());
        when(orderRepository.save(Mockito.any())).thenReturn(orderEntity);

        var orderList = orderRequest.getOrderItems().stream().map(ORDER_ITEM_MAPPER::toDomain).toList();
        var order = orderGateway.register(idCustomerOptional, orderList);

        assertNotNull(order);
        verify(orderRepository, times(1)).save(Mockito.any());

    }

    @Test
    public void launchExceptionWhenProductNotExistTest() {

        var idProduct = orderRequest.getOrderItems().get(0).getIdProduct();

        when(productServiceClient.load(idProduct)).thenReturn(Optional.empty());

        var orderList = orderRequest.getOrderItems().stream().map(ORDER_ITEM_MAPPER::toDomain).toList();
        var assertThrows = Assertions.assertThrows(NotFoundException.class,
                () -> orderGateway.register(idCustomerOptional, orderList));

        assertTrue(assertThrows.getMessage().contains("not found"));
    }

}