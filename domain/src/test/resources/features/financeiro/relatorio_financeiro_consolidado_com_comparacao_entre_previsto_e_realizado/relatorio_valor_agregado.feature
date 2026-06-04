Feature: Relatório financeiro com valor agregado (comparativo, recomendações, tipos e simulação)

  Como controller
  Quero análise financeira com evolução, recomendações e emissão controlada
  Para tomar decisões com base em relatórios auditáveis e não apenas leitura de totais


  Scenario: Simular relatório sem persistir snapshot
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "ALIMENTACAO" é de 1000.0 para o relatório
    When eu simular o relatório financeiro do evento
    Then a simulação deve ser criada sem persistir relatório
    When eu confirmar a geração preliminar da simulação
    Then o relatório é gerado e persistido com sucesso
    And o relatório deve ser do tipo "PRELIMINAR"


  Scenario: Segundo relatório inclui comparativo quando situação piora
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "LOGISTICA" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 900.00 na categoria "LOGISTICA" para o relatório
    When eu gerar o relatório financeiro do evento
    And foram registradas despesas ativas de 1300.00 na categoria "LOGISTICA" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter comparativo com tendência "PIOROU"


  Scenario: Categoria em desvio crítico gera recomendação
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "MARKETING" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 2000.00 na categoria "MARKETING" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter ao menos uma recomendação financeira
    And o relatório deve conter indicador de cobertura contratual


  Scenario: Nova versão oficial exige motivo quando já existe oficial
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "SERVICO" é de 3000.0 para o relatório
    And já existe um relatório oficial para o evento do relatório
    When eu tentar gerar relatório oficial sem motivo após relatório oficial existente
    Then o sistema deve impedir a geração do relatório


  Scenario: Emitir nova versão oficial com motivo documentado
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "DECORACAO" é de 500.0 para o relatório
    And já existe um relatório oficial para o evento do relatório
    When eu gerar relatório oficial do evento com motivo "Revisão pós-aprovação de despesas"
    Then o relatório é gerado e persistido com sucesso
    And o relatório deve ser do tipo "OFICIAL"


  Scenario: Despesas descobertas na conciliação geram recomendação e penalizam score
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "ALIMENTACAO" é de 1000.0 para o relatório
    And foram registradas despesas ativas de 800.00 na categoria "ALIMENTACAO" para o relatório
    And existem 2 despesas ativas no evento do relatório
    And todas as despesas ativas do evento estão descobertas na conciliação
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter indicador de cobertura contratual
    And o relatório deve conter ao menos uma recomendação financeira
    And o score de saúde financeira deve ser menor que 80.0
