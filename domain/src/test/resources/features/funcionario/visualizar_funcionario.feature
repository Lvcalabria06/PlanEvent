Feature: Visualizar funcionários

  Como gestor
  Quero visualizar funcionários no sistema
  Para acompanhar a equipe cadastrada

  Scenario: Visualizar lista de funcionários com sucesso
    Given existem funcionários cadastrados no sistema
    When o gestor solicitar a visualização dos funcionários
    Then o sistema deve exibir a lista de funcionários cadastrados

  Scenario: Exibir dados principais do funcionário
    Given existe um funcionário cadastrado no sistema
    When o gestor visualizar os funcionários
    Then o sistema deve exibir nome, cargo e disponibilidade do funcionário

  Scenario: Não exibir funcionários inativos na listagem padrão
    Given existe um funcionário inativo no sistema
    When o gestor visualizar a lista padrão de funcionários
    Then o sistema não deve exibir o funcionário inativo

  Scenario: Exibir mensagem quando não houver funcionários cadastrados
    Given não existem funcionários cadastrados no sistema
    When o gestor solicitar a visualização dos funcionários
    Then o sistema deve informar que não há funcionários cadastrados