Feature: Editar equipe do evento

    Como gestor
    Quero editar as informações de uma equipe do evento
    Para manter os dados atualizados

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