# J-Blog Spring API

API REST para gerenciamento de usuários e postagens de um blog pessoal, construída com Spring Boot 3, Java 21 e autenticação JWT.

## Visão geral

A aplicação oferece:

- cadastro de usuários
- autenticação com JWT
- gerenciamento de postagens por usuário autenticado
- busca de postagens por filtros
- documentação automática via Swagger/OpenAPI
- tratamento global de erros e respostas padronizadas

Versão atual: `0.3.0`

## Tecnologias

- Java 21
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- JJWT 0.11.5
- PostgreSQL
- Springdoc OpenAPI 2.2.0
- Maven Wrapper
- Actuator + Micrometer/OpenTelemetry

## Pré-requisitos

- JDK 21 instalado e configurado no `JAVA_HOME`
- PostgreSQL em execução localmente
- Banco de dados `blog-db` criado
- Maven opcional: o projeto já inclui `mvnw` e `mvnw.cmd`

## Configuração local

1. Clone o repositório:

   ```powershell
   git clone https://github.com/Brunos-Luna/j-blog-spring.git
   Set-Location j-blog-spring
   ```

2. Crie o banco de dados no PostgreSQL:

   ```sql
   CREATE DATABASE "blog-db";
   ```

3. Ajuste as variáveis de conexão e JWT no arquivo `src/main/resources/application.properties`:

   ```properties
   server.port=8085

   spring.datasource.url=jdbc:postgresql://localhost:5432/blog-db
   spring.datasource.username=postgres
   spring.datasource.password=root
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

   jwt.secret=uma-string-bem-grande-e-segura-para-chave-jwt-2026
   ```

Observações:

- `ddl-auto=update` é adequado apenas para desenvolvimento.
- Em produção, prefira usar variáveis de ambiente ou um secret manager.
- O segredo JWT deve ser longo, aleatório e protegido fora do controle de versão.

## Execução

Windows PowerShell:

```powershell
./mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

A aplicação ficará disponível em:

- API: `http://localhost:8085`
- Swagger UI: `http://localhost:8085/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8085/v3/api-docs`

## Segurança e autenticação

A API usa JWT para autenticar solicitações protegidas.

### Registro público

- `POST /user/register`

### Login público

- `POST /user/login`

### Endpoints protegidos

Todos os endpoints de `/post` exigem token JWT válido no cabeçalho:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Regras da aplicação:

- o `username` é usado como subject do token
- as senhas são armazenadas com BCrypt
- a autenticação é stateless
- CSRF está desabilitado para a API REST
- CORS está liberalmente configurado para qualquer origem

## Endpoints

### Usuários

#### POST `/user/register`

Cria um novo usuário.

Request body:

```json
{
  "username": "bruno",
  "password": "123456"
}
```

Resposta de sucesso (`201 Created`):

```json
{
  "status": 201,
  "message": "User created successfully",
  "user": {
    "userId": "UUID",
    "username": "bruno",
    "createdAt": "2026-10-24T12:34:56"
  }
}
```

Conflito (`409 Conflict`): usuário já existente.

#### POST `/user/login`

Autentica o usuário e retorna um token JWT.

Request body:

```json
{
  "username": "bruno",
  "password": "123456"
}
```

Resposta de sucesso (`200 OK`):

```json
{
  "status": 200,
  "message": "Authentication successful",
  "Bearer": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Credenciais inválidas retornam `401 Unauthorized`.

### Posts

Todos os endpoints abaixo exigem autenticação.

#### GET `/post/me/posts`

Lista as postagens do usuário autenticado.

- `200 OK`: retorna a lista de posts
- `204 No Content`: usuário sem postagens

#### GET `/post/by-filters`

Busca posts por filtros opcionais.

Query params:

- `title` (opcional)
- `body` (opcional)
- `username` (opcional)

Exemplo:

```http
GET /post/by-filters?title=primeiro&username=bruno
```

#### POST `/post`

Cria uma nova postagem para o usuário autenticado.

Request body:

```json
{
  "title": "Meu primeiro post",
  "body": "Conteúdo da postagem"
}
```

Resposta de sucesso (`201 Created`):

```json
{
  "status": 201,
  "message": "Post created successfully",
  "post": {
    "postId": "UUID",
    "title": "Meu primeiro post",
    "body": "Conteúdo da postagem",
    "createdAt": "2026-10-24T12:34:56",
    "updatedAt": "2026-10-24T12:34:56"
  }
}
```

#### PUT `/post/{postId}`

Atualiza uma postagem do usuário autenticado.

Request body:

```json
{
  "title": "Título atualizado",
  "body": "Conteúdo atualizado"
}
```

Possíveis respostas:

- `200 OK`: postagem atualizada
- `403 Forbidden`: postagem de outro usuário
- `404 Not Found`: post não encontrado

#### DELETE `/post/{postId}`

Remove uma postagem do usuário autenticado.

Possíveis respostas:

- `200 OK`: postagem removida
- `403 Forbidden`: postagem de outro usuário
- `404 Not Found`: post inexistente

## Formato padrão das respostas

A API padroniza as respostas em um objeto com `status` e `message`, e, quando necessário, adiciona o payload em um campo específico.

Exemplo:

```json
{
  "status": 200,
  "message": "Authentication successful",
  "Bearer": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## Códigos de erro

- `400 Bad Request`: dados inválidos
- `401 Unauthorized`: credenciais inválidas ou autenticação ausente
- `403 Forbidden`: acesso negado
- `404 Not Found`: recurso inexistente
- `409 Conflict`: conflito de dados (ex.: username já em uso)

Exemplo de erro:

```json
{
  "status": 401,
  "message": "Authentication required"
}
```

## Testes e build

Executar testes:

```powershell
./mvnw.cmd clean test
```

Gerar pacote JAR:

```powershell
./mvnw.cmd clean package -DskipTests
```

## Estrutura do projeto

```text
src/
├── main/
│   ├── java/
│   │   └── br/com/blog/
│   │       ├── api/
│   │       ├── config/
│   │       │   ├── exceptions/
│   │       │   └── security/
│   │       ├── controllers/
│   │       ├── dto/
│   │       ├── models/
│   │       ├── repositories/
│   │       ├── services/
│   │       └── BlogApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/
```

## Observações para produção

- remover credenciais e segredos do `application.properties`
- externalizar configuração com variáveis de ambiente ou secret manager
- usar uma chave JWT forte e segura
- restringir CORS para origens confiáveis
- substituir `ddl-auto=update` por migração de banco (Flyway/Liquibase)
- ampliar a cobertura de testes para autenticação, autorização e casos de erro

## Autor

[Brunos Luna](https://github.com/Brunos-Luna)
