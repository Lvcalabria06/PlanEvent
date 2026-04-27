Feature: Gestão de contratos do evento

  Como gestor
  Quero cadastrar, editar, visualizar e encerrar contratos
  Para formalizar e acompanhar acordos relacionados ao evento

  # Cadastro

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

  # Edição

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

  # Visualização

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

  # Encerramento

  Scenario: Encerrar contrato completo com sucesso
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    When eu encerrar esse contrato
    Then o contrato passa ao status ENCERRADO

  Scenario: Impedir encerramento quando informações estão incompletas
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    But o contrato é considerado incompleto para encerramento
    When eu tentar encerrar esse contrato
    Then o sistema deve impedir o encerramento do contrato

  Scenario: Impedir encerrar contrato já encerrado
    Given existe um evento válido para contrato
    And existe um contrato encerrado nesse evento
    When eu tentar encerrar novamente esse contrato
    Then o sistema deve impedir o encerramento do contrato
