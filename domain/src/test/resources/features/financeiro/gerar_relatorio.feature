Feature: Gerar relatório financeiro consolidado

  Como controller
  Quero gerar um relatório financeiro consolidado do evento
  Para apresentar análise de custos com dados confiáveis e auditáveis

  Scenario: Gerar relatório com sucesso
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "ALIMENTACAO" é de 1000.0 para o relatório
    And foram registradas despesas de 800.00 na categoria "ALIMENTACAO" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório é gerado e persistido com sucesso

  Scenario: Relatório inclui total geral previsto e realizado
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "DECORACAO" é de 500.0 para o relatório
    And foram registradas despesas de 600.00 na categoria "DECORACAO" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter o total geral previsto e realizado

  Scenario: Relatório inclui itens por categoria com percentual de variação
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "LOGISTICA" é de 2000.0 para o relatório
    And foram registradas despesas de 2600.00 na categoria "LOGISTICA" para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter itens por categoria
    And o percentual de variação da categoria "LOGISTICA" deve ser de 30.0 porcento

  Scenario: Impedir geração de relatório sem orçamento cadastrado
    Given existe um evento válido para relatório
    And não existe orçamento cadastrado para o evento do relatório
    When eu tentar gerar o relatório sem orçamento
    Then o sistema deve impedir a geração do relatório

  Scenario: Impedir geração de relatório sem categorias no orçamento
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o relatório sem categorias
    When eu tentar gerar o relatório sem categorias no orçamento
    Then o sistema deve impedir a geração do relatório

  Scenario: Impedir geração de relatório sem evento válido
    Given não existe evento válido para relatório
    When eu tentar gerar o relatório com evento inválido
    Then o sistema deve impedir a geração do relatório

  Scenario: Relatório registra data de geração e usuário responsável automaticamente
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "SERVICO" é de 3000.0 para o relatório
    When eu gerar o relatório financeiro do evento
    Then o relatório deve conter a data de geração e o usuário responsável