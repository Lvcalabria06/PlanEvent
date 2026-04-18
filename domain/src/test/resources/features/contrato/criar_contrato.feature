Feature: Cadastrar contratos

  Como gestor
  Quero cadastrar contratos no sistema
  Para formalizar acordos relacionados ao evento

  Scenario: Cadastrar contrato com sucesso
    Given existe um evento válido para contrato
    When eu cadastrar um contrato completo para esse evento
    Then o contrato é salvo com sucesso

  Scenario: Impedir cadastro sem evento válido
    Given não existe evento válido para contrato
    When eu tentar cadastrar um contrato completo
    Then o sistema deve impedir o cadastro do contrato

  Scenario: Impedir cadastro sem objeto
    Given existe um evento válido para contrato
    When eu tentar cadastrar contrato sem objeto
    Then o sistema deve impedir o cadastro do contrato

  Scenario: Impedir cadastro sem valor
    Given existe um evento válido para contrato
    When eu tentar cadastrar contrato sem valor
    Then o sistema deve impedir o cadastro do contrato

  Scenario: Impedir cadastro com vigência inválida
    Given existe um evento válido para contrato
    When eu tentar cadastrar contrato com data de término não posterior à de início
    Then o sistema deve impedir o cadastro do contrato

  Scenario: Impedir cadastro com menos de duas partes
    Given existe um evento válido para contrato
    When eu tentar cadastrar contrato com apenas uma parte
    Then o sistema deve impedir o cadastro do contrato
