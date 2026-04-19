Feature: Excluir funcionário

  Como gestor
  Quero excluir funcionário no sistema
  Para impedir seu uso futuro sem perder o histórico

  Scenario: Excluir funcionário com sucesso
    Given existe um funcionário ativo sem vínculo com evento ou equipe
    When o gestor solicitar a exclusão do funcionário
    Then o sistema deve inativar o funcionário com sucesso

  Scenario: Impedir exclusão de funcionário vinculado a evento
    Given existe um funcionário ativo vinculado a um evento
    When o gestor solicitar a exclusão do funcionário
    Then o sistema deve impedir a exclusão do funcionário

  Scenario: Impedir exclusão de funcionário vinculado a equipe
    Given existe um funcionário ativo vinculado a uma equipe
    When o gestor solicitar a exclusão do funcionário
    Then o sistema deve impedir a exclusão do funcionário