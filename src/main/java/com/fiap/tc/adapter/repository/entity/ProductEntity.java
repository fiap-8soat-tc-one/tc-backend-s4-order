package com.fiap.tc.adapter.repository.entity;

import com.fiap.tc.adapter.repository.entity.embeddable.Audit;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;


@Data
@Entity
@Table(name = "product")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID uuid;

    private String name;

    private String description;

    private BigDecimal price;

    //TODO add list image to product

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_category", nullable = false)
    private CategoryEntity category;

    @Embedded
    private Audit audit;

}
