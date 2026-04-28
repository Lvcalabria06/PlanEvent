Feature: Controle de capacidade por layout do local

  Scenario: Cadastrar layout em local ativo com auditoria
    Given um evento em planejamento com local ativo vinculado
    When cadastro layout "Auditório" com capacidade 180 e usuario "gestor1"
    Then o layout fica cadastrado para o local
    And o layout registra usuario "gestor1"

  Scenario: Impedir nome de layout duplicado no mesmo local
    Given um evento em planejamento com local ativo vinculado
    And cadastro layout "Auditório" com capacidade 180 e usuario "gestor1"
    When tento cadastrar layout "Auditório" com capacidade 200 e usuario "gestor2"
    Then ocorre erro de layout duplicado

  Scenario: Listar layouts compativeis e incompativeis para o evento
    Given um evento em planejamento com local ativo vinculado
    And cadastro layout "Auditório" com capacidade 180 e usuario "gestor1"
    And cadastro layout "Coquetel" com capacidade 400 e usuario "gestor1"
    When analiso compatibilidade dos layouts para o evento
    Then o layout "Auditório" fica incompativel
    And o layout "Coquetel" fica compativel

  Scenario: Exigir justificativa para selecionar layout incompativel
    Given um evento em planejamento com local ativo vinculado
    And cadastro layout "Auditório" com capacidade 180 e usuario "gestor1"
    When tento associar ao evento o layout "Auditório" sem justificativa
    Then ocorre erro exigindo justificativa de excecao

  Scenario: Marcar revalidacao quando capacidade do layout muda
    Given um evento em planejamento com local ativo vinculado
    And cadastro layout "Coquetel" com capacidade 400 e usuario "gestor1"
    And associo ao evento o layout "Coquetel" com justificativa ""
    When atualizo layout "Coquetel" para capacidade 300 por usuario "gestor2"
    Then o evento fica com validacao de layout pendente
