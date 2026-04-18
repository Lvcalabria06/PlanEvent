Feature: Atribuir responsável à tarefa

Scenario: Atribuir funcionário da equipe
  Given existe uma equipe com um funcionário
  And existe uma tarefa dessa equipe
  When eu atribuir o funcionário à tarefa
  Then o responsável é adicionado com sucesso

Scenario: Atribuir funcionário de outra equipe
  Given existe uma tarefa
  And existe um funcionário que não pertence à equipe
  When eu tentar atribuir esse funcionário
  Then o sistema deve impedir a ação