# J-Blog Spring API 🚀

Uma API RESTful completa para gerenciamento de um blog, desenvolvida com o ecossistema Spring Boot. A aplicação provê funcionalidades de criação de usuários, autenticação via JWT, além de um sistema de CRUD (Create, Read, Update, Delete) de posts.

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes tecnologias e frameworks:

* **Java 21**: Linguagem de programação principal.
* **Spring Boot 3.3.4**: Framework para facilitação do setup e desenvolvimento.
* **Spring Data JPA**: Abstração para persistência de dados.
* **Spring Security**: Gerenciamento de autenticação e controle de acesso.
* **JSON Web Tokens (JWT)**: Para autenticação stateless das rotas protegidas.
* **PostgreSQL**: Banco de dados relacional.
* **Maven**: Gerenciador de dependências e build.

## ⚙️ Pré-requisitos

Para rodar a aplicação localmente, certifique-se de ter os seguintes componentes instalados:

* [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) ou superior.
* [Maven](https://maven.apache.org/) (Opcional, pois o projeto possui o Maven Wrapper `mvnw`).
* [PostgreSQL](https://www.postgresql.org/) rodando localmente na porta padrão (`5432`).

## 🚀 Configurando e Executando

### 1. Clonar o repositório

Caso ainda não o tenha feito, faça o clone do projeto (se aplicável):

```bash
git clone <url-do-repositorio>
cd j-blog-spring
```

### 2. Configurar o Banco de Dados

Crie um banco de dados no PostgreSQL chamado `blog-db`:

```sql
CREATE DATABASE "blog-db";
```

### 3. Configurar o `application.properties`

Verifique o arquivo `src/main/resources/application.properties` e certifique-se de que as credenciais do banco de dados e a secret do JWT correspondem ao seu ambiente de desenvolvimento local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog-db
spring.datasource.username=postgres
spring.datasource.password=root

# Secret para geração dos tokens JWT
jwt.secret=uma-string-bem-grande-e-segura-para-chave-jwt-2026
```

O arquivo atual também usa `spring.jpa.hibernate.ddl-auto=update` e logs detalhados de HTTP, Spring Security e SQL. Esses valores são úteis durante o desenvolvimento, mas devem ser revisados antes de um ambiente de produção. Não use a senha do banco e a chave JWT de exemplo em produção.

### 4. Compilar e Iniciar a Aplicação

Utilize o Maven Wrapper já incluído no projeto para rodar a aplicação facilmente:

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

**Windows:**
```cmd
mvnw.cmd spring-boot:run
```

A API estará disponível e escutando por padrão em `http://localhost:8080`.

## 🌐 Endpoints da API

Abaixo estão listados os principais endpoints disponíveis na aplicação. As rotas de usuário são públicas; as rotas de posts exigem um token JWT no header `Authorization: Bearer <token>`.

### 👤 Usuários (`/user`)

* `POST /user/register`: Registra um novo usuário no sistema.
* `POST /user/login`: Autentica o usuário e retorna o Token JWT.

### 📝 Posts (`/post`)

* `GET /post/me/posts`: Retorna os posts do usuário autenticado.
* `POST /post`: Cria um novo post.
* `PUT /post`: Edita um post existente.
* `DELETE /post`: Exclui um post através do seu `postId`.

### Criar um post com o token JWT

Depois de fazer login em `POST /user/login`, copie o token retornado em `data.token` e envie-o no header `Authorization`. O usuário do post é identificado automaticamente pelo token; não envie `user`, `userId`, `postId` ou `localDateTime` no cadastro:

```powershell
curl.exe -X POST http://localhost:8080/post `
  -H "Authorization: Bearer SEU_TOKEN_JWT" `
  -H "Content-Type: application/json" `
  -d '{"title":"Meu primeiro post","body":"Conteúdo do post"}'
```

O endpoint retorna `201 Created` e define `postId` e `localDateTime` automaticamente. Sem token, com token expirado ou com token inválido, a requisição é rejeitada pelo Spring Security (normalmente `403 Forbidden` com a configuração atual).

### Exemplos de cadastro e login

#### Registrar usuário

```http
POST /user/register
Content-Type: application/json

{
  "username": "seu-nome",
  "password": "senha-segura"
}
```

Retorna `201 Created`. O username duplicado retorna `409 Conflict`.

#### Fazer login

```http
POST /user/login
Content-Type: application/json

{
  "username": "seu-nome",
  "password": "senha-segura"
}
```

A resposta bem-sucedida retorna `200 OK` com o token em `data.token`. O token JWT expira após 30 minutos.

### Consultar, editar e excluir posts

Todas as operações abaixo exigem o header:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

Para consultar os posts do usuário autenticado:

```powershell
curl.exe http://localhost:8080/post/me/posts `
  -H "Authorization: Bearer SEU_TOKEN_JWT"
```

Se não houver posts, a API retorna `204 No Content`.

Para editar, envie o `postId` existente e os novos dados:

```powershell
curl.exe -X PUT http://localhost:8080/post `
  -H "Authorization: Bearer SEU_TOKEN_JWT" `
  -H "Content-Type: application/json" `
  -d '{"postId":"UUID_DO_POST","title":"Título atualizado","body":"Conteúdo atualizado"}'
```

Para excluir um post:

```powershell
curl.exe -X DELETE http://localhost:8080/post `
  -H "Authorization: Bearer SEU_TOKEN_JWT" `
  -H "Content-Type: application/json" `
  -d '{"postId":"UUID_DO_POST"}'
```

O `postId` deve ser um UUID válido. O título aceita no máximo 100 caracteres. O campo `user` não precisa ser enviado: o proprietário é obtido pelo usuário autenticado no JWT.

As respostas de posts usam os campos `title`, `body`, `createdAt`, `userId` e `username`. O campo `createdAt` é formatado como `dd/MM/yyyy HH:mm:ss`. Atualmente, editar ou excluir um post exige autenticação, mas a implementação do serviço ainda deve validar a propriedade do post quando essa restrição for necessária.

### Autenticação e segurança

`POST /user/register` e `POST /user/login` são públicos. As demais rotas exigem um JWT válido no header `Authorization: Bearer ...`; sem autenticação, a configuração atual normalmente responde `403 Forbidden`. A aplicação usa sessões stateless, BCrypt para armazenar senhas e CORS liberado para qualquer origem nos controllers. Restrinja o CORS e forneça `jwt.secret` por configuração externa antes de publicar a aplicação.

### Testes e build

O projeto possui atualmente um teste de carregamento do contexto Spring. Antes de executar os comandos, configure `JAVA_HOME` apontando para um JDK 21.

**Windows PowerShell:**

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd clean package -DskipTests
```

**Linux / macOS:**

```bash
./mvnw clean test
./mvnw clean package -DskipTests
```

## 📁 Estrutura de Diretórios

A estrutura do código fonte principal (`src/main/java/br/com/blog`) está organizada de forma coesa:

* `api/`: Modelos genéricos de resposta das APIs (`ApiResponse`).
* `configs/security/`: Configurações do Spring Security, filtro de JWT, etc.
* `controllers/`: Controladores REST, lidam com as requisições e respostas HTTP.
* `dto/`: Objetos de Transferência de Dados, isolando entidades das chamadas externas.
* `models/`: Entidades JPA representando as tabelas do banco de dados (`User`, `Post`).
* `repositories/`: Interfaces do Spring Data JPA comunicando diretamente com o BD.
* `services/`: Regras de negócio, serviços de geração/validação de JWT e serviços principais de usuários/posts.

## 📄 Licença

Este projeto está disponível sob a [Licença MIT](LICENSE), uma licença open source permissiva que permite uso, cópia, modificação, distribuição e sublicenciamento, desde que o aviso de copyright e a licença sejam mantidos.

## 👨‍💻 Desenvolvido por

[Bruno Luna](https://github.com/Bruno-Luna) · [Repositório no GitHub](https://github.com/Bruno-Luna/j-blog-spring)
