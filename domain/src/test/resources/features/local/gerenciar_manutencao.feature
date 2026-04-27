Feature: Gerenciar Manutenções
  Como gestor
  Quero cadastrar e gerenciar manutenções de um local
  Para controlar a indisponibilidade desses locais no sistema

  Scenario: Cadastrar manutencao com sucesso
    Given que existe um local cadastrado
    And não existem reservas conflitantes para o período de "2026-05-01T08:00" até "2026-05-05T18:00"
    When eu cadastro uma manutenção com início "2026-05-01T08:00", fim "2026-05-05T18:00" e responsável "João Gestor"
    Then a manutenção deve ser salva com sucesso
    And deve estar vinculada ao local
    And deve registrar o responsável "João Gestor" e a data da operação

  Scenario: Impedir cadastro de manutencao com data final anterior a inicial
    Given que existe um local cadastrado
    When eu tento cadastrar uma manutenção com início "2026-05-05T18:00", fim "2026-05-01T08:00" e responsável "João Gestor"
    Then deve ocorrer um erro informando que as datas são inválidas

  Scenario: Impedir cadastro de manutencao com campos obrigatorios ausentes
    Given que existe um local cadastrado
    When eu tento cadastrar uma manutenção sem informar os campos obrigatórios
    Then deve ocorrer um erro na manutenção informando que os campos obrigatórios estão ausentes

  Scenario: Impedir sobreposicao com reservas existentes
    Given que existe um local cadastrado
    And existe uma reserva aprovada para o local no período de "2026-06-10T10:00" até "2026-06-12T18:00"
    When eu tento cadastrar uma manutenção com início "2026-06-11T08:00", fim "2026-06-15T18:00" e responsável "Maria Gestora"
    Then deve ocorrer um erro informando conflito com reservas

  Scenario: Editar manutencao com revalidacao de conflito
    Given que existe um local cadastrado
    And eu tenho uma manutenção cadastrada com início "2026-07-01T08:00" e fim "2026-07-02T18:00"
    And não existem reservas conflitantes para o novo período
    When eu edito a manutenção para ter início "2026-07-01T08:00" e fim "2026-07-04T18:00"
    Then as informações da manutenção devem ser atualizadas com sucesso

  Scenario: Editar manutencao e impedir por conflito de reserva futura
    Given que existe um local cadastrado
    And eu tenho uma manutenção cadastrada com início "2026-08-01T08:00" e fim "2026-08-02T18:00"
    And existe uma reserva aprovada para o local no período de "2026-08-03T10:00" até "2026-08-05T18:00"
    When eu tento editar a manutenção para ter início "2026-08-01T08:00" e fim "2026-08-04T18:00"
    Then deve ocorrer um erro informando conflito com reservas

  Scenario: Consultar manutenções por local
    Given que existe um local cadastrado
    And eu tenho uma manutenção cadastrada com início "2026-09-01T08:00" e fim "2026-09-02T18:00"
    And eu tenho outra manutenção cadastrada com início "2026-09-10T08:00" e fim "2026-09-12T18:00"
    When eu listo as manutenções do local
    Then a lista deve conter as duas manutenções cadastradas

  Scenario: Remover manutencao existente
    Given que existe um local cadastrado
    And eu tenho uma manutenção cadastrada com início "2026-10-01T08:00" e fim "2026-10-02T18:00"
    When eu removo a manutenção
    Then a manutenção deve ser excluída com sucesso
