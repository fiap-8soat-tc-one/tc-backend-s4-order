package com.fiap.tc.infrastructure.persistence.entities;

import com.fiap.tc.infrastructure.persistence.entities.embeddable.Audit;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "order_item")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_product", nullable = false)
    private UUID idProduct;

    @Column(name = "name")
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_order", nullable = false)
    private OrderEntity order;

    private Integer quantity;

    @Column(name = "unit_value")
    private BigDecimal unitValue;

    private BigDecimal total;

    @Embedded
    private Audit audit;

}
