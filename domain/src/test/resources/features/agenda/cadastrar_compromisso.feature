Feature: Cadastrar compromissos

  Como gestor
  Quero cadastrar compromissos na minha agenda
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
