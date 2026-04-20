Feature: Excluir lembretes

  Como gestor
  Quero excluir um lembrete
  Para evitar notificações desnecessárias

  Scenario: Excluir lembrete com sucesso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When eu excluir esse lembrete
    Then o lembrete é removido com sucesso

  Scenario: Impedir exclusão de lembrete inexistente
    When eu tentar excluir lembrete inexistente
    Then o sistema deve impedir a exclusão do lembrete
