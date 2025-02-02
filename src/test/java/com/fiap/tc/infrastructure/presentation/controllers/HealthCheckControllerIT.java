package com.fiap.tc.infrastructure.presentation.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class HealthCheckControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void findOrderTest() throws Exception {
        mockMvc.perform(get("/api/public/v1/health")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("Health"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

}