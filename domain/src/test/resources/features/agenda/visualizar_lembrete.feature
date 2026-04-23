Feature: Visualizar lembretes

  Como gestor
  Quero visualizar os lembretes de um compromisso
  Para acompanhar os alertas configurados

  Scenario: Listar lembretes de um compromisso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When eu listar lembretes desse compromisso
    Then a lista contém ao menos um lembrete

  Scenario: Impedir listar lembretes de compromisso inexistente
    When eu tentar listar lembretes de compromisso inexistente
    Then o sistema deve impedir a visualização dos lembretes
