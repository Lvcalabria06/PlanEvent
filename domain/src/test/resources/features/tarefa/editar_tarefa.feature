Feature: Editar tarefa

Scenario: Editar tarefa com sucesso
  Given existe uma tarefa pendente
  When eu editar o título da tarefa
  Then a tarefa é atualizada com sucesso

Scenario: Editar tarefa com título duplicado
  Given existe uma equipe válida
  And existe uma tarefa com título "A"
  And existe outra tarefa com título "B"
  When eu tentar alterar o título da tarefa "B" para "A"
  Then o sistema deve impedir a edição

Scenario: Editar tarefa com data inválida
  Given existe uma tarefa pendente
  When eu tentar editar com data de fim anterior à de início
  Then o sistema deve impedir a edição

Scenario: Editar tarefa concluída
  Given existe uma tarefa concluída
  When eu tentar editar a tarefa
  Then o sistema deve impedir a edição