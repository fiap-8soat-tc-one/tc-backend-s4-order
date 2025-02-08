# language: en
Feature: API - Order
  Scenario: Registrar um pedido para cliente sem identificação
    When enviar solicitação de pedido sem identificação do cliente
    Then o pedido é criado com sucesso

  Scenario: Registrar um pedido para o cliente
    When enviar solicitação de pedido para o cliente
    Then o pedido é criado com sucesso para o cliente

  Scenario: Confirmar pagamento de um pedido
    Given pedido registrado para o cliente
    When pagamento do pedido for confirmado
    Then o pedido é enviado para preparação na cozinha

  Scenario: Pedido pronto para o cliente
    Given pedido registrado e pagamento confirmado para o cliente
    When cozinha finalizar preparo do pedido
    Then cliente pode retirar pedido no balcão

  Scenario: Pedido finalizado
    Given pedido pronto para o cliente retirar no balcão
    When cliente retirar o pedido
    Then o pedido do cliente é finalizado
