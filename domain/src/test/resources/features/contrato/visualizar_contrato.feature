Feature: Visualizar contratos

  Como gestor
  Quero visualizar contratos
  Para acompanhar os dados e condições estabelecidas

  Scenario: Buscar contrato por identificador
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    When eu buscar esse contrato pelo id
    Then o contrato retornado contém o objeto esperado

  Scenario: Listar contratos do evento
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    When eu listar contratos desse evento
    Then a lista contém ao menos um contrato

  Scenario: Buscar contrato inexistente falha
    Given não existe contrato com o id informado
    When eu tentar buscar contrato por id inexistente
    Then o sistema deve impedir a visualização do contrato
