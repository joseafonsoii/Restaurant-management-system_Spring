# Restaurant Management System (Spring)

Sistema de gestao para um restaurante, desenvolvido com Spring Boot. O projeto cobre o cadastro de categorias e itens do cardapio, gestao de mesas, pedidos (com itens e status) e pagamentos, exibindo ainda um dashboard com estatisticas.

A interface foi construida com Thymeleaf e Bootstrap (server-side rendering); a API REST continua disponivel sob o prefixo `/api/`.

## Tecnologias

- Java 21
- Spring Boot 3.5.5
- Spring Data JPA (Hibernate)
- Spring Web (MVC + API REST)
- Thymeleaf + Bootstrap 5 (via CDN)
- Flyway (migracao de schema)
- MySQL 8
- Lombok
- Maven (wrapper incluso)

## Estrutura do projeto

```
src/main/java/ao/jose/restaurant_management_system/
  config/      Configuracoes (CORS, recursos estaticos)
  controller/  Controllers REST (/api/**)
  controller/web/  Controllers das paginas Thymeleaf
  dto/         DTOs de request, response e estadisticas
  exceptions/  Tratamento de erros
  model/       Entidades JPA e enums
  repository/  Repositorios Spring Data JPA
  service/     Regras de negocio

src/main/resources/
  templates/   Paginas Thymeleaf (+ fragments do layout)
  db/migration/  Migracoes Flyway (V1..V7)
  application.yml  Configuracao da aplicacao
```

## Pre-requisitos

- Java 21
- MySQL 8 com o servico em execucao

## Configuracao do banco de dados

Crie o banco e um utilizador dedicado para a aplicacao:

```sql
CREATE DATABASE restaurant_db;
CREATE USER 'rms_app'@'localhost' IDENTIFIED BY '123';
GRANT ALL PRIVILEGES ON restaurant_db.* TO 'rms_app'@'localhost';
FLUSH PRIVILEGES;
```

Se o MySQL estiver com binary logging ativo, a criacao de triggers (usada nas migracoes V2 a V5) exige a variavel `log_bin_trust_function_creators`:

```sql
SET GLOBAL log_bin_trust_function_creators = 1;
```

Para tornar essa definicao permanente, adicione `log_bin_trust_function_creators = 1` na secao `mysqld` do ficheiro de configuracao do MySQL.

As credenciais padrao estao em `src/main/resources/application.yml` (`rms_app` / `123`).

## Como executar

```bash
./mvnw spring-boot:run
```

A aplicacao fica disponivel em <http://localhost:8080>. O schema e criado automaticamente pelas migracoes Flyway na primeira execucao.

Para compilar sem executar:

```bash
./mvnw clean compile
```

## Paginas Web

| Rota                          | Descricao                       |
|-------------------------------|---------------------------------|
| `/`                           | Dashboard (estatisticas)        |
| `/categories`                 | Lista de categorias             |
| `/categories/new`             | Nova categoria                  |
| `/menu-items`                 | Cardapio                        |
| `/menu-items/new`             | Novo item                       |
| `/tables`                     | Mesas                           |
| `/tables/new`                 | Nova mesa                       |
| `/orders`                     | Pedidos                         |
| `/orders/new`                 | Novo pedido                     |
| `/orders/{id}`                | Detalhe do pedido               |
| `/payments`                   | Pagamentos                      |
| `/payments/new`               | Novo pagamento                  |

## API REST

A API REST mantem os seguintes recursos sob o prefixo `/api/`:

- `/api/categories`
- `/api/menu-items`
- `/api/tables`
- `/api/orders`
- `/api/order-items`
- `/api/payments`

Todos suportam as operacoes CRUD apropriadas, alem de endpoints especificos como mudanca de status, ocupacao/utilizacao de mesas e estatisticas.

## Entidades

Category, MenuItem, RestaurantTable, Order, OrderItem e Payment. Os pedidos e pagamentos possuem estados controlados por enums (`OrderStatus`, `PaymentStatus`, `PaymentMethod`, `TableStatus`, `OrderItemStatus`).

## Migracoes Flyway

O schema e versionado em `src/main/resources/db/migration`:

- `V1` - criacao das tabelas, indices e views
- `V2` - triggers de `updated_at`
- `V3` - triggers de validacao de inventario
- `V4` - atualizacao automatica do total do pedido
- `V5` - triggers de gestao de mesas
- `V6` - dados iniciais (seed)
- `V7` - alinhamento dos ids/FK para `BIGINT`