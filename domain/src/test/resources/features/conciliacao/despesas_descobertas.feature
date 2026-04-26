Feature: Visualizar despesas sem cobertura contratual

  Como gestor
  Quero visualizar despesas sem cobertura contratual
  Para identificar gastos que não estão amparados por nenhum contrato ativo do evento

  Scenario: Lista despesas que não possuem vínculo de conciliação
    Given existe um evento para conciliação
    And existe uma despesa válida sem vínculo de conciliação nesse evento
    When o gestor lista as despesas descobertas do evento
    Then a lista contém a despesa sem cobertura

  Scenario: Despesa coberta não aparece na lista de descobertas
    Given existe um evento para conciliação
    And existe uma despesa válida já vinculada a um contrato nesse evento
    When o gestor lista as despesas descobertas do evento
    Then a lista de despesas descobertas está vazia
