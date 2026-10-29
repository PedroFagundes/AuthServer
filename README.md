# AuthServer - Módulo de E-commerce / Restaurante

API REST desenvolvida em Kotlin com Spring Boot para autenticação, gestão de produtos, clientes e pedidos. Projeto desenvolvido como requisito de avaliação da disciplina de Desenvolvimento Back-End.

---

## 👥 Integrantes da Equipe
- Pedro Henrique Alves de Souza Fagundes
- Yasmim Egidio

---

## 🛠️ Tecnologias Utilizadas
- Linguagem: Kotlin
- Framework: Spring Boot 4.0
- Persistência de Dados: Spring Data JPA / Hibernate
- Banco de Dados: H2 Database (In-Memory)
- Segurança: Spring Security
- Documentação de API: SpringDoc OpenAPI / Swagger UI
- Padronização de Erros: RFC 9457 (ProblemDetail)
- Logs: SLF4J

---

## 📐 Modelo de Dados e Relacionamentos JPA

O sistema foi estruturado seguindo o padrão package-by-domain (customers, products, orders, users):

1. User (1) <-> CustomerProfile (1):
   - Relacionamento @OneToOne utilizando @MapsId.
   - Vincula os dados de entrega e contato do cliente diretamente ao ID da conta do usuário.

2. User (1) <-> Order (N):
   - Relacionamento @ManyToOne. Um usuário pode realizar múltiplos pedidos no sistema.

3. Order (N) <-> Product (N) com Tabela Associativa intermediária (OrderItem):
   - Relacionamento @OneToMany em Order e @ManyToOne em OrderItem.
   - A entidade OrderItem funciona como a tabela associativa no meio, agregando atributos de negócio próprios (quantidade e preço unitário congelado no momento da compra).

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Java JDK 21 instalado e configurado nas variáveis de ambiente.

### Execução via Terminal
1. No terminal do projeto, execute o comando:
   ./gradlew bootRun

2. Aguarde a mensagem de inicialização no console:
   Tomcat started on port 8080 (http) with context path '/api'

3. Acesse a documentação interativa no navegador:
   - Swagger UI: http://localhost:8080/api/swagger-ui/index.html
   - H2 Console: http://localhost:8080/api/h2-console
     - JDBC URL: jdbc:h2:mem:db
     - User: SA
     - Password: (em branco)

---

## 📌 Principais Recursos da API

- Produtos (/products):
  - Cadastro de produto com validação e checagem de duplicidade de nome.
  - Listagem com filtros opcionais via query params (category e sortDir).
  - Consulta por ID, atualização e remoção.

- Pedidos (/orders):
  - Abertura de pedido associado a um usuário.
  - Associação e desassociação de itens ao pedido com cálculo de subtotal e total.
  - Consulta detalhada do pedido com carregamento otimizado (LEFT JOIN FETCH).

- Clientes (/users/{userId}/profile):
  - Cadastro e consulta de perfil de endereço/contato associado 1:1 ao usuário.

---

## 🛡️ Tratamento de Erros e Auditoria

- RFC 9457 (ProblemDetail): Respostas de erro padronizadas globalmente via @RestControllerAdvice para validações (HTTP 422), recursos não encontrados (HTTP 404) e requisições inválidas (HTTP 400).
- Logs com SLF4J: Rastreamento em nível INFO e WARN nas operações de criação, alteração e regras de negócio.