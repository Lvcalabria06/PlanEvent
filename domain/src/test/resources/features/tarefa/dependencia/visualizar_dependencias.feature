Feature: Visualizar dependências de uma tarefa

Scenario: Visualizar tarefas predecessoras
  Given a tarefa C depende das tarefas A e B
  When eu visualizar as dependências da tarefa C
  Then o sistema deve retornar A e B

Scenario: Visualizar tarefas sem dependências
  Given existe uma tarefa sem dependências
  When eu visualizar suas dependências
  Then o sistema deve informar que não há dependências