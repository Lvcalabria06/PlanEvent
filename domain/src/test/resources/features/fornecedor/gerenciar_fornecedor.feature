Feature: Gerenciar fornecedores
  Como gestor
  Quero cadastrar, editar, visualizar e desativar fornecedores
  Para disponibilizá-los em contratos e despesas do evento

  Scenario: Cadastrar fornecedor com sucesso
    Given que eu possuo os dados do fornecedor: nome "Audio Pro", cnpj "11.444.777/0001-61", categoria "Som" e contato "contato@audiopro.com"
    When eu cadastro o fornecedor
    Then o fornecedor deve ser salvo com sucesso
    And o status do fornecedor deve ser "ATIVO"

  Scenario: Impedir cadastro com CNPJ inválido
    Given que eu possuo os dados do fornecedor: nome "Audio Pro", cnpj "11.111.111/1111-11", categoria "Som" e contato "contato@audiopro.com"
    When eu tento cadastrar o fornecedor
    Then deve ocorrer um erro ao cadastrar o fornecedor

  Scenario: Impedir cadastro com CNPJ duplicado
    Given que existe um fornecedor cadastrado com cnpj "11.444.777/0001-61"
    And que eu possuo os dados do fornecedor: nome "Outro Fornecedor", cnpj "11.444.777/0001-61", categoria "Luz" e contato "outro@fornecedor.com"
    When eu tento cadastrar o fornecedor
    Then deve ocorrer um erro ao cadastrar o fornecedor

  Scenario: Impedir cadastro com campos obrigatórios ausentes
    Given que eu possuo os dados do fornecedor: nome "   ", cnpj "", categoria "" e contato ""
    When eu tento cadastrar o fornecedor
    Then deve ocorrer um erro ao cadastrar o fornecedor

  Scenario: Editar fornecedor ativo
    Given que eu tenho um fornecedor ativo cadastrado com nome "Buffet Central", cnpj "04.252.011/0001-10", categoria "Alimentação" e contato "buffet@central.com"
    When eu edito o fornecedor para ter o nome "Buffet Central Plus", cnpj "04.252.011/0001-10", categoria "Alimentação" e contato "novo@central.com"
    Then as informações do fornecedor devem ser atualizadas com sucesso

  Scenario: Impedir edição de fornecedor inativo
    Given que eu tenho um fornecedor inativo cadastrado com nome "Fornecedor Antigo", cnpj "60.746.948/0001-12", categoria "Transporte" e contato "antigo@fornecedor.com"
    When eu tento editar o fornecedor para ter o nome "Fornecedor Antigo Atualizado", cnpj "60.746.948/0001-12", categoria "Transporte" e contato "novo@fornecedor.com"
    Then deve ocorrer um erro ao editar o fornecedor

  Scenario: Visualizar fornecedor por identificador e listar cadastrados
    Given que eu tenho um fornecedor ativo cadastrado com nome "Segurança Total", cnpj "45.723.174/0001-10", categoria "Segurança" e contato "seg@total.com"
    When eu busco o fornecedor pelo id
    And eu listo todos os fornecedores
    Then o fornecedor retornado contém o nome "Segurança Total"
    And a lista de fornecedores contém ao menos um item

  Scenario: Impedir visualização de fornecedor inexistente
    Given que não existe fornecedor com o id informado
    When eu tento buscar fornecedor por id inexistente
    Then deve ocorrer um erro ao buscar o fornecedor

  Scenario: Desativar fornecedor sem vínculos impeditivos
    Given que eu tenho um fornecedor ativo cadastrado com nome "Limpeza Express", cnpj "33.000.167/0001-01", categoria "Limpeza" e contato "limpeza@express.com"
    When eu desativo o fornecedor
    Then o status do fornecedor deve ser "INATIVO"

  Scenario: Impedir desativação com contrato ativo
    Given que eu tenho um fornecedor ativo cadastrado com nome "Palco Master", cnpj "11.222.333/0001-81", categoria "Estrutura" e contato "palco@master.com"
    And o fornecedor possui contrato ativo vinculado
    When eu tento desativar o fornecedor
    Then deve ocorrer um erro ao desativar o fornecedor

  Scenario: Impedir desativação com despesa em evento em andamento
    Given que eu tenho um fornecedor ativo cadastrado com nome "Decora Fest", cnpj "17.895.646/0001-87", categoria "Decoração" e contato "decora@fest.com"
    And o fornecedor possui despesa em evento em andamento
    When eu tento desativar o fornecedor
    Then deve ocorrer um erro ao desativar o fornecedor

  Scenario: Impedir vinculação de fornecedor inativo a novo contrato
    Given que eu tenho um fornecedor inativo cadastrado com nome "Fornecedor Bloqueado", cnpj "27.865.757/0001-02", categoria "Serviços" e contato "bloqueado@fornecedor.com"
    And existe um evento válido para contrato com fornecedor inativo
    When eu tento cadastrar um contrato completo para esse evento
    Then o sistema deve impedir o cadastro do contrato
