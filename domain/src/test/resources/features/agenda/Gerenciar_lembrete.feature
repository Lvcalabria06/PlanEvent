Feature: Gestão de Lembretes

  Como gestor
  Quero gerenciar os lembretes vinculados aos compromissos ou eventos
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

  Scenario: Excluir lembrete com sucesso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When eu excluir esse lembrete
    Then o lembrete é removido com sucesso

  Scenario: Impedir exclusão de lembrete inexistente
    When eu tentar excluir lembrete inexistente
    Then o sistema deve impedir a exclusão do lembrete

  Scenario: Listar lembretes de um compromisso
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When eu listar lembretes desse compromisso
    Then a lista contém ao menos um lembrete

  Scenario: Impedir listar lembretes de compromisso inexistente
    When eu tentar listar lembretes de compromisso inexistente
    Then o sistema deve impedir a visualização dos lembretes

  Scenario: Criar lembrete vinculado apenas ao evento com sucesso
    When eu criar um lembrete vinculado apenas a um evento
    Then o lembrete é salvo com sucesso

  Scenario: Marcar lembrete como notificado após disparo do alerta
    Given existe um gestor válido para agenda
    And existe um compromisso cadastrado para esse gestor
    And existe um lembrete cadastrado para esse compromisso
    When o sistema disparar a notificação desse lembrete
    Then o lembrete é marcado como notificado
