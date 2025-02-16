package com.fiap.tc.fixture.templates;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;
import br.com.six2six.fixturefactory.loader.TemplateLoader;
import com.fiap.tc.domain.entities.Order;
import com.fiap.tc.domain.entities.OrderItem;
import com.fiap.tc.domain.enums.OrderStatus;
import com.fiap.tc.infrastructure.persistence.entities.OrderEntity;
import com.fiap.tc.infrastructure.persistence.entities.OrderItemEntity;
import com.fiap.tc.infrastructure.persistence.entities.embeddable.Audit;
import com.fiap.tc.infrastructure.presentation.requests.OrderItemRequest;
import com.fiap.tc.infrastructure.presentation.requests.OrderRequest;
import com.fiap.tc.infrastructure.presentation.requests.OrderStatusRequest;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderTemplates implements TemplateLoader {

    @Override
    public void load() {

        Fixture.of(OrderEntity.class).addTemplate("valid", new Rule() {
            {
                add("id", random(Integer.class, range(1, 100)));
                add("uuid", UUID.randomUUID());
                add("idCustomer", UUID.randomUUID());
                add("status", random(OrderStatus.RECEIVED, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.FINISHED, OrderStatus.PENDING));
                add("audit", one(Audit.class, "valid"));
                add("items", has(2).of(OrderItemEntity.class, "valid"));
                add("total", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));
            }
        });

        Fixture.of(Order.class).addTemplate("valid", new Rule() {
            {
                add("id", UUID.randomUUID());
                add("orderNumber", random("1234", "5678"));
                add("status", random(OrderStatus.RECEIVED));
                add("total", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));
            }
        });

        Fixture.of(OrderItem.class).addTemplate("valid", new Rule() {
            {
                add("idProduct", UUID.randomUUID());
                add("name", random("hamburger", "cake", "pizza"));
                add("quantity", random(Integer.class, range(1, 10)));
                add("unitValue", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));
                add("total", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));

            }
        });

        Fixture.of(OrderItemEntity.class).addTemplate("valid", new Rule() {
            {
                add("id", random(Integer.class, range(1, 100)));
                add("idProduct", UUID.randomUUID());
                add("name", random("hamburger", "cake", "pizza"));
                add("quantity", random(Integer.class, range(1, 10)));
                add("unitValue", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));
                add("total", random(BigDecimal.valueOf(100.50), BigDecimal.valueOf(200.75)));
                add("audit", one(Audit.class, "valid"));
            }
        });

        Fixture.of(OrderRequest.class).addTemplate("valid", new Rule() {
            {
                add("orderItems", has(2).of(OrderItemRequest.class, "valid"));
            }
        });

        Fixture.of(OrderItemRequest.class).addTemplate("valid", new Rule() {
            {
                add("idProduct", UUID.randomUUID());
                add("quantity", random(Integer.class, range(1, 10)));

            }
        });

        Fixture.of(OrderStatusRequest.class).addTemplate("valid", new Rule() {
            {
                add("id", UUID.randomUUID());
                add("status", random(OrderStatus.READY, OrderStatus.FINISHED));
            }
        });

    }
}
