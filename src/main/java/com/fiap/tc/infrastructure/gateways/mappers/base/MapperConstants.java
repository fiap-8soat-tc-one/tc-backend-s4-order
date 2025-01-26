package com.fiap.tc.infrastructure.gateways.mappers.base;

import com.fiap.tc.infrastructure.gateways.mappers.OrderHistoricMapper;
import com.fiap.tc.infrastructure.gateways.mappers.OrderItemMapper;
import com.fiap.tc.infrastructure.gateways.mappers.OrderListMapper;
import com.fiap.tc.infrastructure.gateways.mappers.OrderMapper;
import org.mapstruct.factory.Mappers;

public class MapperConstants {

    private MapperConstants() {
    }

    public static final OrderMapper ORDER_MAPPER = Mappers.getMapper(OrderMapper.class);

    public static final OrderListMapper ORDER_LIST_MAPPER = Mappers.getMapper(OrderListMapper.class);

    public static final OrderItemMapper ORDER_ITEM_MAPPER = Mappers.getMapper(OrderItemMapper.class);

    public static final OrderHistoricMapper ORDER_HISTORIC_MAPPER = Mappers.getMapper(OrderHistoricMapper.class);

}
