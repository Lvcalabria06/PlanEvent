Feature: Criar tarefa

Scenario: Criar tarefa com sucesso
  Given existe um evento válido
  And existe uma equipe válida associada ao evento
  And não existe tarefa com título "Montar palco" na equipe
  When eu criar uma tarefa com título "Montar palco"
  Then a tarefa é criada com sucesso

Scenario: Criar tarefa sem equipe válida
  Given não existe equipe válida
  When eu tentar criar uma tarefa
  Then o sistema deve impedir a criação

Scenario: Criar tarefa com título duplicado na equipe
  Given existe uma equipe válida
  And já existe uma tarefa com título "Montar palco"
  When eu tentar criar outra tarefa com título "Montar palco"
  Then o sistema deve impedir a criação

Scenario: Criar tarefa sem título
  Given existe uma equipe válida
  When eu tentar criar uma tarefa sem título
  Then o sistema deve impedir a criação

Scenario: Criar tarefa com data inválida
  Given existe uma equipe válida
  When eu tentar criar uma tarefa com data de fim anterior à data de início
  Then o sistema deve impedir a criação