package com.fiap.tc.adapter.repository.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "customer")
public class CustomerEntity {
    @Id
    @Column(name = "document", nullable = false, length = 11)
    private String document;

    @Column(name = "name", nullable = true, length = 20)
    private String name;

    @Column(name = "email", nullable = true)
    private String email;
}
