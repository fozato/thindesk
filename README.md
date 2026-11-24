# Thindesk

Sistema web de controle de chamados técnicos (help desk): a equipe de suporte abre, acompanha e conclui chamados, cadastra clientes e define os horários de atendimento de cada setor. Feito em Java com Spring Boot e MongoDB.

![Versão](https://img.shields.io/badge/vers%C3%A3o-1.0.1-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-green)

## Acesso online

- **Aplicação:** https://thindesk-gldv.onrender.com
- **Como entrar:** acesse o link, clique em **Criar conta**, cadastre um usuário e faça login.

> A hospedagem gratuita "dorme" quando fica sem uso. O primeiro acesso pode levar até 1 minuto.

![Dashboard do Thindesk](docs/dashboard.png)

## Índice

- [Funcionalidades](#funcionalidades)
- [Tecnologias](#tecnologias)
- [Início rápido](#início-rápido)
- [Configuração](#configuração)
- [Testes automatizados](#testes-automatizados)
- [API REST](#api-rest)
- [Deploy](#deploy)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Limitações conhecidas](#limitações-conhecidas)
- [Versionamento](#versionamento)
- [Changelog](#changelog)
- [Autor](#autor)

## Funcionalidades

- **Dashboard** com o total de chamados, quantos estão abertos, em andamento e concluídos (com a proporção de cada status), total de clientes e de horários, e os últimos chamados abertos
- **Chamados**: abrir, editar (descrição, status, tipo, técnico e solicitante) e excluir
- **Clientes**: cadastrar, editar e excluir
- **Horários de atendimento** por setor e dia da semana, com validação (o fim precisa ser depois do início)
- **Contas de usuário**: cadastro pela tela "Criar conta" e login com usuários guardados no MongoDB, com senha em hash BCrypt
- Proteção contra CSRF em todos os formulários; logout e exclusões feitos via POST
- Layout adaptado para celular

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4 (Web, Security, Data MongoDB) |
| Interface | Thymeleaf, Bootstrap 5.3, Bootstrap Icons e fonte Inter |
| Banco de dados | MongoDB Atlas |
| Testes | JUnit 5, Mockito e AssertJ |
| Build | Maven (wrapper incluso, não precisa instalar o Maven) |
| Deploy | Docker no Render (plano gratuito) |

## Início rápido

**Pré-requisitos:** JDK 21, Git e um banco MongoDB (por exemplo, um cluster gratuito no [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)). Testado no Windows e no Linux (contêiner do deploy).

1. Clone o repositório:

   ```bash
   git clone https://github.com/fozato/thindesk.git
   cd thindesk
   ```

2. Crie o arquivo `.env` a partir do modelo e preencha a `MONGODB_URI` com a URI do seu banco:

   ```bash
   # Linux / macOS
   cp .env.example .env

   # Windows
   copy .env.example .env
   ```

3. Rode a aplicação:

   ```bash
   # Linux / macOS
   ./mvnw spring-boot:run

   # Windows
   mvnw.cmd spring-boot:run
   ```

4. Acesse `http://localhost:8080`, clique em **Criar conta** e faça login.

As coleções do MongoDB (`chamados`, `clientes`, `usuarios` e `horariosAtendimento`) são criadas automaticamente na primeira execução.

## Configuração

As credenciais **não** ficam no código. A aplicação lê as variáveis abaixo do ambiente ou do arquivo `.env` na pasta do projeto, que é carregado automaticamente e ignorado pelo Git:

| Variável | Obrigatória | Descrição |
|---|---|---|
| `MONGODB_URI` | Sim | URI de conexão do MongoDB |
| `MONGODB_DATABASE` | Não | Nome do banco (padrão: `thindesk`; no desenvolvimento, use `thindesk_dev` para não misturar com o app publicado) |
| `PORT` | Não | Porta HTTP (padrão: `8080`) |
| `THYMELEAF_CACHE` | Não | Cache de templates (padrão: `false`; use `true` em produção) |

### Gerar o pacote

```bash
./mvnw clean package
java -jar target/thindesk-1.0.1.jar
```

O `package` roda os testes automatizados antes de gerar o `.jar`. Para pular os testes, use `./mvnw clean package -DskipTests`.

## Testes automatizados

Os testes não precisam de banco de dados nem de internet:

```bash
./mvnw test
```

Resultado esperado:

```
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Eles cobrem:

- **Cadastro e login de usuários** (`UsuarioServiceTest`): validação do nome, tamanho e confirmação da senha, nome repetido, senha gravada em BCrypt e login sem diferenciar maiúsculas
- **Status dos chamados** (`StatusChamadoTest`): variações de escrita, nomes e cores de cada status
- **Formulários** (`FormUtilsTest`): limpeza dos campos de texto

## API REST

Além das telas, há endpoints JSON. Eles exigem login (sessão do navegador):

| Método | Rota | O que faz |
|---|---|---|
| `GET` | `/api/chamados` | Lista os chamados |
| `POST` | `/api/chamados` | Cria um chamado |
| `PUT` | `/api/chamados/{id}` | Atualiza um chamado |
| `GET` | `/api/clientes` | Lista os clientes |
| `POST` | `/api/clientes` | Cria um cliente |

Exemplo de resposta de `GET /api/chamados`:

```json
[
  {
    "id": "6650f1c2a1b2c3d4e5f60718",
    "descricao": "Impressora do financeiro não imprime frente e verso",
    "status": "ABERTO",
    "tipo": "Hardware",
    "tecnico": "João Lima",
    "usuario": "Mariana Souza"
  }
]
```

Os valores de `status` são `ABERTO`, `EM_ANDAMENTO` e `CONCLUIDO`. Veja em [Limitações conhecidas](#limitações-conhecidas) por que o `POST` e o `PUT` não funcionam fora das telas.

## Deploy

A aplicação publicada roda no [Render](https://render.com) (plano gratuito), em um contêiner Docker:

1. O `Dockerfile` compila o projeto com Maven e gera uma imagem enxuta, só com o JRE 21 e o `.jar`.
2. O `render.yaml` (Blueprint) descreve o serviço web: plano gratuito, verificação de saúde em `/login`, banco `thindesk` e cache de templates ligado.
3. A variável `MONGODB_URI` é informada só no painel do Render, nunca no repositório.
4. No MongoDB Atlas, em **Network Access**, o acesso é liberado para `0.0.0.0/0`, já que o Render não tem IP fixo no plano gratuito.

A cada `push` na branch `main`, o Render gera uma nova imagem e publica a versão atualizada automaticamente.

## Estrutura do projeto

```
├── Dockerfile                    # Build e imagem de produção
├── render.yaml                   # Configuração do deploy no Render
├── .env.example                  # Modelo das variáveis de ambiente
├── docs/dashboard.png            # Print usado neste README
└── pom.xml                       # Dependências e build (Maven)

src/main/java/com/pfc
├── ThindeskApplication.java      # Classe principal
├── SecurityConfig.java           # Configuração de segurança (login, CSRF, BCrypt)
├── MongoInitConfig.java          # Criação das coleções
└── thindesk
    ├── controller                # Telas (web) e API REST
    ├── entity                    # Documentos do MongoDB
    ├── repository                # Repositórios Spring Data
    └── service                   # Regras de negócio
src/main/resources
├── application.properties        # Configuração (lê as variáveis de ambiente)
├── static                        # CSS, JS e imagens
└── templates                     # Páginas Thymeleaf
src/test/java/com/pfc/thindesk    # Testes automatizados
```

## Limitações conhecidas

- **Sem perfis de acesso:** todo usuário cadastrado vê, edita e exclui todos os chamados, clientes e horários.
- **API só para leitura na prática:** como o CSRF está ativo, o `POST` e o `PUT` da API são recusados (403) quando chamados fora das telas do sistema. Os `GET` funcionam com a sessão de um usuário logado.
- **Sem recuperação de senha:** quem esquecer a senha precisa criar outra conta.
- **Hospedagem gratuita:** o Render coloca o app para "dormir" quando fica sem uso, e o primeiro acesso depois disso é mais lento.

## Versionamento

Este projeto segue o [Versionamento Semântico (SemVer)](https://semver.org/lang/pt-BR/), no formato `MAJOR.MINOR.PATCH`:

- **MAJOR**: mudanças incompatíveis com versões anteriores
- **MINOR**: novas funcionalidades compatíveis
- **PATCH**: correções de bugs compatíveis

Cada versão publicada recebe uma tag no Git e um [Release no GitHub](https://github.com/fozato/thindesk/releases) (ex.: `v1.0.0`).

## Changelog

Todas as mudanças relevantes são registradas aqui, no formato [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/).

### [1.0.1] - 2026-11-24

#### Adicionado
- Testes automatizados com JUnit 5, Mockito e AssertJ (`./mvnw test`)
- README com índice, início rápido, exemplos da API REST e limitações conhecidas

#### Alterado
- `Dockerfile` passou a copiar o `.jar` sem depender do número da versão

### [1.0.0] - 2026-11-24

#### Adicionado
- Deploy online no Render com Docker (`Dockerfile` e `render.yaml`)
- Dashboard com indicadores, distribuição dos chamados por status e lista dos últimos chamados
- Criação, edição e exclusão de chamados e clientes
- Exclusão de horários de atendimento e validação de dia/horário
- Mensagens de sucesso e erro nas telas
- Cadastro de usuários e login com usuários armazenados no MongoDB, com senha em BCrypt
- Botão de logout e nome do usuário logado no menu
- Mensagens de erro, logout e cadastro na tela de login
- Configuração por variáveis de ambiente (`MONGODB_URI`, `MONGODB_DATABASE`, `PORT`, `THYMELEAF_CACHE`) e arquivo `.env.example`

#### Alterado
- Empacotamento de `war` para `jar` executável
- Logout passou a ser feito via POST
- Exclusões passaram a ser feitas via POST, com confirmação
- Menu lateral e layout padronizados em todas as páginas
- Nova interface com as cores do logo: menu com destaque da página atual, status em cores, telas de login e cadastro redesenhadas e layout adaptado para celular

#### Corrigido
- Página de erro substituída por uma página própria com mensagens para 403, 404 e demais erros
- Tela de horários de atendimento, que não salvava nem listava os registros
- Botões de novo/editar/excluir que levavam a páginas inexistentes
- Erro ao receber parâmetros nas rotas (compilação com `-parameters`)

#### Removido
- Credenciais do banco e do usuário fixas no código
- Dependências Java EE não utilizadas
- Itens do menu sem funcionalidade (Técnicos, Tipo de Chamado, Recursos)
- Arquivos de configuração do NetBeans e dados de exemplo não utilizados

#### Segurança
- Proteção CSRF reativada

## Autor

Victor Fozato — [@fozato](https://github.com/fozato)
