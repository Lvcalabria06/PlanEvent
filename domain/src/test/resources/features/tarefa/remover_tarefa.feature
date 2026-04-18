Feature: Remover tarefa

Scenario: Remover tarefa pendente
  Given existe uma tarefa pendente
  When eu remover a tarefa
  Then a tarefa é removida com sucesso

Scenario: Remover tarefa em andamento
  Given existe uma tarefa em andamento
  When eu tentar remover a tarefa
  Then o sistema deve impedir a remoção

Scenario: Remover tarefa concluída
  Given existe uma tarefa concluída
  When eu tentar remover a tarefa
  Then o sistema deve impedir a remoção