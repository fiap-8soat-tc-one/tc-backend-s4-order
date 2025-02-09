# language: en
Feature: Tech Challenge Order

  Scenario: Register an order for an unidentified customer
    When send a new order request without customer identification
    Then the order is created successfully

  Scenario: Register an order for a customer
    Given customer identified
    When send a new order request for a customer
    Then the order is created successfully for the customer

  Scenario: Confirm payment for an order
    Given an order is registered for a customer
    When the payment order is confirmed
    Then the order is sent to the kitchen for preparation

  Scenario: Order ready for the customer
    Given an order is registered for a customer
    And a payment is confirmed
    When the kitchen finishes preparing the order
    Then the customer can pick up the order at the counter

  Scenario: Order completed
    Given an order is registered for a customer
    And a payment is confirmed
    And a order is ready to pick up
    When the customer picks up the order
    Then the customer order is finalized