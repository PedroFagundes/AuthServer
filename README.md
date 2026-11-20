# AuthServer - Restaurante

API REST desenvolvida em Kotlin com Spring Boot para autenticação, gestão de produtos, clientes e pedidos. Projeto desenvolvido como requisito de avaliação da disciplina de Desenvolvimento Back-End.

## Integrantes da Equipe
- Pedro Henrique Alves de Souza Fagundes
- Yasmim Egidio de Oliveira

---

## Link do Youtube: https://youtu.be/jMLPsmVNJcQ

---

## Tecnologias Utilizadas
- Linguagem: Kotlin

## Modelo de Dados e Relacionamentos JPA
O sistema foi estruturado(customers, products, orders, users):

1. User (1) for CustomerProfile (1):
   - Relacionamento One To One utilizando Maps Id.
   - Vincula os dados de entrega e contato do cliente diretamente ao ID da conta do usuário.

2. User (1) for Order (N):
   - Relacionamento Many To One. Um usuário pode realizar múltiplos pedidos no sistema.

3. Order (N) for Product (N) com Tabela Associativa intermediária (OrderItem):
   - Relacionamento One To Many em Order e Many To one em OrderItem.
   - A entidade OrderItem funciona como a tabela associativa no meio, agregando atributos de negócio próprios (quantidade e preço unitário congelado no momento da compra).

## Como Executar o Projeto

### Pré-requisitos
- Java JDK 21 instalado e configurado nas variáveis de ambiente.

### Execução via Terminal
1. No terminal do projeto, execute o comando:
   ./gradlew bootRun

2. Aguarde a mensagem de inicialização no console:
   Tomcat started on port 8080 (http) with context path '/api'

3. Acesse a documentação interativa no navegador:
   - Swagger UI: http://localhost:8080/api/swagger-ui/index.html
   
## Principais Recursos da API

- Produtos (/products):
  - Cadastro de produto com validação e checagem de duplicidade de nome.
  - Listagem filtrável por texto (`search`), categoria (`category`), faixa de preço (`minPrice`/`maxPrice`) e situação (`active`), com ordenação (`sortDir`).
  - Consulta por ID, atualização e remoção.

- Usuários (/users):
  - Listagem filtrável por texto no nome/e-mail (`search`) e perfil de acesso (`role`), com ordenação (`sortDir`).

- Perfis de acesso (/roles):
  - Listagem filtrável por texto no nome/descrição (`search`).

- Pedidos (/orders):
  - Abertura de pedido associado a um usuário.
  - Listagem filtrável por texto no usuário/produtos (`search`), estado (`status`), usuário (`userId`), produto (`productId`) e período (`createdAfter`/`createdBefore`).
  - Associação e desassociação de itens ao pedido com cálculo de subtotal e total.
  - Consulta detalhada do pedido com carregamento otimizado.

- Clientes (/users/{userId}/profile):
  - Cadastro e consulta de perfil de endereço/contato associado 1:1 ao usuário.
- Perfis de clientes (/users/profiles):
  - Listagem filtrável por texto (`search`), endereço (`address`), telefone (`phone`) e usuário (`userId`).

