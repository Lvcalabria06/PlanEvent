Feature: Conciliação de despesas com contratos do evento

  Como gestor
  Quero conciliar despesas a contratos, acompanhar lacunas e extrapolações e gerar relatórios
  Para garantir cobertura contratual dos gastos e visibilidade para auditoria

  # Conciliação automática

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

  # Vínculo manual

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

  # Despesas descobertas

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

  # Contratos extrapolados

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

  # Relatório de conciliação

  Scenario: Relatório gerado consolida despesas cobertas e descobertas
    Given existe um evento para conciliação
    And existe uma despesa válida já vinculada a um contrato nesse evento
    And existe uma despesa válida sem vínculo de conciliação nesse evento
    When o gestor gera o relatório de conciliação do evento com responsável "gestor-1"
    Then o relatório contém itens cobertos e descobertos
    And o relatório registra o responsável "gestor-1" e a data de geração

  Scenario: Relatório é persistido e imutável após geração
    Given existe um evento para conciliação
    And existe uma despesa válida já vinculada a um contrato nesse evento
    When o gestor gera o relatório de conciliação do evento com responsável "gestor-1"
    Then o relatório é salvo pelo repositório

  Scenario: Relatório não pode ser gerado sem despesas elegíveis
    Given existe um evento para conciliação
    And não há despesas elegíveis para conciliação nesse evento
    When o gestor tentar gerar o relatório do evento
    Then o sistema deve impedir a geração do relatório
