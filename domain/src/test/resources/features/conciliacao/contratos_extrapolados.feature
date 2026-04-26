Feature: Visualizar contratos com valor comprometido superior ao contratado

  Como gestor
  Quero visualizar contratos com valor comprometido superior ao contratado
  Para detectar extrapolações antes que se tornem um problema financeiro

  Scenario: Contrato com total conciliado acima do valor contratado é sinalizado como extrapolado
    Given existe um evento para conciliação
    And existe um contrato cujo total de despesas conciliadas ultrapassa seu valor contratado
    When o gestor lista os contratos extrapolados do evento
    Then o contrato aparece na lista de extrapolados

  Scenario: Contrato dentro do limite não aparece na lista de extrapolados
    Given existe um evento para conciliação
    And existe um contrato cujo total de despesas conciliadas não ultrapassa seu valor contratado
    When o gestor lista os contratos extrapolados do evento
    Then a lista de contratos extrapolados está vazia
