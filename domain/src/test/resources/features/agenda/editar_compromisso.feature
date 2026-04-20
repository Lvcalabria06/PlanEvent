Feature: Editar compromissos

  Como gestor
  Quero editar meus compromissos
  Para ajustar horários ou informações

  Scenario: Editar compromisso com sucesso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu editar o título desse compromisso para "Reunião atualizada"
    Then o compromisso é atualizado com sucesso

  Scenario: Impedir edição de compromisso inexistente
    Given existe um gestor válido para agenda
    When eu tentar editar um compromisso inexistente
    Then o sistema deve impedir a edição do compromisso

  Scenario: Impedir edição de compromisso concluído
    Given existe um gestor válido para agenda
    And existe um compromisso concluído para esse gestor
    When eu tentar editar esse compromisso concluído
    Then o sistema deve impedir a edição do compromisso

  Scenario: Impedir edição que gere sobreposição
    Given existe um gestor válido para agenda
    And existem dois compromissos cadastrados para esse gestor
    When eu tentar editar o segundo compromisso para o horário do primeiro
    Then o sistema deve impedir a edição do compromisso
