package com.fiap.tc.infrastructure.persistence.entities;

import com.fiap.tc.infrastructure.persistence.entities.embeddable.Audit;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "customer", schema = "public",
        indexes = {
                @Index(name = "customer_index_document", columnList = "document"),
        })
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID uuid;

    private String name;

    @Column(name = "document", unique = true, length = 20)
    private String document;

    private String email;

    @Embedded
    private Audit audit;
}
