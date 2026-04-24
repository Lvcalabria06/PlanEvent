Feature: Cadastrar equipe do evento

    Como gestor
    Quero cadastrar uma equipe para um evento
    Para organizar os funcionários responsáveis pela execução

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