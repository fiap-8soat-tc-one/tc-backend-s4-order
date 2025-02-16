package com.fiap.tc.infrastructure.gateways;

import com.fiap.tc.application.gateways.OrderGatewaySpec;
import com.fiap.tc.domain.entities.Order;
import com.fiap.tc.domain.entities.OrderItem;
import com.fiap.tc.domain.entities.OrderList;
import com.fiap.tc.domain.enums.OrderStatus;
import com.fiap.tc.domain.exceptions.NotFoundException;
import com.fiap.tc.infrastructure.gateways.mappers.base.MapperConstants;
import com.fiap.tc.infrastructure.persistence.builders.OrderHistoricBuilder;
import com.fiap.tc.infrastructure.persistence.entities.OrderEntity;
import com.fiap.tc.infrastructure.persistence.entities.OrderItemEntity;
import com.fiap.tc.infrastructure.persistence.entities.embeddable.Audit;
import com.fiap.tc.infrastructure.persistence.repositories.OrderRepository;
import com.fiap.tc.infrastructure.services.ProductServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.sqids.Sqids;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.math.BigDecimal.valueOf;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@RequiredArgsConstructor
public class OrderGateway implements OrderGatewaySpec {
    public static final int ORDER_NUMBER_MIN_LENGTH = 4;
    private final OrderRepository repository;
    private final ProductServiceClient productServiceClient;

    @Override
    public Order load(UUID id) {
        var orderEntity = repository.findByUuid(id).orElseThrow(() ->
                new NotFoundException(format("Order id %s not found!", id)));

        return MapperConstants.ORDER_MAPPER.fromEntity(orderEntity);
    }

    @Override
    public Order register(Optional<UUID> idCustomer, List<OrderItem> listOfItems) {

        OrderEntity orderEntity = new OrderEntity();

        orderEntity.setAudit(Audit.builder().active(true).registerDate(LocalDateTime.now()).build());
        orderEntity.setUuid(UUID.randomUUID());

        idCustomer.ifPresent(orderEntity::setIdCustomer);

        orderEntity.setStatus(OrderStatus.RECEIVED);

        addItems(listOfItems, orderEntity);
        orderEntity.getOrderHistoric().add(OrderHistoricBuilder.create(orderEntity, orderEntity.getStatus()));

        return persist(orderEntity);
    }

    @Override
    public Page<OrderList> list(List<String> status, Pageable pageable) {
        var ordersEntity = repository.findByStatus(status, pageable);
        return ordersEntity.map(MapperConstants.ORDER_LIST_MAPPER::fromEntity);
    }

    @Override
    public void updateStatus(UUID idOrder, OrderStatus status) {
        var orderEntityOptional = repository.findByUuid(idOrder);

        if (orderEntityOptional.isEmpty()) {
            throw new NotFoundException(format("Order id %s not found!", idOrder));
        }

        var orderEntity = orderEntityOptional.get();
        orderEntity.getStatus().getValidator().validate(status);
        orderEntity.setStatus(status);
        orderEntity.getAudit().setUpdatedDate(LocalDateTime.now());
        orderEntity.getOrderHistoric().add(OrderHistoricBuilder.create(orderEntity, orderEntity.getStatus()));

        repository.save(orderEntity);
    }


    private Order persist(OrderEntity orderEntity) {
        var idOrder = repository.getNextSequenceValue();
        Sqids sqids = Sqids.builder().minLength(ORDER_NUMBER_MIN_LENGTH).build();
        var orderNumber = sqids.encode(List.of(idOrder.longValue()));
        orderEntity.setId(idOrder);
        orderEntity.setOrderNumber(orderNumber);

        return MapperConstants.ORDER_MAPPER.fromEntity(repository.save(orderEntity));
    }

    private void addItems(List<OrderItem> orderItems, OrderEntity orderEntity) {
        if (!isEmpty(orderItems)) {
            var itemsEntity = orderItems.stream().map(req -> buildOrderItemsEntity(req, orderEntity)).toList();
            orderEntity.setItems(itemsEntity);
            orderEntity.setTotal(itemsEntity.stream().map(OrderItemEntity::getTotal).reduce(BigDecimal.ZERO,
                    BigDecimal::add));
        }
    }

    private OrderItemEntity buildOrderItemsEntity(OrderItem orderItem, OrderEntity orderEntity) {

        var product = productServiceClient.load(orderItem.getIdProduct()).orElseThrow(() ->
                new NotFoundException(format("Product with id %s not found!", orderItem.getIdProduct())));

        var orderItemEntity = new OrderItemEntity();

        orderItemEntity.setOrder(orderEntity);
        orderItemEntity.setIdProduct(product.getId());
        orderItemEntity.setName(product.getName());
        orderItemEntity.setUnitValue(product.getPrice());
        orderItemEntity.setQuantity(orderItem.getQuantity());
        orderItemEntity.setTotal(calcItemTotal(orderItem, orderItemEntity));
        orderItemEntity.setAudit(Audit.builder().active(true).registerDate(LocalDateTime.now()).build());

        return orderItemEntity;


    }

    private BigDecimal calcItemTotal(OrderItem orderItemRequest, OrderItemEntity orderItemEntity) {
        return orderItemEntity.getUnitValue().multiply(valueOf(orderItemRequest.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
