Feature: Registrar associação manual entre despesa e contrato

  Como gestor
  Quero registrar manualmente a associação entre uma despesa e um contrato
  Quando a conciliação automática não conseguir estabelecer o vínculo

  Scenario: Vínculo manual com contrato ativo e vigente é registrado com sucesso
    Given existe um evento para conciliação
    And existe um contrato ativo e vigente nesse evento para o fornecedor "forn-1"
    And existe uma despesa válida do fornecedor "forn-1" nesse evento dentro da vigência
    When o gestor vincula manualmente a despesa ao contrato com responsável "gestor-1"
    Then o vínculo é registrado como manual com o responsável "gestor-1"

  Scenario: Impedir vínculo manual com contrato encerrado
    Given existe um evento para conciliação
    And existe um contrato encerrado nesse evento para o fornecedor "forn-1"
    And existe uma despesa válida do fornecedor "forn-1" nesse evento dentro da vigência
    When o gestor tentar vincular manualmente a despesa ao contrato encerrado
    Then o sistema deve impedir o vínculo manual

  Scenario: Impedir vínculo manual com contrato de outro evento
    Given existe um evento para conciliação
    And existe um contrato ativo pertencente a outro evento
    And existe uma despesa válida do fornecedor "forn-1" nesse evento dentro da vigência
    When o gestor tentar vincular manualmente a despesa ao contrato de outro evento
    Then o sistema deve impedir o vínculo manual

  Scenario: Impedir vínculo manual com contrato fora da vigência na data da despesa
    Given existe um evento para conciliação
    And existe um contrato ativo e vigente nesse evento para o fornecedor "forn-1"
    And existe uma despesa do fornecedor "forn-1" com data fora da vigência do contrato
    When o gestor tentar vincular manualmente essa despesa ao contrato
    Then o sistema deve impedir o vínculo manual

  Scenario: Vínculo manual substitui conciliação anterior e registra responsável
    Given existe um evento para conciliação
    And existe um contrato ativo e vigente nesse evento para o fornecedor "forn-1"
    And existe uma despesa já vinculada automaticamente nesse evento
    When o gestor substitui o vínculo manualmente com responsável "gestor-2"
    Then o vínculo é atualizado para manual com o responsável "gestor-2"
