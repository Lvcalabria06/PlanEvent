Feature: Previsao de consumo baseada em historico

  Como gestor
  Quero que o sistema estime automaticamente a quantidade de recursos necessarios com base em eventos anteriores
  Para planejar melhor o estoque e reduzir faltas ou excessos

  Scenario: Gerar previsao com base em eventos concluidos do mesmo tipo
    Given que existe um evento corporativo medio com estimativa de 200 participantes
    And que existem 2 eventos historicos concluidos do mesmo tipo com consumos validos
    When eu gero a previsao de consumo para esse evento
    Then a previsao deve ser gerada com sucesso
    And a previsao deve indicar historico suficiente
    And a previsao deve conter o item "cadeira" com quantidade ajustada igual a 191
    And a previsao deve conter o item "agua" com quantidade ajustada igual a 357
    And a previsao deve registrar a geracao inicial vinculada ao evento e ao usuario

  Scenario: Indicar historico inexistente quando nao houver base valida
    Given que existe um evento academico medio com estimativa de 80 participantes
    When eu gero a previsao de consumo para esse evento
    Then a previsao deve ser gerada com sucesso
    And a previsao deve indicar historico inexistente

  Scenario: Permitir ajuste manual antes da confirmacao da reserva
    Given que existe um evento academico medio com estimativa de 100 participantes
    And que existe um unico evento historico concluido com consumo valido de agua igual a 120
    When eu gero a previsao de consumo para esse evento
    And eu ajusto manualmente a quantidade prevista do item "agua" para 150
    Then a previsao deve indicar historico insuficiente
    And a previsao deve conter o item "agua" com quantidade ajustada igual a 150
    And o historico da previsao deve preservar o valor original de 120 para o item "agua"
    And o historico da previsao deve registrar um ajuste manual

  Scenario: Recalcular previsao quando houver mudanca relevante no evento
    Given que existe um evento corporativo pequeno com estimativa de 100 participantes
    And que existe um unico evento historico concluido com consumo valido de copos igual a 100
    And que existe outro evento historico concluido de porte grande com consumo valido de copos igual a 240
    When eu gero a previsao de consumo para esse evento
    And eu altero o porte do evento para grande e a estimativa para 180 participantes
    And eu recalculo a previsao desse evento
    Then a previsao deve conter o item "copo" com quantidade ajustada igual a 204
    And o historico da previsao deve registrar um recalculo

  Scenario: Impedir recalculo quando nao houver mudanca relevante
    Given que existe um evento corporativo pequeno com estimativa de 60 participantes
    And que existe um unico evento historico concluido com consumo valido de copos igual a 60
    When eu gero a previsao de consumo para esse evento
    And eu recalculo a previsao desse evento
    Then o sistema deve impedir o recalculo da previsao
