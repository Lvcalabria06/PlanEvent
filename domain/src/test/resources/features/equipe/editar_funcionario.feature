Feature: Editar funcionário

  Como gestor
  Quero editar funcionário no sistema
  Para manter os dados atualizados

  Scenario: Editar funcionário com sucesso
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor editar o nome, cargo ou disponibilidade com valores válidos
    Then o sistema deve atualizar os dados do funcionário com sucesso

  Scenario: Impedir edição com nome inválido
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar editar o funcionário com nome com menos de 3 caracteres ou com caracteres inválidos
    Then o sistema deve impedir a edição do funcionário

  Scenario: Impedir edição com cargo inválido
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar editar o funcionário com cargo fora dos valores permitidos
    Then o sistema deve impedir a edição do funcionário

  Scenario: Impedir edição com disponibilidade inválida
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar editar o funcionário com disponibilidade fora do padrão permitido
    Then o sistema deve impedir a edição do funcionário

  Scenario: Atualizar updatedAt automaticamente na edição
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor editar qualquer dado válido do funcionário
    Then o sistema deve atualizar automaticamente o campo updatedAt

  Scenario: Impedir alteração manual de createdAt
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar alterar manualmente o campo createdAt
    Then o sistema deve impedir a edição do campo createdAt

  Scenario: Impedir alteração manual de updatedAt
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar alterar manualmente o campo updatedAt
    Then o sistema deve impedir a edição do campo updatedAt

  Scenario: Impedir edição que deixe o funcionário em estado inválido
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar salvar alterações inconsistentes no funcionário
    Then o sistema deve impedir a edição do funcionário