# J-Blog Spring API 🚀

API REST para gerenciamento de usuários e posts, desenvolvida com Spring Boot. O projeto oferece cadastro, login com JWT e operações autenticadas para criação, consulta, edição e exclusão de posts.

## Tecnologias

- Java 21
- Spring Boot 3.3.4
- Spring Web, Spring Data JPA, Spring Validation e Spring Security
- JJWT 0.11.5
- PostgreSQL
- Maven Wrapper

## Pré-requisitos

- JDK 21 configurado em `JAVA_HOME`
- PostgreSQL em execução na porta `5432`
- Maven instalado é opcional, pois o projeto inclui `mvnw` e `mvnw.cmd`

## Configuração local

1. Clone o repositório e entre na pasta:

   ```powershell
   git clone https://github.com/Bruno-Luna/j-blog-spring.git
   Set-Location j-blog-spring
   ```

2. Crie o banco de dados usado pela aplicação:

   ```sql
   CREATE DATABASE "blog-db";
   ```

3. Revise `src/main/resources/application.properties`:

   ```properties
   server.port=8085
   spring.datasource.url=jdbc:postgresql://localhost:5432/blog-db
   spring.datasource.username=postgres
   spring.datasource.password=root
   jwt.secret=uma-chave-com-pelo-menos-32-caracteres
   ```

   A aplicação usa `spring.jpa.hibernate.ddl-auto=update`, portanto o Hibernate atualiza o esquema automaticamente durante o desenvolvimento. Substitua a senha do banco e o segredo JWT por valores seguros; não versione credenciais reais.

## Executar

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

A API fica disponível em `http://localhost:8085`.

## Autenticação

`POST /user/register` e `POST /user/login` são públicos. Todas as rotas de posts exigem:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

O token é assinado com `jwt.secret`, usa o username como subject e expira 30 minutos após a emissão. Senhas são armazenadas com BCrypt e as sessões são stateless.

## Endpoints

### Usuários

#### `POST /user/register`

Cria um usuário. O corpo deve conter `username` e `password`:

```json
{
  "username": "seu-nome",
  "password": "sua-senha"
}
```

Retorna `201 Created` com os dados públicos do usuário (`userId`, `username` e `createdAt`). Se o username já existir, retorna `409 Conflict`.

#### `POST /user/login`

Autentica um usuário:

```json
{
  "username": "seu-nome",
  "password": "sua-senha"
}
```

Retorna `200 OK` com o token em `data.token`. Credenciais inválidas retornam `401 Unauthorized`.

### Posts

Todas as rotas a seguir exigem JWT.

#### `GET /post/me/posts`

Lista os posts do usuário autenticado. Retorna `200 OK` com uma lista de objetos contendo `postId`, `title`, `body` e `createdAt`. Se não houver posts, retorna `204 No Content`.

#### `POST /post`

Cria um post. O usuário, `postId` e data são definidos pelo servidor:

```json
{
  "title": "Meu primeiro post",
  "body": "Conteúdo do post"
}
```

Retorna `201 Created` com o post criado dentro de `data.post`. `title` é obrigatório e aceita até 100 caracteres; `body` também é obrigatório.

Exemplo no PowerShell:

```powershell
curl.exe -X POST http://localhost:8085/post `
  -H "Authorization: Bearer SEU_TOKEN_JWT" `
  -H "Content-Type: application/json" `
  -d '{"title":"Meu primeiro post","body":"Conteúdo do post"}'
```

#### `PUT /post`

Edita um post pelo UUID. Envie `postId`, `title` e `body`:

```json
{
  "postId": "UUID_DO_POST",
  "title": "Título atualizado",
  "body": "Conteúdo atualizado"
}
```

Retorna `200 OK` com o post atualizado em `data.post`.

#### `DELETE /post`

Exclui um post pelo UUID:

```json
{
  "postId": "UUID_DO_POST"
}
```

Retorna `200 OK` com a mensagem de confirmação.

## Respostas e erros

Respostas de operação usam o formato genérico:

```json
{
  "status": 201,
  "message": "Post created with success",
  "post": {
    "postId": "UUID_DO_POST",
    "title": "Meu primeiro post",
    "body": "Conteúdo do post",
    "createdAt": "22/09/2026 14:30:00"
  }
}
```

As datas são formatadas como `dd/MM/yyyy HH:mm:ss`. Erros de validação retornam `400 Bad Request`; acesso sem autenticação retorna `401 Unauthorized`; acesso negado retorna `403 Forbidden`; e username duplicado retorna `409 Conflict`.

## Testes e build

O projeto possui um teste de carregamento do contexto Spring:

```powershell
.\mvnw.cmd clean test
```

Para gerar o artefato sem executar os testes:

```powershell
.\mvnw.cmd clean package -DskipTests
```

## Estrutura do projeto

Em `src/main/java/br/com/blog`:

- `api/`: formato genérico das respostas (`ApiResponse`).
- `config/security/`: configuração do Spring Security, filtro JWT e carregamento de usuários.
- `config/exceptions/`: tratamento global de erros de validação e argumentos.
- `controllers/`: endpoints REST de usuários e posts.
- `dto/`: objetos de resposta da API.
- `models/`: entidades JPA `UserModel` e `PostModel`.
- `repositories/`: repositórios Spring Data JPA.
- `services/`: regras de negócio e geração/validação de JWT.

## Pontos de atenção

- O CORS está aberto para qualquer origem nos controllers; restrinja-o antes de publicar a aplicação.
- O segredo JWT e as credenciais do banco estão no arquivo de propriedades padrão; prefira configuração externa em ambientes reais.
- `ddl-auto=update` e os logs de requisições estão voltados para desenvolvimento.
- A implementação atual autentica as operações de edição e exclusão, mas o serviço ainda não verifica explicitamente se o post informado pertence ao usuário autenticado. Essa regra deve ser reforçada antes de usar a API em produção.

## Autor

[Bruno Luna](https://github.com/Bruno-Luna) · [Repositório no GitHub](https://github.com/Bruno-Luna/j-blog-spring)
