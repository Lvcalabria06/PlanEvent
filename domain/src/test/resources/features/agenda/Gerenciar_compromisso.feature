Feature: Gestão de Compromissos

  Como gestor
  Quero gerenciar meus compromissos na minha agenda
  Para organizar minhas atividades relacionadas ao evento

  Scenario: Cadastrar compromisso com sucesso
    Given existe um gestor válido para agenda
    When eu cadastrar um compromisso completo para esse gestor
    Then o compromisso é salvo com sucesso

  Scenario: Impedir cadastro sem gestor válido
    When eu tentar cadastrar compromisso sem gestor
    Then o sistema deve impedir o cadastro do compromisso

  Scenario: Impedir cadastro sem título
    Given existe um gestor válido para agenda
    When eu tentar cadastrar compromisso sem título
    Then o sistema deve impedir o cadastro do compromisso

  Scenario: Impedir cadastro com horário de fim anterior ao início
    Given existe um gestor válido para agenda
    When eu tentar cadastrar compromisso com horário de fim anterior ao início
    Then o sistema deve impedir o cadastro do compromisso

  Scenario: Impedir cadastro em data passada
    Given existe um gestor válido para agenda
    When eu tentar cadastrar compromisso em data passada
    Then o sistema deve impedir o cadastro do compromisso

  Scenario: Impedir cadastro com sobreposição de horário
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu tentar cadastrar compromisso com sobreposição de horário
    Then o sistema deve impedir o cadastro do compromisso

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

