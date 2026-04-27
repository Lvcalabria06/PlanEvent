Feature: Registrar despesa de evento

  Como gestor financeiro
  Quero registrar despesas do evento categorizadas por tipo e associadas a fornecedores
  Para manter o controle financeiro do evento

  Scenario: Registrar despesa com sucesso
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    When eu registrar uma despesa de 200.00 na categoria "ALIMENTACAO" com fornecedor e usuário válidos
    Then a despesa é salva com sucesso

  Scenario: Impedir registro de despesa sem evento válido
    Given não existe evento válido para despesas
    When eu tentar registrar uma despesa sem evento válido
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa sem orçamento prévio cadastrado
    Given existe um evento válido para despesas
    And não existe orçamento cadastrado para o evento
    When eu tentar registrar uma despesa sem orçamento
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa quando categoria não tem orçamento previsto
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    When eu tentar registrar uma despesa na categoria "MARKETING" sem orçamento previsto
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa com valor zero
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    When eu tentar registrar uma despesa com valor zero
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa com valor negativo
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    When eu tentar registrar uma despesa com valor negativo
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa sem categoria
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    When eu tentar registrar uma despesa sem categoria
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa sem fornecedor
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    When eu tentar registrar uma despesa sem fornecedor
    Then o sistema deve impedir o registro da despesa

  Scenario: Impedir registro de despesa sem data
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    When eu tentar registrar uma despesa sem data
    Then o sistema deve impedir o registro da despesa

  Scenario: Registrar data, hora e usuário automaticamente
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "EQUIPAMENTO" possui orçamento previsto de 5000.00
    When eu registrar uma despesa de 1000.00 na categoria "EQUIPAMENTO" com fornecedor e usuário válidos
    Then a despesa deve conter data, hora e usuário responsável pelo lançamento
