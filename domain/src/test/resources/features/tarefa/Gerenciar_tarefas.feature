Feature: Gerenciar tarefas

  # Atribuir responsável à tarefa
  Scenario: Atribuir funcionário da equipe
    Given existe uma equipe com um funcionário
    And existe uma tarefa dessa equipe
    When eu atribuir o funcionário à tarefa
    Then o responsável é adicionado com sucesso

  Scenario: Atribuir funcionário de outra equipe
    Given existe uma tarefa
    And existe um funcionário que não pertence à equipe
    When eu tentar atribuir esse funcionário
    Then o sistema deve impedir a ação

  # Criar tarefa
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

  # Editar tarefa
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

  # Atualizar status da tarefa
  Scenario: Iniciar tarefa com responsável
    Given existe uma tarefa pendente
    And existe um funcionário atribuído à tarefa
    When eu iniciar a tarefa
    Then o status deve ser "EM_ANDAMENTO"

  Scenario: Iniciar tarefa sem responsável
    Given existe uma tarefa pendente
    And não há responsáveis atribuídos
    When eu tentar iniciar a tarefa
    Then o sistema deve impedir a ação

  Scenario: Concluir tarefa corretamente
    Given existe uma tarefa em andamento
    When eu concluir a tarefa
    Then o status deve ser "CONCLUIDA"

  Scenario: Pular status
    Given existe uma tarefa pendente
    When eu tentar marcar como concluída diretamente
    Then o sistema deve impedir a ação

  # Remover tarefa
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
