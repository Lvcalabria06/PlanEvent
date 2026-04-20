Feature: Iniciar tarefa com dependências

Scenario: Iniciar tarefa com dependências concluídas
  Given a tarefa B depende da tarefa A
  And a tarefa A está concluída
  When eu iniciar a tarefa B
  Then a tarefa B é iniciada com sucesso

Scenario: Iniciar tarefa com dependência não concluída
  Given a tarefa B depende da tarefa A
  And a tarefa A não está concluída
  When eu tentar iniciar a tarefa B
  Then o sistema deve impedir a ação