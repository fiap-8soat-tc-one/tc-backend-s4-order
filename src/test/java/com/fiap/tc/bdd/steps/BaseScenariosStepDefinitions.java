package com.fiap.tc.bdd.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.tc.bdd.dto.CustomerLoginRequest;
import com.fiap.tc.infrastructure.presentation.requests.OrderRequest;
import com.fiap.tc.infrastructure.presentation.requests.OrderStatusRequest;
import com.fiap.tc.infrastructure.presentation.response.DefaultResponse;
import com.fiap.tc.infrastructure.presentation.response.OrderResponse;
import com.fiap.tc.infrastructure.services.AuthLoginClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.fiap.tc.util.TestUtils.readResourceFileAsString;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
public class BaseScenariosStepDefinitions {

    @LocalServerPort
    int port;
    public static final String URL_CREATE_ORDER_TEMPLATE = "%s:%s/%s";
    public static final String BASE_ENDPOINT_APP_ORDER = "http://localhost";
    public static final String ORDER_RESOURCE = "api/public/v1/orders";

    public static final String URL_UPDATE_ORDER_TEMPLATE = "%s:%s/%s";
    public static final String ORDER_STATUS_RESOURCE = "api/private/v1/orders/status";
    public static final String GET_ORDER_RESOURCE = "api/private/v1/orders/%s";

    public static final String CUSTOMER_LOGIN_URL = "http://localhost:8081/api/public/v1/oauth/token";
    public static final String CUSTOMER_DOCUMENT = "88404071039";

    @Autowired
    protected TestRestTemplate testRestTemplate;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected AuthLoginClient authLoginClient;

    private String backofficeToken;
    private String customerToken;


    protected String buildCreateOrderUrl() {
        return format(URL_CREATE_ORDER_TEMPLATE, BASE_ENDPOINT_APP_ORDER, port, ORDER_RESOURCE);
    }

    protected String buildUpdateOrderStatusUrl() {
        return format(URL_UPDATE_ORDER_TEMPLATE, BASE_ENDPOINT_APP_ORDER, port, ORDER_STATUS_RESOURCE);
    }

    protected String buildGetOrderStatusUrl(String id) {
        return format(format(URL_UPDATE_ORDER_TEMPLATE, BASE_ENDPOINT_APP_ORDER, port, GET_ORDER_RESOURCE), id);
    }

    protected ResponseEntity<OrderResponse> createNewOrder(OrderRequest request, HttpHeaders headers) {
        var requestEntity = new HttpEntity<>(request, headers);
        var orderResponse = testRestTemplate.exchange(buildCreateOrderUrl(), HttpMethod.POST, requestEntity,
                OrderResponse.class);
        assertOrderCreated(orderResponse);
        return orderResponse;
    }

    protected String getCustomerToken() {
        if (Objects.isNull(customerToken)) {
            var request = CustomerLoginRequest.builder().document(CUSTOMER_DOCUMENT).build();
            var customerTokenResponse = testRestTemplate.postForEntity(CUSTOMER_LOGIN_URL, request, Map.class);
            assertThat(customerTokenResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(customerTokenResponse.getBody()).isInstanceOf(Map.class);
            assertThat(requireNonNull(customerTokenResponse.getBody()).get("access_token")).isNotNull();

            return (String) customerTokenResponse.getBody().get("access_token");
        }
        return customerToken;

    }

    protected String getBackofficeToken() {
        if (isNull(backofficeToken)) {
            var authDetail = authLoginClient.execute();
            assertThat(authDetail).isNotNull();
            assertThat(authDetail.getAccessToken()).isNotNull();
            return authDetail.getAccessToken();
        }
        return backofficeToken;

    }

    protected OrderRequest getOrderRequest() throws JsonProcessingException {
        return objectMapper.readValue(readResourceFileAsString("requests/create_order.json")
                , OrderRequest.class);
    }

    private void assertOrderCreated(ResponseEntity<OrderResponse> orderResponse) {
        assertThat(orderResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(orderResponse.getBody()).isInstanceOf(OrderResponse.class);
        assertThat(requireNonNull(orderResponse.getBody()).getItems()).hasSize(4);
    }

    protected HttpHeaders getCustomerTokenHttpHeaders() {
        var headers = new HttpHeaders();
        headers.set("X-Authorization-Token", getCustomerToken());
        return headers;
    }

    protected HttpHeaders getBackofficeTokenHttpHeaders() {
        var headers = new HttpHeaders();
        headers.set("Authorization", format("Bearer %s", getBackofficeToken()));
        return headers;
    }

    protected void updateOrderStatus(OrderStatusRequest request) {
        var requestEntity = new HttpEntity<>(request, getBackofficeTokenHttpHeaders());
        var result = testRestTemplate.exchange(buildUpdateOrderStatusUrl(), HttpMethod.PUT, requestEntity,
                DefaultResponse.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    protected ResponseEntity<OrderResponse> getOrder(UUID orderId) {
        var requestEntity = new HttpEntity<>(null, getBackofficeTokenHttpHeaders());
        var result = testRestTemplate.exchange(buildGetOrderStatusUrl(orderId.toString()), HttpMethod.GET, requestEntity,
                OrderResponse.class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isInstanceOf(OrderResponse.class);
        return result;
    }


}
