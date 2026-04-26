Feature: Conciliar despesas do evento com contratos vigentes

  Como gestor
  Quero conciliar as despesas do evento com os contratos vigentes
  Para verificar se todos os gastos possuem cobertura contratual válida

  Scenario: Despesa compatível é vinculada automaticamente ao contrato
    Given existe um evento para conciliação
    And existe um contrato ativo e vigente nesse evento para o fornecedor "forn-1"
    And existe uma despesa válida do fornecedor "forn-1" nesse evento dentro da vigência
    When o gestor executa a conciliação automática
    Then a despesa é vinculada ao contrato pelo método automático

  Scenario: Despesa sem contrato compatível permanece sem cobertura
    Given existe um evento para conciliação
    And não existe contrato compatível com a despesa nesse evento
    And existe uma despesa válida do fornecedor "forn-1" nesse evento dentro da vigência
    When o gestor executa a conciliação automática
    Then a despesa não possui cobertura contratual

  Scenario: Contrato encerrado não participa da conciliação automática
    Given existe um evento para conciliação
    And existe um contrato encerrado nesse evento para o fornecedor "forn-1"
    And existe uma despesa válida do fornecedor "forn-1" nesse evento dentro da vigência
    When o gestor executa a conciliação automática
    Then a despesa não possui cobertura contratual

  Scenario: Despesa fora da vigência contratual permanece sem cobertura
    Given existe um evento para conciliação
    And existe um contrato ativo e vigente nesse evento para o fornecedor "forn-1"
    And existe uma despesa do fornecedor "forn-1" com data fora da vigência do contrato
    When o gestor executa a conciliação automática
    Then a despesa não possui cobertura contratual
