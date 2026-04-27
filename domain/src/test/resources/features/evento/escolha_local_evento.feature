Feature: Escolha de local do evento com teto de custo
  Como gestor
  Quero filtrar e vincular local ao evento
  Para escolher um espaço compatível com custo e capacidade

  Scenario: Listar apenas locais compatíveis com teto e capacidade
    Given que existe um evento não confirmado para escolha de local
    And existem locais com custos, capacidades e status diferentes
    When eu busco locais compatíveis com teto de custo 1500.00
    Then a lista deve retornar apenas locais compatíveis

  Scenario: Vincular local compatível ao evento
    Given que existe um evento não confirmado para escolha de local
    And existe um local compatível para vínculo
    When eu vinculo o local ao evento com teto de custo 1500.00
    Then o local deve ficar vinculado ao evento

  Scenario: Impedir vínculo com teto negativo
    Given que existe um evento não confirmado para escolha de local
    When eu tento vincular um local ao evento com teto de custo -1.00
    Then deve ocorrer erro de teto de custo inválido

  Scenario: Impedir alteração de local após confirmação
    Given que existe um evento já confirmado
    And existe um local compatível para vínculo
    When eu tento vincular o local ao evento confirmado com teto de custo 1500.00
    Then deve ocorrer erro por evento confirmado
