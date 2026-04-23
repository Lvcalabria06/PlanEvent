Feature: Impacto de alteração de datas em tarefas dependentes

Scenario: Alterar data de tarefa predecessora impacta dependente
  Given a tarefa B depende da tarefa A
  And a tarefa A tem sua data de conclusão alterada para depois do início de B
  When a alteração é realizada
  Then a tarefa B deve ser marcada como potencialmente atrasada