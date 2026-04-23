Feature: Cadastrar lembretes

  Como gestor
  Quero criar um lembrete vinculado a um compromisso
  Para ser notificado antes do horário definido

  Scenario: Criar lembrete com sucesso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu criar um lembrete para esse compromisso
    Then o lembrete é salvo com sucesso

  Scenario: Impedir criar lembrete para compromisso inexistente
    When eu tentar criar lembrete para compromisso inexistente
    Then o sistema deve impedir o cadastro do lembrete

  Scenario: Impedir criar lembrete com horário posterior ao início do compromisso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    When eu tentar criar lembrete com horário posterior ao compromisso
    Then o sistema deve impedir o cadastro do lembrete

  Scenario: Impedir criar lembrete com horário duplicado
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When eu tentar criar lembrete com o mesmo horário
    Then o sistema deve impedir o cadastro do lembrete

  Scenario: Impedir criar lembrete para compromisso finalizado
    Given existe um gestor válido para agenda
    And existe um compromisso concluído para esse gestor
    When eu tentar criar lembrete para compromisso finalizado
    Then o sistema deve impedir o cadastro do lembrete
