Feature: Otimizacao e redistribuicao de estoque entre eventos

  Como gestor
  Quero que o sistema redistribua automaticamente os recursos disponiveis entre eventos concorrentes
  Para maximizar o atendimento das demandas e minimizar faltas criticas

  Scenario: Identificar cenario de escassez de recursos entre multiplos eventos
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    When o gestor solicitar a geracao do cenario de redistribuicao
    Then o sistema deve identificar escassez global do item "item-cadeira"
    And o cenario gerado deve possuir status pendente

  Scenario: Sugerir redistribuicao com base em criterios de prioridade
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    And o evento "evento-prioritario" possui maior prioridade por proximidade e porte
    When o gestor solicitar a geracao do cenario de redistribuicao
    Then o evento "evento-prioritario" deve receber alocacao igual ou superior ao evento "evento-secundario"
    And o cenario deve apresentar alocacoes atuais e otimizadas

  Scenario: Apresentar impacto da redistribuicao por evento
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    When o gestor solicitar a geracao do cenario de redistribuicao
    Then o cenario deve apresentar impacto para o evento "evento-prioritario"
    And o cenario deve apresentar impacto para o evento "evento-secundario"
    And pelo menos um evento deve apresentar deficit apos redistribuicao

  Scenario: Confirmar redistribuicao manualmente antes de aplicar alteracoes
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    And existe um cenario de redistribuicao pendente gerado anteriormente
    When o gestor aplicar a redistribuicao com confirmacao manual
    Then o cenario deve ser marcado como aplicado
    And as reservas dos eventos devem ser atualizadas conforme alocacoes otimizadas

  Scenario: Impedir redistribuicao de itens ja em uso ou consumidos
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva em uso de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    When o gestor solicitar a geracao do cenario de redistribuicao
    Then a reserva em uso do evento "evento-prioritario" nao deve ser alterada na redistribuicao

  Scenario: Recalcular cenario ao ocorrerem alteracoes relevantes
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    And existe um cenario de redistribuicao pendente gerado anteriormente
    When o gestor solicitar novo calculo de redistribuicao
    Then o cenario anterior deve ser invalidado
    And um novo cenario pendente deve ser gerado

  Scenario: Manter historico completo das redistribuicoes realizadas
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 100 unidades para redistribuicao
    And o evento "evento-prioritario" possui reserva de 70 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 60 unidades de "item-cadeira"
    And existe um cenario de redistribuicao pendente gerado anteriormente
    When o gestor aplicar a redistribuicao com confirmacao manual
    Then o cenario deve possuir historico com usuario data e descricao da redistribuicao

  Scenario: Considerar substituicao de itens equivalentes na redistribuicao
    Given existem dois eventos ativos no mesmo periodo com demanda concorrente
    And o item "item-cadeira" possui estoque total de 50 unidades para redistribuicao
    And o item "item-banco" possui estoque total de 40 unidades para redistribuicao
    And existe substituicao de "item-cadeira" por "item-banco" com fator 1.0
    And o evento "evento-prioritario" possui reserva de 40 unidades de "item-cadeira"
    And o evento "evento-secundario" possui reserva de 30 unidades de "item-cadeira"
    When o gestor solicitar a geracao do cenario de redistribuicao
    Then o cenario deve considerar substituicao de "item-cadeira" por "item-banco" para eventos com deficit
