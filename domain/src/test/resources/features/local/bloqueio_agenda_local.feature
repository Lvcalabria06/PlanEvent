Feature: Bloqueio de agenda do local
  Como gestor
  Quero que respeitem indisponibilidade e conflitos de reserva
  Para não agendar em horário inválido

  Scenario: Reserva permitida sem indisponibilidade nem conflito
    Given a preparação de bloqueio de agenda com um local ativo
    When eu tento reservar o local de "2025-01-10T10:00" a "2025-01-10T12:00" para o evento "E1"
    Then a reserva é aceita

  Scenario: Reserva rejeitada por indisponibilidade configurada
    Given a preparação de bloqueio de agenda com um local ativo
    And existe indisponibilidade no local de "2025-01-10T09:00" a "2025-01-10T15:00" com motivo "Obras"
    When eu tento reservar o local de "2025-01-10T10:00" a "2025-01-10T11:00" para o evento "E1"
    Then a reserva é rejeitada
    And a mensagem explica bloqueio por indisponibilidade

  Scenario: Reserva rejeitada por conflito com reserva existente
    Given a preparação de bloqueio de agenda com um local ativo
    And já existe reserva no local de "2025-01-12T14:00" a "2025-01-12T18:00" para o evento "E0"
    When eu tento reservar o local de "2025-01-12T16:00" a "2025-01-12T17:00" para o evento "E1"
    Then a reserva é rejeitada
    And a mensagem explica conflito com reserva

  Scenario: Após alterar a agenda com indisponibilidade, validação pega nova requisição
    Given a preparação de bloqueio de agenda com um local ativo
    And eu registro reserva com sucesso de "2025-01-20T09:00" a "2025-01-20T10:00" para o evento "E-ok"
    When o gestor bloqueia o local de "2025-01-20T08:00" a "2025-01-20T20:00" com motivo "Fechadura geral"
    And tenta reservar o local de "2025-01-20T10:00" a "2025-01-20T11:00" para o evento "E-novo"
    Then a reserva é rejeitada
