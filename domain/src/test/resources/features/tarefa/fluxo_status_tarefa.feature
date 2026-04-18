Feature: Atualizar status da tarefa

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