package com.fiap.tc.bdd.steps;

import com.fiap.tc.domain.enums.OrderStatus;
import com.fiap.tc.infrastructure.presentation.response.OrderResponse;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


public class CreateOrderScenariosStepDefinitions extends BaseScenariosStepDefinitions {

    private ResponseEntity<OrderResponse> orderResponseWithoutCustomer;
    private ResponseEntity<OrderResponse> orderResponse;
    private String customerToken;

    @SneakyThrows
    @When("send a new order request without customer identification")
    public void sendNewOrderRequestWithoutCustomerIdentification() {
        var request = getOrderRequest();
        orderResponseWithoutCustomer = createNewOrder(request, new HttpHeaders());

    }

    @Then("the order is created successfully")
    public void orderCreatedSuccessfully() {
        assertThat(Objects.requireNonNull(orderResponseWithoutCustomer.getBody()).getStatus()).isEqualTo(OrderStatus.RECEIVED);

    }

    @Given("customer identified")
    public void customerIdentified() {
        customerToken = getCustomerToken();

    }


    @When("send a new order request for a customer")
    @SneakyThrows
    public void sendNewOrderRequestForCustomer() {
        var headers = new HttpHeaders();
        headers.set("X-Authorization-Token", customerToken);
        orderResponse = createNewOrder(getOrderRequest(), headers);
    }

    @Then("the order is created successfully for the customer")
    public void customerOrderCreatedSuccessfully() {
        assertThat(Objects.requireNonNull(orderResponse.getBody()).getStatus()).isEqualTo(OrderStatus.RECEIVED);
    }

}
