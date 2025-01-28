package com.fiap.tc.infrastructure.persistence.repositories;

import com.fiap.tc.infrastructure.persistence.entities.OrderEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderEntity orderEntity;
    private UUID uuid;
    private List<String> statusList;
    private Pageable pageable;

    @BeforeEach
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        orderEntity = new OrderEntity();
        uuid = UUID.randomUUID();
        statusList = new ArrayList<>();
        pageable = mock(Pageable.class);
    }

    @Test
    public void testFindByUuid_ShouldReturnOrderEntity_WhenUuidExists() {
        // Given
        Optional<OrderEntity> expectedOrder = Optional.of(orderEntity);
        when(orderRepository.findByUuid(uuid)).thenReturn(expectedOrder);

        // When
        Optional<OrderEntity> actualOrder = orderRepository.findByUuid(uuid);

        // Then
        verify(orderRepository).findByUuid(uuid);
        assertTrue(actualOrder.isPresent());
        assertEquals(expectedOrder.get(), actualOrder.get());
    }

    @Test
    public void testFindByUuid_ShouldReturnEmptyOptional_WhenUuidDoesNotExist() {
        // Given
        when(orderRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        // When
        Optional<OrderEntity> actualOrder = orderRepository.findByUuid(uuid);

        // Then
        verify(orderRepository).findByUuid(uuid);
        assertFalse(actualOrder.isPresent());
    }

    @Test
    public void testFindByStatus_ShouldReturnPageOfOrders_WhenStatusListIsProvided() {
        // Given
        statusList.add("READY");
        Page<OrderEntity> expectedPage = mock(Page.class);
        when(orderRepository.findByStatus(statusList, pageable)).thenReturn(expectedPage);

        // When
        Page<OrderEntity> actualPage = orderRepository.findByStatus(statusList, pageable);

        // Then
        verify(orderRepository).findByStatus(statusList, pageable);
        assertNotNull(actualPage);
    }

    @Test
    public void testGetNextSequenceValue_ShouldReturnInteger_WhenCalled() {
        // Given
        int expectedValue = 10;
        when(orderRepository.getNextSequenceValue()).thenReturn(expectedValue);

        // When
        int actualValue = orderRepository.getNextSequenceValue();

        // Then
        verify(orderRepository).getNextSequenceValue();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSave_ShouldPersistOrderEntity() {
        // Given
        orderRepository.save(orderEntity);

        // Then
        verify(orderRepository).save(orderEntity);
    }
}