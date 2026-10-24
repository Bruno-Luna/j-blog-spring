# J-Blog Spring API 🚀

API REST para gerenciamento de usuários e posts, desenvolvida com Spring Boot. A aplicação oferece cadastro de usuários, autenticação via JWT e gerenciamento autenticado dos próprios posts.

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

Versão atual da aplicação: `0.2.0`.

## Pré-requisitos

- JDK 21 configurado em `JAVA_HOME`.
- PostgreSQL em execução na porta `5432`.
- Maven é opcional, pois o projeto inclui `mvnw` e `mvnw.cmd`.

## Configuração local

1. Clone o repositório e entre na pasta:

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

   O projeto usa `ddl-auto=update`, uma configuração adequada apenas para desenvolvimento. Em ambientes reais, forneça as credenciais do banco e o segredo JWT por variáveis de ambiente ou configuração externa. O segredo deve ser longo, aleatório e mantido fora do controle de versão.

## Execução

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8085`.

A documentação OpenAPI pode ser acessada em:

- Swagger UI: `http://localhost:8085/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8085/v3/api-docs`

## Autenticação

Os endpoints de registro e login são públicos. Os endpoints de posts exigem um token JWT no cabeçalho:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Características da autenticação:

- O username é usado como subject do token.
- O token expira após 30 minutos.
- As senhas são armazenadas usando BCrypt.
- As sessões são stateless.
- CSRF está desabilitado para a API.
- O CORS está aberto para qualquer origem na configuração atual.

## Endpoints

### Usuários

#### `POST /user/register`

Cria um usuário.

Corpo da requisição:

```json
{
  "username": "seu-nome",
  "password": "sua-senha"
}
```

Respostas principais:

- `201 Created`: usuário criado, com `user` na raiz da resposta.
- `409 Conflict`: username já utilizado.

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

Autentica um usuário.

Corpo da requisição:

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

Credenciais inválidas retornam `401 Unauthorized`.

### Posts

Todos os endpoints desta seção exigem autenticação JWT.

#### `GET /post/me/posts`

Lista os posts pertencentes ao usuário autenticado.

- `200 OK`: retorna diretamente um array de posts.
- `204 No Content`: o usuário não possui posts.

Cada post contém `postId`, `title`, `body` e `updatedAt`. A data é formatada como `dd/MM/yyyy HH:mm:ss`.

#### `POST /post`

Cria um post para o usuário autenticado.

Corpo da requisição:

```json
{
  "title": "Meu primeiro post",
  "body": "Conteúdo do post"
}
```

Regras:

- `title` é obrigatório e aceita até 100 caracteres.
- `body` é obrigatório.
- `postId`, usuário e data são definidos pelo servidor.

Resposta de sucesso: `201 Created`, com o post no campo `post` da raiz da resposta.

Exemplo no PowerShell:

```powershell
curl.exe -X POST http://localhost:8085/post `
  -H "Authorization: Bearer SEU_TOKEN_JWT" `
  -H "Content-Type: application/json" `
  -d '{"title":"Meu primeiro post","body":"Conteúdo do post"}'
```

#### `PUT /post/{postId}`

Edita um post do usuário autenticado. O UUID deve ser informado na URL, e não no corpo.

Exemplo de URL:

```text
PUT /post/UUID_DO_POST
```

Corpo da requisição:

```json
{
  "title": "Título atualizado",
  "body": "Conteúdo atualizado"
}
```

Respostas principais:

- `200 OK`: post atualizado.
- `403 Forbidden`: o post pertence a outro usuário.
- `404 Not Found`: post ou usuário não encontrado.

#### `DELETE /post/{postId}`

Exclui um post do usuário autenticado. O UUID deve ser informado na URL e a requisição não precisa de corpo.

Exemplo:

```text
DELETE /post/UUID_DO_POST
```

Respostas principais:

- `200 OK`: post excluído.
- `403 Forbidden`: o post pertence a outro usuário.
- `404 Not Found`: post inexistente.

## Formato das respostas

As respostas de operações usam `status` e `message`. Quando aplicável, os dados são adicionados diretamente na raiz, sem um campo `data` intermediário.

Exemplo de criação ou edição de post:

```json
{
  "status": 201,
  "message": "Post created with success",
  "post": {
    "postId": "UUID_DO_POST",
    "title": "Meu primeiro post",
    "body": "Conteúdo do post",
    "updatedAt": "09/10/2026 14:30:00"
  }
}
```

O endpoint `GET /post/me/posts` retorna diretamente um array JSON. O login retorna o token no campo `token`, e o cadastro retorna o usuário no campo `user`.

## Códigos de erro

- `400 Bad Request`: dados inválidos ou argumentos incorretos.
- `401 Unauthorized`: credenciais inválidas ou autenticação ausente.
- `403 Forbidden`: usuário não é proprietário do post.
- `404 Not Found`: usuário ou post não encontrado.
- `409 Conflict`: username já existente.

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

- `api/`: respostas padronizadas e configuração OpenAPI.
- `config/security/`: configuração do Spring Security, filtro JWT e carregamento de usuários.
- `config/exceptions/`: tratamento global de exceções.
- `controllers/`: endpoints REST de usuários e posts.
- `dto/`: objetos de requisição e resposta.
- `models/`: entidades JPA de usuários e posts.
- `repositories/`: repositórios Spring Data JPA.
- `services/`: regras de negócio e geração/validação de JWT.
- `BlogApplication`: classe principal e configuração do log de requisições.

## Considerações para produção

- Remover credenciais do arquivo `application.properties` e usar configuração externa.
- Usar um segredo JWT longo, aleatório e protegido.
- Restringir o CORS a origens confiáveis.
- Substituir `ddl-auto=update` por uma estratégia de migração de banco.
- Avaliar a exposição de payloads, query strings e informações do cliente nos logs de requisição.
- Adicionar testes para autenticação, autorização, validação e operações de posts.
- Revisar a nomenclatura do esquema de segurança exibido no OpenAPI para garantir que o botão de autenticação do Swagger corresponda às anotações dos controllers.

## Autor

[Bruno-Luna](https://github.com/Bruno-Luna) · [Repositório no GitHub](https://github.com/Bruno-Luna/j-blog-spring)
