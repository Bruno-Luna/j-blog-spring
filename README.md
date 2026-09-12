# J-Blog Spring API 🚀

Uma API RESTful completa para gerenciamento de um blog, desenvolvida com o ecossistema Spring Boot. A aplicação provê funcionalidades de criação de usuários, autenticação via JWT, além de um sistema de CRUD (Create, Read, Update, Delete) de posts.

## 🛠️ Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes tecnologias e frameworks:

* **Java 21**: Linguagem de programação principal.
* **Spring Boot 3.3.4**: Framework para facilitação do setup e desenvolvimento.
* **Spring Data JPA**: Abstração para persistência de dados.
* **Spring Security**: Gerenciamento de autenticação e controle de acesso.
* **JSON Web Tokens (JWT)**: Para autenticação e autorização stateless e segura.
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

### 3. Ajustar as variáveis de ambiente (application.properties)

Verifique o arquivo `src/main/resources/application.properties` e certifique-se de que as credenciais do banco de dados e a secret do JWT correspondem ao seu ambiente de desenvolvimento local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/blog-db
spring.datasource.username=postgres
spring.datasource.password=root

# Secret para geração dos tokens JWT
jwt.secret=uma-string-bem-grande-e-segura-para-chave-jwt-2026
```

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

Abaixo estão listados os principais endpoints disponíveis na aplicação. Para rotas protegidas (como posts), não se esqueça de passar o token JWT no cabeçalho `Authorization: Bearer <seu-token>`.

### 👤 Usuários (`/user`)

* `POST /user/register`: Registra um novo usuário no sistema.
* `POST /user/login`: Autentica o usuário e retorna o Token JWT.

### 📝 Posts (`/post`)

* `GET /post`: Retorna todos os posts.
* `GET /post/{userId}`: Retorna todos os posts escritos por um usuário específico.
* `POST /post`: Cria um novo post.
* `PUT /post`: Edita um post existente.
* `DELETE /post`: Exclui um post através do seu ID.

## 📁 Estrutura de Diretórios

A estrutura do código fonte principal (`src/main/java/br/com/blog`) está organizada de forma coesa:

* `api/`: Modelos genéricos de resposta das APIs (`ApiResponse`).
* `configs/security/`: Configurações do Spring Security, filtro de JWT, etc.
* `controllers/`: Controladores REST, lidam com as requisições e respostas HTTP.
* `dto/`: Objetos de Transferência de Dados, isolando entidades das chamadas externas.
* `models/`: Entidades JPA representando as tabelas do banco de dados (`User`, `Post`).
* `repositories/`: Interfaces do Spring Data JPA comunicando diretamente com o BD.
* `services/`: Regras de negócio, serviços de geração/validação de JWT e serviços principais de usuários/posts.
