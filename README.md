# J-Blog Spring API ðŸš€

API REST para gerenciamento de usuÃ¡rios e posts, desenvolvida com Spring Boot. A aplicaÃ§Ã£o oferece cadastro de usuÃ¡rios, autenticaÃ§Ã£o via JWT e gerenciamento autenticado dos prÃ³prios posts.

## Tecnologias

- Java 21
- Spring Boot 3.3.4
- Spring Web
- Spring Data JPA e Hibernate
- Spring Validation
- Spring Security
- JJWT 0.11.5
- PostgreSQL
- Springdoc OpenAPI 2.2.0 (Swagger UI)
- Maven Wrapper
- Spring Boot DevTools
- Spring Boot Test

VersÃ£o atual da aplicaÃ§Ã£o: `0.2.0`.

## PrÃ©-requisitos

- JDK 21 configurado em `JAVA_HOME`.
- PostgreSQL em execuÃ§Ã£o na porta `5432`.
- Maven Ã© opcional, pois o projeto inclui `mvnw` e `mvnw.cmd`.

## ConfiguraÃ§Ã£o local

1. Clone o repositÃ³rio e entre na pasta:

   ```powershell
   git clone https://github.com/Bruno-Luna/j-blog-spring.git
   Set-Location j-blog-spring
   ```

2. Crie o banco de dados:

   ```sql
   CREATE DATABASE "blog-db";
   ```

3. Revise `src/main/resources/application.properties`:

   ```properties
   server.port=8085
   spring.datasource.url=jdbc:postgresql://localhost:5432/blog-db
   spring.datasource.username=postgres
   spring.datasource.password=ALTERE_ESTA_SENHA
   spring.jpa.hibernate.ddl-auto=update
   jwt.secret=ALTERE_ESTE_SEGREDO
   ```

   O projeto usa `ddl-auto=update`, uma configuraÃ§Ã£o adequada apenas para desenvolvimento. Em ambientes reais, forneÃ§a as credenciais do banco e o segredo JWT por variÃ¡veis de ambiente ou configuraÃ§Ã£o externa. O segredo deve ser longo, aleatÃ³rio e mantido fora do controle de versÃ£o.

## ExecuÃ§Ã£o

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A API estarÃ¡ disponÃ­vel em `http://localhost:8085`.

A documentaÃ§Ã£o OpenAPI pode ser acessada em:

- Swagger UI: `http://localhost:8085/swagger-ui.html`
- EspecificaÃ§Ã£o OpenAPI: `http://localhost:8085/v3/api-docs`

## AutenticaÃ§Ã£o

Os endpoints de registro e login sÃ£o pÃºblicos. Os endpoints de posts exigem um token JWT no cabeÃ§alho:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

CaracterÃ­sticas da autenticaÃ§Ã£o:

- O username Ã© usado como subject do token.
- O token expira apÃ³s 30 minutos.
- As senhas sÃ£o armazenadas usando BCrypt.
- As sessÃµes sÃ£o stateless.
- CSRF estÃ¡ desabilitado para a API.
- O CORS estÃ¡ aberto para qualquer origem na configuraÃ§Ã£o atual.

## Endpoints

### UsuÃ¡rios

#### `POST /user/register`

Cria um usuÃ¡rio.

Corpo da requisiÃ§Ã£o:

```json
{
  "username": "seu-nome",
  "password": "sua-senha"
}
```

Respostas principais:

- `201 Created`: usuÃ¡rio criado, com `user` na raiz da resposta.
- `409 Conflict`: username jÃ¡ utilizado.

Exemplo de resposta:

```json
{
  "status": 201,
  "message": "User created with success",
  "user": {
    "userId": "UUID_DO_USUARIO",
    "username": "seu-nome",
    "createdAt": "09/10/2026 14:30:00"
  }
}
```

#### `POST /user/login`

Autentica um usuÃ¡rio.

Corpo da requisiÃ§Ã£o:

```json
{
  "username": "seu-nome",
  "password": "sua-senha"
}
```

Resposta de sucesso:

```json
{
  "status": 200,
  "message": "Login success",
  "token": "SEU_TOKEN_JWT"
}
```

Credenciais invÃ¡lidas retornam `401 Unauthorized`.

### Posts

Todos os endpoints desta seÃ§Ã£o exigem autenticaÃ§Ã£o JWT.

#### `GET /post/me/posts`

Lista os posts pertencentes ao usuÃ¡rio autenticado.

- `200 OK`: retorna diretamente um array de posts.
- `204 No Content`: o usuÃ¡rio nÃ£o possui posts.

Cada post contÃ©m `postId`, `title`, `body` e `updatedAt`. A data Ã© formatada como `dd/MM/yyyy HH:mm:ss`.

#### `POST /post`

Cria um post para o usuÃ¡rio autenticado.

Corpo da requisiÃ§Ã£o:

```json
{
  "title": "Meu primeiro post",
  "body": "ConteÃºdo do post"
}
```

Regras:

- `title` Ã© obrigatÃ³rio e aceita atÃ© 100 caracteres.
- `body` Ã© obrigatÃ³rio.
- `postId`, usuÃ¡rio e data sÃ£o definidos pelo servidor.

Resposta de sucesso: `201 Created`, com o post no campo `post` da raiz da resposta.

Exemplo no PowerShell:

```powershell
curl.exe -X POST http://localhost:8085/post `
  -H "Authorization: Bearer SEU_TOKEN_JWT" `
  -H "Content-Type: application/json" `
  -d '{"title":"Meu primeiro post","body":"ConteÃºdo do post"}'
```

#### `PUT /post/{postId}`

Edita um post do usuÃ¡rio autenticado. O UUID deve ser informado na URL, e nÃ£o no corpo.

Exemplo de URL:

```text
PUT /post/UUID_DO_POST
```

Corpo da requisiÃ§Ã£o:

```json
{
  "title": "TÃ­tulo atualizado",
  "body": "ConteÃºdo atualizado"
}
```

Respostas principais:

- `200 OK`: post atualizado.
- `403 Forbidden`: o post pertence a outro usuÃ¡rio.
- `404 Not Found`: post ou usuÃ¡rio nÃ£o encontrado.

#### `DELETE /post/{postId}`

Exclui um post do usuÃ¡rio autenticado. O UUID deve ser informado na URL e a requisiÃ§Ã£o nÃ£o precisa de corpo.

Exemplo:

```text
DELETE /post/UUID_DO_POST
```

Respostas principais:

- `200 OK`: post excluÃ­do.
- `403 Forbidden`: o post pertence a outro usuÃ¡rio.
- `404 Not Found`: post inexistente.

## Formato das respostas

As respostas de operaÃ§Ãµes usam `status` e `message`. Quando aplicÃ¡vel, os dados sÃ£o adicionados diretamente na raiz, sem um campo `data` intermediÃ¡rio.

Exemplo de criaÃ§Ã£o ou ediÃ§Ã£o de post:

```json
{
  "status": 201,
  "message": "Post created with success",
  "post": {
    "postId": "UUID_DO_POST",
    "title": "Meu primeiro post",
    "body": "ConteÃºdo do post",
    "updatedAt": "09/10/2026 14:30:00"
  }
}
```

O endpoint `GET /post/me/posts` retorna diretamente um array JSON. O login retorna o token no campo `token`, e o cadastro retorna o usuÃ¡rio no campo `user`.

## CÃ³digos de erro

- `400 Bad Request`: dados invÃ¡lidos ou argumentos incorretos.
- `401 Unauthorized`: credenciais invÃ¡lidas ou autenticaÃ§Ã£o ausente.
- `403 Forbidden`: usuÃ¡rio nÃ£o Ã© proprietÃ¡rio do post.
- `404 Not Found`: usuÃ¡rio ou post nÃ£o encontrado.
- `409 Conflict`: username jÃ¡ existente.

Os erros normalmente seguem este formato:

```json
{
  "status": 403,
  "message": "Acesso negado"
}
```

## Testes e build

Executar os testes:

```powershell
.\mvnw.cmd clean test
```

O projeto possui um teste de carregamento do contexto Spring.

Gerar o artefato sem executar os testes:

```powershell
.\mvnw.cmd clean package -DskipTests
```

## Estrutura do projeto

Em `src/main/java/br/com/blog`:

- `api/`: respostas padronizadas e configuraÃ§Ã£o OpenAPI.
- `config/security/`: configuraÃ§Ã£o do Spring Security, filtro JWT e carregamento de usuÃ¡rios.
- `config/exceptions/`: tratamento global de exceÃ§Ãµes.
- `controllers/`: endpoints REST de usuÃ¡rios e posts.
- `dto/`: objetos de requisiÃ§Ã£o e resposta.
- `models/`: entidades JPA de usuÃ¡rios e posts.
- `repositories/`: repositÃ³rios Spring Data JPA.
- `services/`: regras de negÃ³cio e geraÃ§Ã£o/validaÃ§Ã£o de JWT.
- `BlogApplication`: classe principal e configuraÃ§Ã£o do log de requisiÃ§Ãµes.

## ConsideraÃ§Ãµes para produÃ§Ã£o

- Remover credenciais do arquivo `application.properties` e usar configuraÃ§Ã£o externa.
- Usar um segredo JWT longo, aleatÃ³rio e protegido.
- Restringir o CORS a origens confiÃ¡veis.
- Substituir `ddl-auto=update` por uma estratÃ©gia de migraÃ§Ã£o de banco.
- Avaliar a exposiÃ§Ã£o de payloads, query strings e informaÃ§Ãµes do cliente nos logs de requisiÃ§Ã£o.
- Adicionar testes para autenticaÃ§Ã£o, autorizaÃ§Ã£o, validaÃ§Ã£o e operaÃ§Ãµes de posts.
- Revisar a nomenclatura do esquema de seguranÃ§a exibido no OpenAPI para garantir que o botÃ£o de autenticaÃ§Ã£o do Swagger corresponda Ã s anotaÃ§Ãµes dos controllers.

## Autor

[Bruno Luna](https://github.com/Bruno-Luna) Â· [RepositÃ³rio no GitHub](https://github.com/Bruno-Luna/j-blog-spring)
