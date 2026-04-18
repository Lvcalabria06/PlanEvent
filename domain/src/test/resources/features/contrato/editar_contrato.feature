Feature: Editar contratos

  Como gestor
  Quero editar contratos
  Para atualizar informações conforme necessário

  Scenario: Editar contrato com sucesso
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    When eu editar o objeto desse contrato para "Novo objeto acordado"
    Then o contrato é atualizado com sucesso

  Scenario: Impedir edição de contrato inexistente
    Given existe um evento válido para contrato
    And não existe contrato com o id informado
    When eu tentar editar um contrato inexistente
    Then o sistema deve impedir a edição do contrato

  Scenario: Impedir edição que deixa o contrato inconsistente
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    When eu tentar editar o contrato com valor zero
    Then o sistema deve impedir a edição do contrato

  Scenario: Contrato encerrado não pode ser editado
    Given existe um evento válido para contrato
    And existe um contrato encerrado nesse evento
    When eu tentar editar esse contrato encerrado
    Then o sistema deve impedir a edição do contrato
