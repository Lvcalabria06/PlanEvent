Feature: Visualizar o relatório de conciliação do evento

  Como gestor
  Quero visualizar o relatório de conciliação do evento
  Para ter uma visão consolidada da cobertura contratual de todas as despesas

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
