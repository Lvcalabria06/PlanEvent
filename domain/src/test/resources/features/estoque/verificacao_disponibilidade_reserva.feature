Feature: Verificacao de disponibilidade com reserva concorrente

  Como gestor
  Quero que o sistema verifique conflitos de reserva de itens entre eventos
  Para evitar alocacoes duplicadas e garantir a disponibilidade real do estoque

  Scenario: Sinalizar conflito por item, quantidade e periodo
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And existe uma reserva "confirmada" do item "item-cadeira" com quantidade 80 para o mesmo periodo
    And o gestor solicita reservar 30 unidades do item "item-cadeira"
    When o gestor verificar a disponibilidade da solicitacao
    Then o sistema deve sinalizar conflito de disponibilidade
    And o sistema deve exibir o item "item-cadeira" em conflito com o evento "evento-reserva-conflito"

  Scenario: Permitir reserva quando a disponibilidade for suficiente
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And o item "item-projetor" possui estoque total de 10 unidades
    And existem reservas pendentes, confirmadas ou em uso que nao comprometem a solicitacao
    And o gestor solicita reservar 30 unidades do item "item-cadeira"
    When o gestor verificar a disponibilidade da solicitacao
    And o gestor criar a reserva de estoque
    Then o sistema deve indicar que nao ha conflito de disponibilidade
    And o sistema deve considerar reservas pendentes, confirmadas e em uso no calculo
    And a reserva deve ser registrada vinculada ao evento e aos itens solicitados

  Scenario: Bloquear confirmacao quando ultrapassar a disponibilidade real
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And existe uma reserva pendente vinculada ao evento atual com conflito de disponibilidade
    When o gestor confirmar a reserva sem autorizacao especial
    Then o sistema deve bloquear a confirmacao da reserva

  Scenario: Revalidar disponibilidade apos alteracao na solicitacao
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And existe uma reserva "confirmada" do item "item-cadeira" com quantidade 60 para o mesmo periodo
    And existe uma reserva pendente vinculada ao evento atual
    When o gestor alterar a solicitacao para 40 unidades do item "item-cadeira"
    Then a solicitacao deve ser atualizada para nova validacao
    And o sistema deve recalcular a disponibilidade considerando a nova solicitacao

  Scenario: Confirmar reserva com autorizacao especial quando houver conflito
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And existe uma reserva pendente vinculada ao evento atual com conflito de disponibilidade
    When o gestor confirmar a reserva com justificativa "Aprovacao da diretoria para uso excepcional" e autorizacao especial
    Then a reserva deve ser confirmada por autorizacao especial

  Scenario: Exibir lista completa de itens em conflito
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And o item "item-projetor" possui estoque total de 10 unidades
    And existe uma reserva "confirmada" do item "item-cadeira" com quantidade 80 para o mesmo periodo
    And existe uma reserva "em uso" do item "item-projetor" com quantidade 9 para o mesmo periodo
    And o gestor solicita reservar os itens abaixo
      | item          | quantidade |
      | item-cadeira  | 30         |
      | item-projetor | 2          |
    When o gestor verificar a disponibilidade da solicitacao
    Then o sistema deve sinalizar conflito de disponibilidade
    And o sistema deve exibir os itens "item-cadeira" e "item-projetor" em conflito

  Scenario: Ignorar reservas fora do periodo da solicitacao
    Given existe um evento valido para reserva de estoque
    And o item "item-cadeira" possui estoque total de 100 unidades
    And existe uma reserva confirmada do item "item-cadeira" com quantidade 100 em periodo sem sobreposicao
    And o gestor solicita reservar 100 unidades do item "item-cadeira"
    When o gestor verificar a disponibilidade da solicitacao
    Then o sistema deve indicar que nao ha conflito de disponibilidade
