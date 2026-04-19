Feature: Encerrar contratos

  Como gestor
  Quero encerrar contratos
  Para finalizar formalmente acordos concluídos

  Scenario: Encerrar contrato completo com sucesso
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    When eu encerrar esse contrato
    Then o contrato passa ao status ENCERRADO

  Scenario: Impedir encerramento quando informações estão incompletas
    Given existe um evento válido para contrato
    And existe um contrato cadastrado nesse evento
    But o contrato é considerado incompleto para encerramento
    When eu tentar encerrar esse contrato
    Then o sistema deve impedir o encerramento do contrato

  Scenario: Impedir encerrar contrato já encerrado
    Given existe um evento válido para contrato
    And existe um contrato encerrado nesse evento
    When eu tentar encerrar novamente esse contrato
    Then o sistema deve impedir o encerramento do contrato
