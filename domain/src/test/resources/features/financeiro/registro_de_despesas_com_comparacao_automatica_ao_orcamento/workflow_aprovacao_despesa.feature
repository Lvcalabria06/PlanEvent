Feature: Workflow de aprovação de despesa

  Como gestor financeiro e aprovador
  Quero que despesas que atingem 80% do orçamento da categoria requeiram aprovação
  Para garantir controle sobre gastos elevados antes de comprometer o orçamento

  # ──── Despesa aprovada automaticamente (abaixo de 80%) ───────────────

  Scenario: Despesa abaixo de 80 porcento é REGISTRADA automaticamente
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "ALIMENTACAO" é de 0.00
    When eu registrar uma despesa de 200.00 na categoria "ALIMENTACAO" com fornecedor e usuário válidos
    Then a despesa é salva com sucesso
    And o status da despesa deve ser "REGISTRADA"

  # ──── Despesa pendente de aprovação (atinge 80%) ─────────────────────

  Scenario: Despesa que ultrapassa limiar de aprovação fica PENDENTE_APROVACAO
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "LOGISTICA" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "LOGISTICA" é de 700.00
    When eu registrar uma despesa de 200.00 na categoria "LOGISTICA" com fornecedor e usuário válidos
    Then a despesa é salva com sucesso
    And o status da despesa deve ser "PENDENTE_APROVACAO"

  # ──── Aprovação de despesa pendente ──────────────────────────────────

  Scenario: Aprovar despesa pendente muda status para APROVADA
    Given existe uma despesa pendente de aprovação de 300.00 na categoria "SERVICO"
    When o aprovador aprovar a despesa pendente
    Then o aprovador deve estar registrado na despesa

  # ──── Rejeição de despesa pendente ───────────────────────────────────

  Scenario: Rejeitar despesa pendente muda status para REJEITADA
    Given existe uma despesa pendente de aprovação de 300.00 na categoria "SERVICO"
    When o aprovador rejeitar a despesa com motivo "Valor incompatível com cotação"
    Then o motivo de rejeição deve estar registrado

  # ──── Rejeição impacta o total acumulado ─────────────────────────────

  Scenario: Despesa rejeitada não conta no total acumulado para cálculo de desvio
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "DECORACAO" possui orçamento previsto de 1000.00
    And o total acumulado ativo da categoria "DECORACAO" é de 200.00
    When eu calcular o desvio da categoria "DECORACAO"
    Then o desvio percentual calculado deve ser de -80.0 porcento
    And a classificação deve ser "NORMAL"

  # ──── Tentativas inválidas de transição de estado ────────────────────

  Scenario: Não é possível aprovar despesa que já está APROVADA
    Given existe uma despesa já aprovada
    When eu tentar aprovar novamente a despesa já aprovada
    Then o sistema deve impedir a transição de estado

  Scenario: Não é possível rejeitar despesa que já está REJEITADA
    Given existe uma despesa já rejeitada
    When eu tentar rejeitar novamente a despesa já rejeitada
    Then o sistema deve impedir a transição de estado

  Scenario: Não é possível aprovar despesa com status REGISTRADA
    Given existe uma despesa com status REGISTRADA
    When eu tentar aprovar uma despesa REGISTRADA diretamente
    Then o sistema deve impedir a transição de estado
