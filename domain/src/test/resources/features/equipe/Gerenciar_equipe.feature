Feature: Gerenciar equipe do evento

    Como gestor
    Quero gerenciar as equipes para um evento
    Para organizar os funcionários responsáveis pela execução e manter a estrutura atualizada

    Scenario: Cadastrar equipe com sucesso
        Given o gestor informa um evento válido, nome único e funcionários válidos
        And os funcionários não estão alocados em outra equipe do evento
        And a equipe possui no máximo um responsável válido pertencente à equipe
        When o gestor cadastrar a equipe no sistema
        Then a equipe é salva com sucesso

    Scenario: Impedir cadastro sem evento válido
        Given não existe evento válido
        When o gestor tentar cadastrar uma equipe
        Then o sistema deve impedir o cadastro da equipe

    Scenario: Impedir cadastro com nome duplicado no evento
        Given existe um evento válido
        And já existe uma equipe com o mesmo nome no evento
        When o gestor tentar cadastrar uma equipe
        Then o sistema deve impedir o cadastro da equipe

    Scenario: Impedir cadastro sem funcionários
        Given existe um evento válido
        When o gestor tentar cadastrar uma equipe sem funcionários
        Then o sistema deve impedir o cadastro da equipe

    Scenario: Impedir cadastro com funcionário já alocado no evento
        Given existe um evento válido
        And um funcionário já está alocado em outra equipe do mesmo evento
        When o gestor tentar cadastrar uma equipe
        Then o sistema deve impedir o cadastro da equipe

    Scenario: Impedir cadastro com mais de um responsável
        Given existe um evento válido e funcionários válidos
        When o gestor tentar cadastrar uma equipe com mais de um responsável
        Then o sistema deve impedir o cadastro da equipe

    Scenario: Impedir cadastro com responsável inválido
        Given existe um evento válido e funcionários válidos
        When o gestor tentar cadastrar uma equipe com responsável inválido
        Then o sistema deve impedir o cadastro da equipe

    Scenario: Editar nome da equipe com sucesso
        Given existe uma equipe cadastrada
        And o novo nome não está sendo utilizado no evento
        When o gestor editar o nome da equipe
        Then a equipe é atualizada com sucesso

    Scenario: Impedir edição que deixe equipe sem funcionários
        Given existe uma equipe cadastrada
        When o gestor tentar remover todos os funcionários da equipe
        Then o sistema deve impedir a edição da equipe

    Scenario: Impedir remoção do líder sem definir novo responsável
        Given existe uma equipe cadastrada com líder e outro membro
        When o gestor tentar remover o líder sem definir novo responsável
        Then o sistema deve impedir a edição da equipe

    Scenario: Remover equipe com sucesso
        Given existe uma equipe cadastrada
        And a equipe não possui tarefas em andamento
        When o gestor remover a equipe
        Then a equipe é removida com sucesso

    Scenario: Impedir remoção de equipe com tarefas em andamento
        Given existe uma equipe cadastrada
        And a equipe possui tarefas em andamento
        When o gestor tentar remover a equipe
        Then o sistema deve impedir a remoção da equipe

    Scenario: Visualizar equipes do evento
        Given existe um evento válido
        And existem equipes cadastradas para o evento
        When o gestor listar as equipes
        Then o sistema deve exibir as equipes do evento