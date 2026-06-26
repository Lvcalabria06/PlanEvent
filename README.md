# EventOS - Sistema de Gestão de Eventos

## 1. Descrição do Domínio
O **EventOS** é um sistema voltado para o gerenciamento inteligente e completo de eventos.

O EventOS é um sistema inteligente de gestão de eventos que auxilia gestores a planejar, organizar e executar eventos de forma estruturada, garantindo controle operacional, financeiro, logístico e administrativo.

**Localização no repositório:**
`entregaveis/descricaodominio.txt`

## 2. Mapa de Histórias do Usuário
O User Story Map apresenta a estrutura das funcionalidades sob a ótica das histórias de usuário.

[Acessar o Mapa de Histórias do Usuário](https://www.figma.com/board/UtZHD9HZRh7F3WhnbhhAna/Untitled?node-id=0-1&t=ukSzPQyxWYwhqUnc-1)

## 3. Protótipo de Interface
O protótipo foi criado para validar os fluxos principais do sistema e garantir uma experiência de uso fluida e intuitiva.

[Acessar o Protótipo no Figma](https://www.figma.com/make/bErcGQJjdMs0ySafOYH52r/SaaS-Event-Management-Prototype?p=f&t=x7xKkuovh4qgACHf-0&fullscreen=1)

## 4. Context Mapping
O arquivo .cml foi elaborado com o Context Mapper, respeitando os princípios de Bounded Contexts e Ubiquitous Language.

**Localização no repositório:**
`entregaveis/eventos.cml`

## 5. Definição de Funcionalidades, User Stories e Regras de Negócio
Este documento apresenta a descrição detalhada das funcionalidades do sistema, acompanhada das respectivas histórias de usuário (User Stories) e regras de negócio (Business Rules), construídas a partir da linguagem onipresente e das necessidades identificadas no domínio.

**Localização no repositório:**
`entregaveis/Userstories.txt`

## 6. Cenários de Teste BDD
Os cenários de teste comportamentais (BDD) foram escritos no formato Gherkin, descrevendo os comportamentos esperados do sistema sob a ótica do usuário e do domínio.

**Localização no repositório:**
`domain/src/test/resources/features`

## 7. Automação de Testes BDD com Cucumber
Os cenários foram automatizados com o framework Cucumber, integrando as definições dos testes em Java com o domínio de negócio.

**Localização no repositório:**
`domain/src/test/java/domain`

Cada pacote de subdomínio (`agenda`, `tarefa`, `evento`, etc.) e suas respectivas classes de *steps* implementam os comportamentos descritos nos arquivos `.feature`, assegurando a validade das regras de domínio e o funcionamento das interações entre agregados.

## 8. Estruturação de pastas
O projeto segue uma estrutura baseada em Domain-Driven Design (DDD) e Clean Architecture, dividida nas seguintes pastas principais:
- **`application/`**: Casos de uso e orquestração do sistema.
- **`entregaveis/`**: Documentações (histórias de usuário, domínio), modelos (.cml) e links de referência.
- **`domain/`**: Coração do software (Entidades, Testes e Regras de Negócio).
- **`infrastructure/`**: Implementações de persistência e recursos externos.
- **`presentation-backend/`**: Controladores e APIs.
- **`presentation-frontend/`**: Interface web da aplicação.

## 9. Padrões de Projeto
Padrões utilizados por cada integrante.

Felipe Barros — Strategy
Victor Paes - Strategy
Henrique Gueiros — Iterator
Lucas Calabria — Iterator
Maria Julia — Template Method
Samuel Abreu — Interpreter
Maria Luíza — Interpreter
Artur Dowsley — Observer
Lucca - Template Method

## 10. Padrões de Projeto — Mapa de Arquivos

São 6 padrões distintos implementados.

---

### Iterator — Henrique Gueiros

**Intenção:** percorrer apenas os contratos ativos de um fornecedor sem expor a coleção interna de contratos nem vazar a lógica de filtragem para o serviço consumidor.

| Papel no padrão | Arquivo .java |
|---|---|
| Interface do iterador | `java.util.Iterator<Contrato>` (JDK — sem arquivo próprio) |
| Iterador concreto | `domain/contrato/iterator/IteradorContratosAtivos.java` |
| Agregado iterável (fornece o iterador) | `domain/contrato/iterator/ContratosAtivosFornecedor.java` |
| Serviço consumidor do iterador | `domain/fornecedor/service/FornecedorServiceImpl.java` |

**Como aparece no código:** `ContratosAtivosFornecedor.iterator()` retorna uma instância de `IteradorContratosAtivos` que carrega os contratos do repositório uma única vez e avança pulando automaticamente os registros com status `ENCERRADO` ou `CANCELADO`. `FornecedorServiceImpl.desativarFornecedor()` usa apenas `possuiAtivos()` — que delega internamente a `hasNext()` — para bloquear a desativação quando há contratos vigentes, sem tocar a lista diretamente.

---

### Interpreter — Samuel Abreu

**Intenção:** permitir que o serviço de locais filtre registros por meio de uma expressão textual arbitrária (ex.: `status = ATIVO AND capacidade_min = 50`) sem que a lógica de filtragem fique acoplada ao serviço — cada regra é encapsulada em um objeto separado e as combinações AND/OR são compostas em árvore.

| Papel no padrão | Arquivo .java |
|---|---|
| Interface AbstractExpression | `domain/local/interpreter/ExpressaoLocal.java` |
| Expressões terminais | `domain/local/interpreter/ExpressaoStatus.java` · `ExpressaoTipo.java` · `ExpressaoCapacidadeMinima.java` · `ExpressaoCapacidadeMaxima.java` |
| Expressões não-terminais (compostas) | `domain/local/interpreter/ExpressaoAnd.java` · `ExpressaoOr.java` |
| Parser (constrói a árvore de expressões) | `domain/local/interpreter/AnalisadorExpressaoLocal.java` |
| Cliente (usa a árvore) | `domain/local/service/LocalServiceImpl.java` |

**Como aparece no código:** `AnalisadorExpressaoLocal.parse(expressao)` tokeniza a string de filtro e monta recursivamente a árvore de `ExpressaoLocal`, respeitando precedência (AND > OR). `LocalServiceImpl.filtrarLocais()` chama `parse()` uma única vez e aplica `filtro::interpretar` a cada `Local` retornado pelo repositório, sem conhecer os campos concretos que estão sendo avaliados.

---

### Template Method — Maria Júlia

**Intenção:** definir o esqueleto de um algoritmo para geração de relatórios financeiros (validação, coleta de orçamento/despesas, cálculos de totais, indicadores de saúde/cobertura e recomendações), permitindo que subclasses personalizem apenas as etapas variáveis (montagem de itens e formatação do conteúdo) sem alterar a estrutura geral.

| Papel no padrão                  | Arquivo .java |
|----------------------------------|---------------|
| Classe Abstrata (Template)       | `domain/financeiro/template/GeradorRelatorioFinanceiroTemplateMethod.java` |
| Classe Concreta (Padrão)         | `domain/financeiro/template/GeradorRelatorioPadraoTemplateMethod.java` |
| Classe Concreta (What-If)        | `domain/financeiro/template/GeradorRelatorioWhatIfTemplateMethod.java` |
| Cliente                          | `domain/financeiro/service/RelatorioFinanceiroServiceImpl.java` |

**Como aparece no código:** O método `executar(String eventoId, String usuarioId)` é `final` na classe abstrata e orquestra todo o fluxo fixo: valida evento, obtém orçamento/categorias, monta itens, calcula totais, indicadores, comparativo e recomendações, chamando por fim o método abstrato `construirConteudo(...)`.

- `GeradorRelatorioPadraoTemplateMethod` sobrescreve apenas `construirConteudo` para gerar relatório textual completo.
- `GeradorRelatorioWhatIfTemplateMethod` sobrescreve `montarItens`, `montarIndicadorCobertura` e `construirConteudo` para suportar simulações com parâmetros (despesas pendentes/hipotéticas e cenários pessimistas).

O `RelatorioFinanceiroServiceImpl` instancia o gerador adequado (padrão ou what-if) e sempre chama `executar(...)`, sem conhecer os detalhes internos de cada variação.

O mesmo padrão é aplicado em outros componentes do pacote `domain/financeiro/template` (processadores e validadores de despesa).

Aqui está o conteúdo formatado no mesmo estilo:

---

### Interpreter — Maria Luíza Dantas

**Intenção:** definir uma linguagem simples para filtragem de funcionários e membros de equipe, permitindo interpretar expressões textuais compostas por regras de negócio (cargo, disponibilidade, liderança e status), de forma que novas condições possam ser adicionadas sem alterar a lógica principal dos serviços.

| Papel no padrão | Arquivo .java |
|---|---|
| Interface Abstrata (Expression) | `domain/funcionario/interpreter/ExpressaoFuncionario.java` |
| Expressão Terminal | `domain/funcionario/interpreter/ExpressaoCargoFuncionario.java` |
| Expressão Terminal | `domain/funcionario/interpreter/ExpressaoDisponibilidadeFuncionario.java` |
| Expressão Terminal | `domain/funcionario/interpreter/ExpressaoAtivoFuncionario.java` |
| Expressão Não-Terminal | `domain/funcionario/interpreter/ExpressaoAndFuncionario.java` |
| Expressão Não-Terminal | `domain/funcionario/interpreter/ExpressaoOrFuncionario.java` |
| Parser | `domain/funcionario/interpreter/AnalisadorExpressaoFuncionario.java` |
| Cliente | `domain/funcionario/service/FuncionarioServiceImpl.java` |
| Interface Abstrata (Expression) | `domain/equipe/interpreter/ExpressaoMembro.java` |
| Expressão Terminal | `domain/equipe/interpreter/ExpressaoLider.java` |
| Expressão Terminal | `domain/equipe/interpreter/ExpressaoCargo.java` |
| Expressão Terminal | `domain/equipe/interpreter/ExpressaoDisponibilidade.java` |
| Expressão Não-Terminal | `domain/equipe/interpreter/ExpressaoAnd.java` |
| Expressão Não-Terminal | `domain/equipe/interpreter/ExpressaoOr.java` |
| Parser | `domain/equipe/interpreter/AnalisadorExpressaoMembro.java` |
| Cliente | `domain/equipe/service/EquipeServiceImpl.java` |

**Como aparece no código:** O método `parse(String expressao)` é responsável por transformar uma expressão textual em uma árvore de objetos do padrão Interpreter. O analisador realiza a tokenização da entrada, identifica expressões terminais e composições lógicas utilizando os operadores AND e OR, construindo a estrutura que será interpretada posteriormente.

- `AnalisadorExpressaoFuncionario` cria expressões para filtragem de funcionários com base em cargo, disponibilidade e status de atividade.
- `ExpressaoCargoFuncionario`, `ExpressaoDisponibilidadeFuncionario` e `ExpressaoAtivoFuncionario` representam as regras terminais da linguagem.
- `ExpressaoAndFuncionario` e `ExpressaoOrFuncionario` permitem combinar múltiplas regras em expressões compostas.

O `FuncionarioServiceImpl` interpreta a expressão gerada pelo analisador e aplica o filtro sobre a coleção de funcionários, sem conhecer os detalhes internos de cada regra.

O mesmo padrão é aplicado no pacote `domain/equipe/interpreter`, onde expressões relacionadas a liderança, cargo e disponibilidade são utilizadas para filtrar membros de uma equipe por meio do `EquipeServiceImpl`.

## 11. Autores
Projeto desenvolvido no contexto da disciplina Requisitos, Projeto de Software e Validação – CESAR School.

**Equipe EventOS:**
- Artur Dowsley
- Felipe Barros
- Maria Luíza Dantas
- Henrique Gueiros
- Lucas Calabria
- Maria Júlia Dantas
- Samuel Lucas
- Victor Paes
- Lucca D'Angelo