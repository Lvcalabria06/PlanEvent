Feature: Remover dependência entre tarefas

Scenario: Remover dependência com sucesso
  Given existe uma dependência entre tarefa A e tarefa B
  When eu remover essa dependência
  Then a dependência é removida com sucesso

Scenario: Remover tarefa que é dependência de outra
  Given a tarefa B depende da tarefa A
  When eu tentar remover a tarefa A
  Then o sistema deve impedir a remoção

Scenario: Remover tarefa após remover dependência
  Given a tarefa B depende da tarefa A
  And a dependência foi removida
  When eu remover a tarefa A
  Then a tarefa é removida com sucesso