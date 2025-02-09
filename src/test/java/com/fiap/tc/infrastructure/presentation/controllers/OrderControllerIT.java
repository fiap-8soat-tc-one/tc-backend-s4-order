package com.fiap.tc.infrastructure.presentation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tc.infrastructure.presentation.response.OrderResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.fiap.tc.util.TestUtils.readResourceFileAsString;
import static java.lang.String.format;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class OrderControllerIT {

    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJteWxsZXIiLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwicHJvZmlsZSI6IkFETUlOSVNUUkFUT1IiLCJuYW1lIjoiTXlsbGVyIFNha2FndWNoaSIsImV4cCI6MTczOTIwMTg4OSwidXVpZCI6IjM0ODQ4ZTIwLTk2NzktMTFlYi05ZTEzLTAyNDJhYzExMDAwMiIsImF1dGhvcml0aWVzIjpbIkRFTEVURV9DVVNUT01FUlMiLCJSRUdJU1RFUl9PUkRFUlMiLCJMSVNUX1VTRVJTIiwiU0VBUkNIX09SREVSUyIsIkVESVRfT1JERVJTIiwiU0VBUkNIX1BST0RVQ1RTIiwiRURJVF9VU0VSUyIsIkRFTEVURV9QUk9EVUNUUyIsIkRFTEVURV9PUkRFUlMiLCJSRUdJU1RFUl9DVVNUT01FUlMiLCJERUxFVEVfVVNFUlMiLCJMSVNUX1BST0RVQ1RTIiwiU0VBUkNIX0NBVEVHT1JJRVMiLCJMSVNUX0NBVEVHT1JJRVMiLCJMSVNUX0NVU1RPTUVSUyIsIlVQREFURV9TVEFUVVNfT1JERVJTIiwiTElTVF9PUkRFUlMiLCJFRElUX0NVU1RPTUVSUyIsIlJFR0lTVEVSX1VTRVJTIiwiU0VBUkNIX0NVU1RPTUVSUyIsIkVESVRfQ0FURUdPUklFUyIsIlJFR0lTVEVSX0NBVEVHT1JJRVMiLCJQUk9DRVNTX1BBWU1FTlRTIiwiREVMRVRFX0NBVEVHT1JJRVMiLCJSRUdJU1RFUl9QUk9EVUNUUyIsIkVESVRfUFJPRFVDVFMiLCJTRUFSQ0hfVVNFUlMiXSwianRpIjoiMzA4Y2RhMGMtMzk5Mi00MzZkLTllMGQtNzVjZTNjMjY3ZGZiIiwiY2xpZW50X2lkIjoidGNfY2xpZW50In0.QaiYu3HxrAVzQDOV0JFqnRK1H1LRS-1pRcM1XiyhJqg";
    private static final String CUSTOMER_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4ODQwNDA3MTAzOSIsImlkIjoiM2ZhODVmNjQtNTcxNy00NTYyLWIzZmMtMmM5NjNmNjZhZmE2IiwibmFtZSI6Ik15bGxlciBTYWthZ3VjaGkiLCJlbWFpbCI6Im15bGxlcnNha2FndWNoaUBnbWFpbC5jb20iLCJkb2N1bWVudCI6Ijg4NDA0MDcxMDM5IiwiaWF0IjoxNzM5MTE1NTI0LCJleHAiOjE3MzkxMTkxMjR9.grf1C-PQKciSt3lT8kdG8jyTpDs34DRCwbmoov50mXcrnc3ezvrN3LY53qX6BCjlb2GN6If1dol6znTFHS4StA";


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Transactional
    public void createOrderTest() throws Exception {
        createOrder();
    }

    private OrderResponse createOrder() throws Exception {
        String responseJson = mockMvc.perform(post("/api/public/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readResourceFileAsString("requests/create_order.json"))
                        .header("X-Authorization-Token", CUSTOMER_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.order_number").exists())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.items..id_product").exists())
                .andExpect(jsonPath("$.items..name").exists())
                .andExpect(jsonPath("$.items..quantity").exists())
                .andExpect(jsonPath("$.items..unit_value").exists())
                .andExpect(jsonPath("$.items..total").exists())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();


        return objectMapper.readValue(responseJson, OrderResponse.class);
    }


    @Test
    @Transactional
    public void updateOrderStatus() throws Exception {

        var orderResponse = createOrder();

        updateOrderStatusToPreparing(orderResponse);


    }

    @Test
    @Transactional
    public void listOrders() throws Exception {
        var orderResponse = createOrder();
        updateOrderStatusToPreparing(orderResponse);

        mockMvc.perform(get("/api/private/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getBackofficeTokenTest()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.total_elements").exists())
                .andExpect(jsonPath("$.total_pages").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.content..id").exists())
                .andExpect(jsonPath("$.content..status").exists())
                .andExpect(jsonPath("$.content..updated_date").exists())
                .andExpect(jsonPath("$.content..wait_time").exists())
                .andExpect(jsonPath("$.content..order_number").exists());

    }

    private void updateOrderStatusToPreparing(OrderResponse orderResponse) throws Exception {

        var orderStatusRequest = readResourceFileAsString("requests/order_status_update.json");
        var request = format(orderStatusRequest, orderResponse.getId());

        mockMvc.perform(put("/api/private/v1/orders/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
                        .header("Authorization", getBackofficeTokenTest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    public void findOrder() throws Exception {
        mockMvc.perform(get("/api/private/v1/orders/955bc45f-4594-4319-9b8c-1d34b95d79bb")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getBackofficeTokenTest()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.order_number").exists())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.items..id_product").exists())
                .andExpect(jsonPath("$.items..name").exists())
                .andExpect(jsonPath("$.items..quantity").exists())
                .andExpect(jsonPath("$.items..unit_value").exists())
                .andExpect(jsonPath("$.items..total").exists());


    }

    @Test
    public void findOrder_WhenNotFoundTest() throws Exception {
        mockMvc.perform(get("/api/private/v1/orders/40f1c363-b4ac-434c-a669-94e655c21357")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", getBackofficeTokenTest()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    }

    private Object getBackofficeTokenTest() {
        return format("Bearer %s", TOKEN);
    }


}