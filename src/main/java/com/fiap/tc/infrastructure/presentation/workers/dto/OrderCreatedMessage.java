package com.fiap.tc.infrastructure.presentation.workers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreatedMessage implements Serializable {
    private String id;
}
