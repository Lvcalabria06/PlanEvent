Feature: Remover compromissos

  Como gestor
  Quero remover compromissos
  Para organizar minha agenda

  Scenario: Remover compromisso com sucesso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu remover esse compromisso
    Then o compromisso é removido com sucesso

  Scenario: Impedir remoção de compromisso em andamento
    Given existe um gestor válido para agenda
    And existe um compromisso em andamento para esse gestor
    When eu tentar remover esse compromisso em andamento
    Then o sistema deve impedir a remoção do compromisso

  Scenario: Impedir remoção de compromisso inexistente
    When eu tentar remover um compromisso inexistente
    Then o sistema deve impedir a remoção do compromisso

  Scenario: Remover compromisso remove seus lembretes
    Given existe um gestor válido para agenda
    And existe um compromisso com lembretes cadastrado para esse gestor
    When eu remover esse compromisso
    Then o compromisso é removido com sucesso
    And os lembretes vinculados são removidos
