Feature: Gerenciar Locais
  Como gestor
  Quero gerenciar locais no sistema
  Para disponibilizá-los, editá-los, consultá-los ou desativá-los

  Scenario: Cadastrar local com sucesso
    Given que eu possuo os dados do local: nome "Centro de Convenções", capacidade 500, endereco "Rua X", tipo "Auditório", infraestrutura "Projetor, Som" e custo 1500.00
    When eu cadastro o local
    Then o local deve ser salvo com sucesso
    And o status do local deve ser "ATIVO"
    And a capacidade deve ser 500

  Scenario: Impedir cadastro com campos obrigatorios ausentes
    Given que eu possuo os dados do local: nome "   ", capacidade 0, endereco "", tipo "", infraestrutura "" e custo 0.00
    When eu tento cadastrar o local
    Then deve ocorrer um erro informando que os campos obrigatórios estão ausentes

  Scenario: Editar local existente
    Given que eu tenho um local cadastrado com nome "Sala 1", capacidade 50, endereco "Rua Y", tipo "Sala", infraestrutura "Quadro" e custo 100.00
    When eu edito o local para ter o nome "Sala 1 Modificada", capacidade 60, endereco "Rua Y", tipo "Sala", infraestrutura "Quadro Branco" e custo 120.00
    Then as informações do local devem ser atualizadas com sucesso
    And o updatedAt deve ser modificado

  Scenario: Desativar local
    Given que eu tenho um local cadastrado com nome "Espaço Antigo", capacidade 200, endereco "Av Z", tipo "Saguão", infraestrutura "Luzes" e custo 500.00
    When eu desativo o local
    Then o status do local deve ser "INATIVO"
    And eu não devo poder realizar novas reservas no local

  Scenario: Visualizar locais mantendo historico de inativos
    Given que eu tenho um local cadastrado com nome "Ativo", capacidade 100, endereco "A", tipo "B", infraestrutura "C" e custo 0
    And eu tenho um local inativo com nome "Inativo", capacidade 50, endereco "X", tipo "Y", infraestrutura "Z" e custo 0
    When eu listo todos os locais
    Then a lista deve conter tanto "Ativo" quanto "Inativo"
