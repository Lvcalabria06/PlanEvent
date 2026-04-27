Feature: Gerenciar funcionário

  Como gestor
  Quero gerenciar funcionários no sistema
  Para manter a equipe organizada e os dados atualizados

  Scenario: Cadastrar funcionário com sucesso
    Given o gestor informa um nome válido, cargo permitido e disponibilidade válida
    When o gestor cadastrar o funcionário no sistema
    Then o funcionário é salvo com sucesso

  Scenario: Impedir cadastro sem nome
    Given o gestor informa cargo e disponibilidade válidos
    When o gestor tentar cadastrar um funcionário sem nome
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Impedir cadastro com nome inválido
    Given o gestor informa um nome com menos de 3 caracteres ou com caracteres inválidos
    When o gestor tentar cadastrar o funcionário
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Impedir cadastro com cargo inválido
    Given o gestor informa um nome válido e disponibilidade válida
    When o gestor tentar cadastrar um funcionário com cargo fora dos valores permitidos
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Impedir cadastro com disponibilidade inválida
    Given o gestor informa um nome válido e cargo permitido
    When o gestor tentar cadastrar um funcionário com disponibilidade inválida
    Then o sistema deve impedir o cadastro do funcionário

  Scenario: Garantir geração automática de identificador
    Given o gestor informa dados válidos para cadastro
    When o gestor cadastrar o funcionário
    Then o sistema deve gerar automaticamente um identificador único e imutável

  Scenario: Garantir registro de datas automáticas
    Given o gestor informa dados válidos para cadastro
    When o gestor cadastrar o funcionário
    Then o sistema deve definir automaticamente createdAt e updatedAt

  Scenario: Editar funcionário com sucesso
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor editar o nome, cargo ou disponibilidade com valores válidos
    Then o sistema deve atualizar os dados do funcionário com sucesso

  Scenario: Impedir edição com nome inválido
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar editar o funcionário com nome com menos de 3 caracteres ou com caracteres inválidos
    Then o sistema deve impedir a edição do funcionário

  Scenario: Impedir edição com cargo inválido
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar editar o funcionário com cargo fora dos valores permitidos
    Then o sistema deve impedir a edição do funcionário

  Scenario: Impedir edição com disponibilidade inválida
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar editar o funcionário com disponibilidade fora do padrão permitido
    Then o sistema deve impedir a edição do funcionário

  Scenario: Atualizar updatedAt automaticamente na edição
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor editar qualquer dado válido do funcionário
    Then o sistema deve atualizar automaticamente o campo updatedAt

  Scenario: Impedir alteração manual de createdAt
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar alterar manualmente o campo createdAt
    Then o sistema deve impedir a edição do campo createdAt

  Scenario: Impedir alteração manual de updatedAt
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar alterar manualmente o campo updatedAt
    Then o sistema deve impedir a edição do campo updatedAt

  Scenario: Impedir edição que deixe o funcionário em estado inválido
    Given existe um funcionário ativo cadastrado no sistema
    When o gestor tentar salvar alterações inconsistentes no funcionário
    Then o sistema deve impedir a edição do funcionário

  Scenario: Excluir funcionário com sucesso
    Given existe um funcionário ativo sem vínculo com evento ou equipe
    When o gestor solicitar a exclusão do funcionário
    Then o sistema deve inativar o funcionário com sucesso

  Scenario: Impedir exclusão de funcionário vinculado a evento
    Given existe um funcionário ativo vinculado a um evento
    When o gestor solicitar a exclusão do funcionário
    Then o sistema deve impedir a exclusão do funcionário

  Scenario: Impedir exclusão de funcionário vinculado a equipe
    Given existe um funcionário ativo vinculado a uma equipe
    When o gestor solicitar a exclusão do funcionário
    Then o sistema deve impedir a exclusão do funcionário

  Scenario: Visualizar lista de funcionários com sucesso
    Given existem funcionários cadastrados no sistema
    When o gestor solicitar a visualização dos funcionários
    Then o sistema deve exibir a lista de funcionários cadastrados

  Scenario: Exibir dados principais do funcionário
    Given existe um funcionário cadastrado no sistema
    When o gestor visualizar os funcionários
    Then o sistema deve exibir nome, cargo e disponibilidade do funcionário

  Scenario: Não exibir funcionários inativos na listagem padrão
    Given existe um funcionário inativo no sistema
    When o gestor visualizar a lista padrão de funcionários
    Then o sistema não deve exibir o funcionário inativo

  Scenario: Exibir mensagem quando não houver funcionários cadastrados
    Given não existem funcionários cadastrados no sistema
    When o gestor solicitar a visualização dos funcionários
    Then o sistema deve informar que não há funcionários cadastrados