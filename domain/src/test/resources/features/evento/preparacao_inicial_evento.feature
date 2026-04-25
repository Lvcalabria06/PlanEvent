Feature: Preparação inicial do evento
  Como gestor
  Quero cadastrar e confirmar a preparação inicial do evento
  Para manter os dados básicos consistentes

  Scenario: Cadastrar evento com dados válidos
    Given que eu possuo dados válidos para cadastro de evento
    When eu cadastro o evento
    Then o evento deve ser salvo com sucesso
    And o evento deve iniciar com preparação não confirmada

  Scenario: Impedir cadastro com nome inválido
    Given que eu possuo dados inválidos de nome para evento
    When eu tento cadastrar o evento
    Then deve ocorrer erro de validação de evento

  Scenario: Editar evento antes da confirmação
    Given que existe um evento cadastrado e não confirmado
    When eu edito os dados do evento com valores válidos
    Then os dados do evento devem ser atualizados

  Scenario: Confirmar preparação inicial uma única vez
    Given que existe um evento cadastrado e não confirmado
    When eu confirmo a preparação inicial do evento
    Then o evento deve ficar confirmado
    When eu tento confirmar novamente a preparação inicial do evento
    Then deve ocorrer erro de confirmação duplicada
