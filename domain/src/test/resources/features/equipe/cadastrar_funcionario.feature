Feature: Cadastrar funcionário

  Como gestor
  Quero cadastrar um funcionário com nome, cargo e disponibilidade válidos
  Para manter a equipe organizada

  Scenario: Cadastrar funcionário com sucesso
    Given o gestor informa um nome válido, cargo permitido e disponibilidade válida
    When o gestor cadastrar o funcionário no sistema
    Then o funcionário é salvo com sucesso

  Scenario: Impedir cadastro sem nome
    Given o gestor informa cargo e disponibilidade válidos
    When o gestor tentar cadastrar um funcionário sem nome
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Impedir cadastro com nome inválido
    Given o gestor informa um nome com menos de 3 caracteres ou com caracteres inválidos
    When o gestor tentar cadastrar o funcionário
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Impedir cadastro com cargo inválido
    Given o gestor informa um nome válido e disponibilidade válida
    When o gestor tentar cadastrar um funcionário com cargo fora dos valores permitidos
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Impedir cadastro com disponibilidade inválida
    Given o gestor informa um nome válido e cargo permitido
    When o gestor tentar cadastrar um funcionário com disponibilidade inválida
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Garantir geração automática de identificador
    Given o gestor informa dados válidos para cadastro
    When o gestor cadastrar o funcionário
    Then o sistema deve gerar automaticamente um identificador único e imutável

  Scenario: Garantir registro de datas automáticas
    Given o gestor informa dados válidos para cadastro
    When o gestor cadastrar o funcionário
    Then o sistema deve definir automaticamente createdAt e updatedAt