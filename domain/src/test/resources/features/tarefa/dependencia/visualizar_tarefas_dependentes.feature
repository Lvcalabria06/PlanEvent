Feature: Visualizar tarefas dependentes

Scenario: Visualizar tarefas que dependem de uma tarefa
  Given as tarefas B e C dependem da tarefa A
  When eu visualizar tarefas dependentes de A
  Then o sistema deve retornar B e C

Scenario: Visualizar tarefa sem dependentes
  Given existe uma tarefa sem dependentes
  When eu visualizar tarefas dependentes dela
  Then o sistema deve informar que não há dependentes