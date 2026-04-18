# Arquivos de Funcionalidade (Features)

Esta pasta (`src/test/resources/features`) é destinada para armazenar seus arquivos de especificação BDD, chamados de arquivos `.feature`.

## O que colocar nesta pasta:
- Arquivos com extensão `.feature` (ex: `criar_tarefa.feature`, `dependencias_tarefa.feature`).
- Cada arquivo `.feature` contém descrições de cenários escritos na linguagem estruturada **Gherkin**.

## O que você faz aqui:
- Você descreve o comportamento do sistema do ponto de vista do usuário/negócio em texto legível.
- Você usa as palavras-chave `Funcionalidade` (Feature), `Cenário` (Scenario), `Dado` (Given), `Quando` (When) e `Então` (Then) para definir o que o sistema deve fazer.

**Exemplo:**
```gherkin
Funcionalidade: Gerenciamento de dependências de Tarefas
  Cenário: Adicionar uma dependência bloqueante com sucesso
    Dado que a tarefa "Testar backend" está em aberto
    Quando eu adiciono a dependência "Criar testes unitários" à tarefa
    Então a tarefa principal deve registrar essa nova dependência
```
