package com.fiap.tc.infrastructure.gateways.mappers;

import com.fiap.tc.domain.entities.OrderItem;
import com.fiap.tc.infrastructure.gateways.mappers.base.MapperEntity;
import com.fiap.tc.infrastructure.persistence.entities.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OrderItemMapper extends MapperEntity<OrderItemEntity, OrderItem> {

    @Override
    OrderItem fromEntity(OrderItemEntity orderItemEntity);

    @Override
    OrderItemEntity toEntity(OrderItem orderItem);
}


