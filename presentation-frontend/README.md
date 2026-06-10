# PlanEvent - Frontend

Interface web do EventOS, construída com React, TypeScript e Vite.

## Pré-requisitos

- Node.js 20+
- npm 10+

## Desenvolvimento local

```bash
cd presentation-frontend
npm install
npm run dev
```

A aplicação estará disponível em `http://localhost:8080`. Requisições para `/api` são encaminhadas ao backend em `http://localhost:3000`.

## Build

```bash
npm run build
```

O build gera os arquivos estáticos em `target/classes/static`, prontos para empacotamento no JAR Maven.

## Integração com Maven

O módulo usa o `frontend-maven-plugin` para instalar dependências e executar o build automaticamente:

```bash
mvn package -pl presentation-frontend
```

Os assets são incluídos no JAR e servidos pelo Spring Boot em `classpath:/static/`.
