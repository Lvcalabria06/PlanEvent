Feature: Visualizar compromissos

  Como gestor
  Quero visualizar meus compromissos
  Para acompanhar minha rotina e responsabilidades

  Scenario: Buscar compromisso por identificador
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu buscar esse compromisso pelo id
    Then o compromisso retornado contém o título esperado

  Scenario: Listar compromissos do gestor
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu listar compromissos desse gestor
    Then a lista contém ao menos um compromisso

  Scenario: Buscar compromisso inexistente falha
    When eu tentar buscar compromisso por id inexistente
    Then o sistema deve impedir a visualização do compromisso
