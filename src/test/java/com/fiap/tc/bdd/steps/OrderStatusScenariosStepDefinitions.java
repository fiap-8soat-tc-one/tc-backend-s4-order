package com.fiap.tc.bdd.steps;

import com.fiap.tc.domain.enums.OrderStatus;
import com.fiap.tc.infrastructure.presentation.requests.OrderStatusRequest;
import com.fiap.tc.infrastructure.presentation.response.OrderResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;


public class OrderStatusScenariosStepDefinitions extends BaseScenariosStepDefinitions {

    private ResponseEntity<OrderResponse> orderToBeConfirmedResponse;
    private ResponseEntity<OrderResponse> orderPreparingResponse;
    private ResponseEntity<OrderResponse> orderReadyResponse;


    @SneakyThrows
    @Given("an order is registered for a customer")
    public void orderRegistered() {
        orderToBeConfirmedResponse = createNewOrder(getOrderRequest(), getCustomerTokenHttpHeaders());
        assertThat(requireNonNull(orderToBeConfirmedResponse.getBody()).getStatus()).isEqualTo(OrderStatus.RECEIVED);
    }

    @SneakyThrows
    @When("the payment order is confirmed")
    public void paymentOrderConfirmed() {
        var orderPending = orderToBeConfirmedResponse.getBody();
        assert nonNull(orderPending);
        var request = OrderStatusRequest.builder().id(orderPending.getId()).status(OrderStatus.PREPARING).build();
        updateOrderStatus(request);

    }


    @Then("the order is sent to the kitchen for preparation")
    public void orderBeingPreparing() {
        orderPreparingResponse = getOrder(requireNonNull(orderToBeConfirmedResponse.getBody()).getId());
        assertThat(requireNonNull(orderPreparingResponse.getBody()).getStatus()).isEqualTo(OrderStatus.PREPARING);
    }

    @SneakyThrows
    @And("a payment is confirmed")
    public void orderRegisteredAndPreparing() {

        var orderCreated = orderToBeConfirmedResponse.getBody();
        var request = OrderStatusRequest.builder().id(requireNonNull(orderCreated).getId()).status(OrderStatus.PREPARING).build();
        updateOrderStatus(request);
        orderPreparingResponse = getOrder(requireNonNull(orderCreated.getId()));
        assertThat(requireNonNull(orderPreparingResponse.getBody()).getStatus()).isEqualTo(OrderStatus.PREPARING);

    }

    @When("the kitchen finishes preparing the order")
    public void orderPrepareFinished() {
        var orderPreparing = orderPreparingResponse.getBody();
        assertThat(requireNonNull(orderPreparing).getStatus()).isEqualTo(OrderStatus.PREPARING);
        var request = OrderStatusRequest.builder().id(orderPreparing.getId()).status(OrderStatus.READY).build();
        updateOrderStatus(request);
    }

    @Then("the customer can pick up the order at the counter")
    public void orderReadyToPickUpByTheCustomer() {
        orderReadyResponse = getOrder(requireNonNull(orderPreparingResponse.getBody()).getId());
        assertThat(requireNonNull(orderReadyResponse.getBody()).getStatus()).isEqualTo(OrderStatus.READY);
    }

    @And("a order is ready to pick up")
    public void orderReadyToBePickedUp() {
        var orderPreparing = orderPreparingResponse.getBody();
        var request =
                OrderStatusRequest.builder().id(requireNonNull(orderPreparing).getId()).status(OrderStatus.READY).build();
        updateOrderStatus(request);
        orderReadyResponse = getOrder(requireNonNull(orderPreparing.getId()));
        assertThat(requireNonNull(orderReadyResponse.getBody()).getStatus()).isEqualTo(OrderStatus.READY);
    }

    @When("the customer picks up the order")
    public void customerPicksOrder() {
        var orderReady = orderReadyResponse.getBody();
        assertThat(requireNonNull(orderReady).getStatus()).isEqualTo(OrderStatus.READY);
        var request = OrderStatusRequest.builder().id(orderReady.getId()).status(OrderStatus.FINISHED).build();
        updateOrderStatus(request);
    }

    @Then("the customer order is finalized")
    public void orderFinished() {
        var orderFinishedResponse = getOrder(requireNonNull(orderReadyResponse.getBody()).getId());
        assertThat(requireNonNull(orderFinishedResponse.getBody()).getStatus()).isEqualTo(OrderStatus.FINISHED);
    }


}
