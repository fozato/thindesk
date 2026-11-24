# Thindesk

Sistema web de controle de chamados técnicos (help desk), desenvolvido em Java com Spring Boot e MongoDB.

![Versão](https://img.shields.io/badge/vers%C3%A3o-1.0.0-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green)

## Acesso online

- **Aplicação:** _link do deploy será adicionado aqui_
- **Como entrar:** acesse o link, clique em **Criar conta**, cadastre um usuário e faça login.

> A hospedagem gratuita pode "dormir" quando fica sem uso. O primeiro acesso pode levar até 1 minuto.

## Dashboard

_O print do dashboard será adicionado aqui (`docs/dashboard.png`)._

## Funcionalidades

- Login com usuários cadastrados no MongoDB (senhas armazenadas com hash BCrypt)
- Cadastro de novos usuários
- Proteção contra CSRF em todos os formulários
- Listagem de chamados, clientes e horários de atendimento por setor

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4 (Web, Security, Data MongoDB) |
| Interface | Thymeleaf + Bootstrap 5 |
| Banco de dados | MongoDB Atlas |
| Build | Maven (wrapper incluso) |

## Como rodar localmente

### Pré-requisitos

- JDK 21
- Um banco MongoDB (por exemplo, um cluster gratuito no [MongoDB Atlas](https://www.mongodb.com/cloud/atlas))

### Configuração

As credenciais **não** ficam no código. A aplicação lê as variáveis de ambiente abaixo:

| Variável | Obrigatória | Descrição |
|---|---|---|
| `MONGODB_URI` | Sim | URI de conexão do MongoDB |
| `MONGODB_DATABASE` | Não | Nome do banco (padrão: `thindesk`) |
| `PORT` | Não | Porta HTTP (padrão: `8080`) |

Para desenvolvimento, copie o arquivo de exemplo e preencha com seus dados (o `.env` é ignorado pelo Git):

```bash
cp .env.example .env
```

### Execução

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Acesse `http://localhost:8080`.

### Gerar o pacote

```bash
./mvnw clean package
java -jar target/thindesk-1.0.0.jar
```

## Estrutura do projeto

```
src/main/java/com/pfc
├── ThindeskApplication.java      # Classe principal
├── SecurityConfig.java           # Configuração de segurança (login, CSRF, BCrypt)
├── MongoInitConfig.java          # Criação das coleções
└── thindesk
    ├── controller                # Rotas web e API
    ├── entity                    # Documentos do MongoDB
    ├── repository                # Repositórios Spring Data
    └── service                   # Regras de negócio
src/main/resources
├── application.properties
├── static                        # CSS, JS e imagens
└── templates                     # Páginas Thymeleaf
```

## Versionamento

Este projeto segue o [Versionamento Semântico (SemVer)](https://semver.org/lang/pt-BR/), no formato `MAJOR.MINOR.PATCH`:

- **MAJOR**: mudanças incompatíveis com versões anteriores
- **MINOR**: novas funcionalidades compatíveis
- **PATCH**: correções de bugs compatíveis

Cada versão publicada recebe uma tag no Git (ex.: `v1.0.0`).

## Changelog

Todas as mudanças relevantes são registradas aqui, no formato [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/).

### [1.0.0] - em desenvolvimento

#### Adicionado
- Cadastro de usuários e login com usuários armazenados no MongoDB, com senha em BCrypt
- Botão de logout
- Mensagens de erro, logout e cadastro na tela de login
- Configuração por variáveis de ambiente (`MONGODB_URI`, `MONGODB_DATABASE`, `PORT`) e arquivo `.env.example`

#### Alterado
- Empacotamento de `war` para `jar` executável
- Logout passou a ser feito via POST

#### Removido
- Credenciais do banco e do usuário fixas no código
- Dependências Java EE não utilizadas

#### Segurança
- Proteção CSRF reativada

## Autor

Victor Fozato — [@fozato](https://github.com/fozato)
