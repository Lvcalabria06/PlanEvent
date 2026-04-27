Feature: Avaliação de adequação do local após o uso
  Como gestor
  Quero registrar e consultar a adequação do local após o evento
  Para manter histórico e decisões futuras

  Scenario: Registrar avaliação com evento concluído
    Given cenario de avaliacao com evento vinculado a um local
    And o evento do contexto de avaliação foi concluído
    When o gestor registra avaliação "ADEQUADO" com justificativa "Estrutura ok" como usuário "gestor-1"
    Then a avaliação fica salva
    And o registro traz o usuário "gestor-1"

  Scenario: Impedir avaliação sem conclusão do evento
    Given cenario de avaliacao com evento vinculado a um local
    When o gestor tenta registrar avaliação "PARCIALMENTE_ADEQUADO" com justificativa "Quase" como usuário "g2"
    Then a avaliação é rejeitada
    And a mensagem fala de evento concluir

  Scenario: Impedir avaliação com outro local que o do evento
    Given cenario de avaliacao com evento vinculado a um local
    And o evento do contexto de avaliação foi concluído
    When o gestor tenta avaliar o local "outro-local" em vez do vinculado
    Then a avaliação é rejeitada
    And a mensagem fala de vinculado

  Scenario: Não permitir duas avaliações principais do mesmo evento
    Given cenario de avaliacao com evento vinculado a um local
    And o evento do contexto de avaliação foi concluído
    And uma avaliação "ADEQUADO" com justificativa "Primeira" já foi registrada por "g1"
    When o gestor tenta registrar avaliação "INADEQUADO" com justificativa "Segunda" como usuário "g2"
    Then a avaliação é rejeitada
    And a mensagem cita que já existe avaliação

  Scenario: Listar histórico de adequação por local
    Given cenario de avaliacao com evento vinculado a um local
    And o evento do contexto de avaliação foi concluído
    And uma avaliação "EXCELENTE" com justificativa "Bom" já foi registrada por "g9"
    When o gestor lista avaliações do local
    Then a lista traz ao menos 1 registro
    And o registro visto tem nível "EXCELENTE"
