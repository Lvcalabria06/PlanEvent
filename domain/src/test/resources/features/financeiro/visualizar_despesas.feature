Feature: Visualizar despesas do evento

  Como gestor financeiro
  Quero visualizar as despesas registradas
  Para acompanhar os gastos do evento

  Scenario: Buscar despesa por identificador
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    And existe uma despesa registrada de 300.00 na categoria "ALIMENTACAO"
    When eu buscar essa despesa pelo id
    Then a despesa retornada deve conter os dados corretos

  Scenario: Listar despesas do evento
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "EQUIPAMENTO" possui orçamento previsto de 5000.00
    And existe uma despesa registrada de 1000.00 na categoria "EQUIPAMENTO"
    When eu listar as despesas do evento
    Then a lista deve conter ao menos uma despesa

  Scenario: Buscar despesa inexistente retorna erro
    Given não existe despesa com o id informado
    When eu tentar buscar a despesa por id inexistente
    Then o sistema deve impedir a visualização da despesa
