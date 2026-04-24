Feature: Visualizar equipes do evento

    Como gestor
    Quero visualizar as equipes de um evento
    Para acompanhar a organização e distribuição dos funcionários

    Scenario: Visualizar equipes do evento
        Given existe um evento válido
        And existem equipes cadastradas para o evento
        When o gestor listar as equipes
        Then o sistema deve exibir as equipes do evento