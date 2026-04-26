Feature: Imutabilidade e auditoria do relatório financeiro

  Como controller
  Quero que os relatórios gerados sejam imutáveis e auditáveis
  Para garantir a integridade dos dados apresentados aos stakeholders

  Scenario: Relatório não pode ser editado após geração
    Given existe um relatório financeiro já gerado
    When eu tentar modificar o conteúdo do relatório
    Then o sistema deve impedir a edição do relatório

  Scenario: Múltiplos relatórios podem ser gerados para o mesmo evento
    Given existe um evento válido para relatório
    And existe um orçamento cadastrado para o evento do relatório com categorias
    And o orçamento da categoria "MARKETING" é de 1000.0 para o relatório
    When eu gerar dois relatórios do mesmo evento em momentos distintos
    Then os dois relatórios devem ter identificadores diferentes
    And os dois relatórios devem ter datas de geração registradas

  Scenario: Buscar relatório gerado por identificador
    Given existe um relatório financeiro já gerado
    When eu buscar o relatório pelo id
    Then o relatório retornado deve conter os dados corretos

  Scenario: Listar todos os relatórios de um evento
    Given existe um evento válido para relatório
    And existem dois relatórios gerados para o evento
    When eu listar os relatórios do evento
    Then a lista deve conter dois relatórios

  Scenario: Buscar relatório inexistente retorna erro
    Given não existe relatório com o id informado
    When eu tentar buscar o relatório por id inexistente
    Then o sistema deve impedir a visualização do relatório