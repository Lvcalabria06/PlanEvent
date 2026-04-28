Feature: Calcular desvio orçamentário por categoria

  Como gestor financeiro
  Quero que o sistema calcule automaticamente o desvio entre o realizado e o previsto
  Para identificar extrapolações no orçamento do evento e agir preventivamente


  Scenario: Calcular desvio dentro do limite normal até 10 porcento
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "DECORACAO" possui orçamento previsto de 1000.00
    And já foram registradas despesas ativas de 900.00 na categoria "DECORACAO"
    When eu calcular o desvio da categoria "DECORACAO"
    Then o desvio percentual calculado deve ser de -10.0 porcento
    And a classificação deve ser "NORMAL"

  Scenario: Desvio exatamente em zero porcento é NORMAL
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    And já foram registradas despesas ativas de 1000.00 na categoria "ALIMENTACAO"
    When eu calcular o desvio da categoria "ALIMENTACAO"
    Then o desvio percentual calculado deve ser de 0.0 porcento
    And a classificação deve ser "NORMAL"


  Scenario: Calcular desvio entre 10 e 20 porcento classifica como ATENCAO
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "SERVICO" possui orçamento previsto de 1000.00
    And já foram registradas despesas ativas de 1100.00 na categoria "SERVICO"
    When eu calcular o desvio da categoria "SERVICO"
    Then o desvio percentual calculado deve ser de 10.0 porcento
    And a classificação deve ser "ATENCAO"

  Scenario: Desvio exatamente no limite de 20 porcento é ATENCAO e não CRITICO
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "OUTRO" possui orçamento previsto de 1000.00
    And já foram registradas despesas ativas de 1200.00 na categoria "OUTRO"
    When eu calcular o desvio da categoria "OUTRO"
    Then o desvio percentual calculado deve ser de 20.0 porcento
    And a classificação deve ser "ATENCAO"


  Scenario: Calcular desvio crítico acima de 20 porcento
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "LOGISTICA" possui orçamento previsto de 1000.00
    And já foram registradas despesas ativas de 1300.00 na categoria "LOGISTICA"
    When eu calcular o desvio da categoria "LOGISTICA"
    Then o desvio percentual calculado deve ser de 30.0 porcento
    And a classificação deve ser "CRITICO"

  Scenario: Acumular despesas de múltiplos registros e classificar como CRITICO
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "MARKETING" possui orçamento previsto de 2000.00
    And o total acumulado de despesas da categoria "MARKETING" é de 2500.00
    When eu calcular o desvio da categoria "MARKETING"
    Then o desvio percentual calculado deve ser de 25.0 porcento
    And a classificação deve ser "CRITICO"


  Scenario: Calcular desvio sem orçamento previsto para o evento retorna erro
    Given existe um evento válido para despesas
    And não existe orçamento cadastrado para o evento
    When eu tentar calcular o desvio sem orçamento cadastrado
    Then o sistema deve impedir o cálculo do desvio

  Scenario: Calcular desvios de todas as categorias do evento
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    And a categoria "DECORACAO" possui orçamento previsto de 500.00
    When eu calcular os desvios de todas as categorias do evento
    Then a lista de desvios deve conter ao menos uma entrada
