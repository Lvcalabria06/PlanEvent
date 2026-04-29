Feature: Avaliacao contextual de desempenho do local

  Scenario: Registrar avaliacao com evento concluido
    Given um evento concluido com local vinculado para avaliacao contextual
    When registro avaliacao contextual com notas validas e usuario "gestor9"
    Then a avaliacao contextual fica registrada
    And a avaliacao registra usuario "gestor9"

  Scenario: Impedir avaliacao antes da conclusao do evento
    Given um evento em andamento com local vinculado para avaliacao contextual
    When registro avaliacao contextual com notas validas e usuario "gestor9"
    Then ocorre erro de evento nao concluido para avaliacao

  Scenario: Impedir segunda avaliacao principal para mesmo evento e local
    Given um evento concluido com local vinculado para avaliacao contextual
    And registro avaliacao contextual com notas validas e usuario "gestor9"
    When registro avaliacao contextual com notas validas e usuario "gestor10"
    Then ocorre erro de avaliacao duplicada do evento

  Scenario: Calcular media geral, media contextual e baixa base historica
    Given um evento concluido com local vinculado para avaliacao contextual
    And registro avaliacao contextual com notas validas e usuario "g1"
    And existe outro evento concluido do mesmo contexto avaliado com nota base 4
    And existe outro evento concluido do mesmo contexto avaliado com nota base 5
    When consulto o resumo contextual do local
    Then a media contextual do local e maior que zero
    And nao ha baixa base historica no contexto
