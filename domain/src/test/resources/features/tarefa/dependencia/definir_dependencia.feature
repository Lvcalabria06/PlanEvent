Feature: Definir dependência entre tarefas

Scenario: Criar dependência com sucesso
  Given existem duas tarefas do mesmo evento
  When eu definir que a tarefa B depende da tarefa A
  Then a dependência é criada com sucesso

Scenario: Criar dependência entre tarefas de eventos diferentes
  Given existem duas tarefas de eventos diferentes
  When eu tentar definir uma dependência entre elas
  Then o sistema deve impedir a criação

Scenario: Criar dependência cíclica direta
  Given existe uma tarefa A que depende da tarefa B
  When eu tentar fazer a tarefa B depender da tarefa A
  Then o sistema deve impedir a criação

Scenario: Criar dependência cíclica indireta
  Given A depende de B
  And B depende de C
  When eu tentar fazer C depender de A
  Then o sistema deve impedir a criação

Scenario: Criar dependência com múltiplas tarefas
  Given existem tarefas A, B e C do mesmo evento
  When eu definir que a tarefa C depende de A e B
  Then as dependências são criadas com sucesso

Scenario: Criar dependência com datas incompatíveis
  Given a tarefa A termina após a tarefa B iniciar
  When eu tentar fazer B depender de A
  Then o sistema deve impedir a criação

Scenario: Criar dependência de uma tarefa com ela mesma
  Given existe uma tarefa A
  When eu tentar fazer A depender de A
  Then o sistema deve impedir a criação