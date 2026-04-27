Feature: Gerenciar dependência entre tarefas

  # Definir dependência entre tarefas
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

  # Impacto de alteração de datas em tarefas dependentes
  Scenario: Alterar data de tarefa predecessora impacta dependente
    Given a tarefa B depende da tarefa A
    And a tarefa A tem sua data de conclusão alterada para depois do início de B
    When a alteração é realizada
    Then a tarefa B deve ser marcada como potencialmente atrasada

  # Iniciar tarefa com dependências
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

  # Remover dependência entre tarefas
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

  # Visualizar dependências de uma tarefa
  Scenario: Visualizar tarefas predecessoras
    Given a tarefa C depende das tarefas A e B
    When eu visualizar as dependências da tarefa C
    Then o sistema deve retornar A e B

  Scenario: Visualizar tarefas sem dependências
    Given existe uma tarefa sem dependências
    When eu visualizar suas dependências
    Then o sistema deve informar que não há dependências

  # Visualizar tarefas dependentes
  Scenario: Visualizar tarefas que dependem de uma tarefa
    Given as tarefas B e C dependem da tarefa A
    When eu visualizar tarefas dependentes de A
    Then o sistema deve retornar B e C

  Scenario: Visualizar tarefa sem dependentes
    Given existe uma tarefa sem dependentes
    When eu visualizar tarefas dependentes dela
    Then o sistema deve informar que não há dependentes
