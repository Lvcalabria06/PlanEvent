Feature: Saúde financeira do relatório consolidado

  Como controller
  Quero que o relatório calcule automaticamente um score de saúde financeira do evento
  Para apresentar uma visão sintética e ponderada do estado orçamentário aos stakeholders

  # ──── Score SAUDAVEL (≥ 80 pontos) ───────────────────────────────────

  Scenario: Relatório com todas categorias dentro do orçamento tem saúde SAUDAVEL
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "ALIMENTACAO" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 800.00 na categoria "ALIMENTACAO" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o score de saúde financeira deve ser maior ou igual a 80.0
    And a classificação de saúde do relatório deve ser "SAUDAVEL"

  # ──── Score ATENÇÃO (60–79 pontos) ───────────────────────────────────

  Scenario: Relatório com categoria em desvio moderado tem saúde ATENCAO
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "LOGISTICA" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 1300.00 na categoria "LOGISTICA" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o score de saúde financeira deve ser menor que 80.0
    And a classificação de saúde do relatório deve ser "ATENCAO"

  # ──── Score CRITICO (< 60 pontos) ────────────────────────────────────

  Scenario: Relatório com categoria em desvio severo tem saúde CRITICO
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "MARKETING" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 2000.00 na categoria "MARKETING" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o score de saúde financeira deve ser menor que 60.0
    And a classificação de saúde do relatório deve ser "CRITICO"

  # ──── Ponderação por peso orçamentário ───────────────────────────────

  Scenario: Score é ponderado pelo peso orçamentário de cada categoria
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "ALIMENTACAO" é de 9000.0 para o relatório
    And foram registradas despesas ativas de 8000.00 na categoria "ALIMENTACAO" para o relatório
    And o orçamento da categoria "DECORACAO" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 2500.00 na categoria "DECORACAO" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter o score de saúde financeira calculado
    And a classificação de saúde do relatório deve ser "SAUDAVEL"
