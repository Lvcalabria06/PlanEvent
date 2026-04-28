Feature: Registrar despesa com comparação automática ao orçamento

  Como gestor financeiro
  Quero registrar despesas do evento categorizadas por tipo e associadas a fornecedores
  Para que o sistema compare automaticamente os valores realizados com o orçamento previsto

  # ──── Fluxo principal ────────────────────────────────────────────────────

  Scenario: Registrar despesa com sucesso dentro do limite seguro
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "ALIMENTACAO" é de 0.00
    When eu registrar uma despesa de 200.00 na categoria "ALIMENTACAO" com fornecedor e usuário válidos
    Then a despesa é salva com sucesso
    And o status da despesa deve ser "REGISTRADA"

  Scenario: Registrar data, hora e usuário automaticamente
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "EQUIPAMENTO" possui orçamento previsto de 5000.00
    And o total acumulado ativo da categoria "EQUIPAMENTO" é de 0.00
    When eu registrar uma despesa de 1000.00 na categoria "EQUIPAMENTO" com fornecedor e usuário válidos
    Then a despesa deve conter data, hora e usuário responsável pelo lançamento

  # ──── Validações de campos obrigatórios (CA4) ─────────────────────────

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

  # ──── Bloqueio automático de categoria esgotada ───────────────────────

  Scenario: Bloquear despesa que ultrapassaria o orçamento da categoria
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "LOGISTICA" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "LOGISTICA" é de 900.00
    When eu tentar registrar uma despesa de 200.00 na categoria "LOGISTICA" que ultrapassaria o limite
    Then o sistema deve bloquear o registro por orçamento esgotado

  Scenario: Bloquear despesa quando orçamento já está 100 porcento consumido
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "DECORACAO" possui orçamento previsto de 500.00
    And o total acumulado ativo da categoria "DECORACAO" é de 500.00
    When eu tentar registrar uma despesa de 1.00 na categoria "DECORACAO" com orçamento esgotado
    Then o sistema deve bloquear o registro por orçamento esgotado

  Scenario: Permitir despesa que atinge exatamente o limite de 80 porcento
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "SERVICO" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "SERVICO" é de 0.00
    When eu registrar uma despesa de 800.00 na categoria "SERVICO" com fornecedor e usuário válidos
    Then a despesa é salva com sucesso
    And o status da despesa deve ser "PENDENTE_APROVACAO"

  # ──── Workflow de aprovação (despesa que atinge 80% do orçamento) ─────

  Scenario: Despesa que ultrapassa 80 porcento do orçamento fica PENDENTE_APROVACAO
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "MARKETING" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "MARKETING" é de 750.00
    When eu registrar uma despesa de 100.00 na categoria "MARKETING" com fornecedor e usuário válidos
    Then a despesa é salva com sucesso
    And o status da despesa deve ser "PENDENTE_APROVACAO"
