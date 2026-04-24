Feature: Excluir equipe do evento

    Como gestor
    Quero remover uma equipe de um evento
    Para reorganizar ou corrigir a estrutura do evento

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