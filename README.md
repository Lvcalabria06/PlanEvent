# PlanEvent - Sistema de Gestão de Eventos

## 1. Descrição do Domínio
O **PlanEvent** é um sistema voltado para o gerenciamento inteligente e completo de eventos.

O PlanEvent é um sistema inteligente de gestão de eventos que auxilia gestores a planejar, organizar e executar eventos de forma estruturada, garantindo controle operacional, financeiro, logístico e administrativo.

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
`entregaveis/planevent.cml`

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

## 9. Autores
Projeto desenvolvido no contexto da disciplina Requisitos, Projeto de Software e Validação – CESAR School.

**Equipe PlanEvent:**
- Artur Dowsley
- Felipe Barros
- Maria Luíza Dantas
- Henrique Gueiros
- Lucas Calabria
- Maria Júlia Dantas
- Samuel Lucas
- Victor Paes
- Lucca D'Angelo
