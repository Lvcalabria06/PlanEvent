Feature: Editar lembretes

  Como gestor
  Quero editar um lembrete
  Para alterar o horário ou configuração de notificação

  Scenario: Editar lembrete com sucesso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When eu editar o horário desse lembrete
    Then o lembrete é atualizado com sucesso

  Scenario: Impedir edição de lembrete já notificado
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete notificado para esse compromisso
    When eu tentar editar esse lembrete notificado
    Then o sistema deve impedir a edição do lembrete

  Scenario: Impedir edição de lembrete de compromisso finalizado
    Given existe um gestor válido para agenda
    And existe um compromisso concluído para esse gestor
    And existe um lembrete cadastrado para esse compromisso finalizado
    When eu tentar editar lembrete de compromisso finalizado
    Then o sistema deve impedir a edição do lembrete
