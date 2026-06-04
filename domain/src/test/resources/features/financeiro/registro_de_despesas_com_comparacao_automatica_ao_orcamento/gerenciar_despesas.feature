Feature: Gerenciar despesas do evento

  Como gestor financeiro
  Quero alterar, excluir e pesquisar despesas enquanto permitido pelo status
  Para manter o controle orçamentário sem comprometer registros aprovados


  Scenario: Alterar valor de despesa com status REGISTRADA
    Given existe um evento válido para despesas
    And existe um orçamento cadastrado para o evento
    And a categoria "ALIMENTACAO" possui orçamento previsto de 1000.00
    And existe uma despesa registrada de 300.00 na categoria "ALIMENTACAO"
    When eu alterar o valor da despesa para 350.00
    Then o valor da despesa deve ser 350.00


  Scenario: Impedir alteração de despesa já aprovada
    Given existe uma despesa já aprovada para gerenciamento
    When eu tentar alterar o valor da despesa aprovada para 500.00
    Then o sistema deve impedir a alteração da despesa


  Scenario: Excluir despesa com status REGISTRADA
    Given existe um evento válido para despesas
    And existe uma despesa registrada de 200.00 na categoria "DECORACAO"
    When eu excluir essa despesa
    Then a despesa deve ser removida com sucesso


  Scenario: Impedir exclusão de despesa já aprovada
    Given existe uma despesa já aprovada para gerenciamento
    When eu tentar excluir a despesa aprovada
    Then o sistema deve impedir a exclusão da despesa


  Scenario: Pesquisar despesas por categoria
    Given existe um evento válido para despesas
    And existe uma despesa registrada de 400.00 na categoria "EQUIPAMENTO"
    When eu pesquisar despesas da categoria "EQUIPAMENTO"
    Then a pesquisa deve retornar ao menos 1 despesa


  Scenario: Pesquisar despesas por fornecedor
    Given existe um evento válido para despesas
    And existe uma despesa registrada de 250.00 na categoria "SERVICO"
    When eu pesquisar despesas do fornecedor "fornecedor-1"
    Then a pesquisa deve retornar ao menos 1 despesa
