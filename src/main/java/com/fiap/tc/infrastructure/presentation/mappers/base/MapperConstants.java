package com.fiap.tc.infrastructure.presentation.mappers.base;

import com.fiap.tc.infrastructure.presentation.mappers.OrderItemRequestMapper;
import com.fiap.tc.infrastructure.presentation.mappers.OrderListResponseMapper;
import com.fiap.tc.infrastructure.presentation.mappers.OrderResponseMapper;
import org.mapstruct.factory.Mappers;

public class MapperConstants {

    private MapperConstants() {
    }

    public static final OrderListResponseMapper ORDER_LIST_MAPPER = Mappers.getMapper(OrderListResponseMapper.class);
    public static final OrderItemRequestMapper ORDER_ITEM_MAPPER = Mappers.getMapper(OrderItemRequestMapper.class);
    public static final OrderResponseMapper ORDER_RESPONSE_MAPPER = Mappers.getMapper(OrderResponseMapper.class);


}

