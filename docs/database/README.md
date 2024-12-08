# Documentação do banco de dados :floppy_disk:

## PostgreSQL :floppy_disk:

PostgreSQL é um sistema de gerenciamento de banco de dados relacional e objeto-relacional (ORDBMS) de código aberto. Ele
é conhecido por sua robustez, extensibilidade e conformidade com os padrões SQL. Foi desenvolvido para ser altamente
escalável, suportando grandes volumes de dados e usuários simultâneos.

### Vantagens do PostgreSQL :heavy_check_mark:

1. **Conformidade com SQL:** PostgreSQL é altamente compatível com o padrão SQL, garantindo portabilidade e facilidade
   de uso.
2. **Extensibilidade:** Permite a criação de tipos de dados personalizados, funções e operadores, oferecendo grande
   flexibilidade.
3. **Desempenho e Escalabilidade:** Suporta consultas complexas e grandes volumes de dados com eficiência.
4. **Segurança:** Oferece recursos avançados de segurança, como autenticação baseada em roles, SSL, e controle de acesso
   granular.
5. **Transações ACID:** Garantia de integridade e consistência dos dados através de transações completas com
   Atomicidade, Consistência, Isolamento e Durabilidade.
6. **Comunidade Ativa:** Um grande número de desenvolvedores e documentação disponível, facilitando a resolução de
   problemas e o aprendizado.

### Sinergia com Java :heavy_check_mark:

1. **JDBC (Java Database Connectivity):** O PostgreSQL possui drivers JDBC robustos, permitindo uma integração suave com
   aplicações Java.
2. **ORMs (Object-Relational Mappers):** Ferramentas como Hibernate e JPA (Java Persistence API) funcionam muito bem com
   PostgreSQL, facilitando o mapeamento objeto-relacional e reduzindo a complexidade do código.
3. **Multiplataforma:** Tanto PostgreSQL quanto Java são multiplataforma, permitindo a criação de aplicações portáveis e
   independentes de sistema operacional.
4. **Desempenho:** A combinação de PostgreSQL com Java permite a criação de aplicações de alto desempenho, aproveitando
   o melhor das capacidades de gerenciamento de dados de PostgreSQL e a eficiência da JVM (Java Virtual Machine).
5. **Ferramentas de Desenvolvimento:** Existem várias ferramentas de desenvolvimento e bibliotecas em Java que oferecem
   suporte direto para PostgreSQL, melhorando a produtividade do desenvolvedor.
6. **Escalabilidade e Manutenção:** Aplicações em Java com PostgreSQL são fáceis de escalar e manter, graças à robustez
   e confiabilidade de ambos.

### Modelo entidade relacionamento

O modelo de dados procura ser uma representação das entidades de domínio para esse sistema, de modo a suportar as 
seguintes funcionalidades e operações de leitura e escrita:
-  Pedido: realizar pedido, acompanhar pedido, pagamento, acompanhamento e entrega
- Gerenciamento de Clientes
- Gerenciamento de Produtos e Categorias

Abaixo uma breve documentação das tabelas do sistema, com base no diagrama de entidade relacionamento evidenciado:

![image](https://github.com/fiap-8soat-tc-one/tc-backend-s3/blob/main/assets/model-er.png)
         
- Gerenciamento de clientes é identificado basicamente pela tabela customer:
<b>customer</b>: contem informações dos clientes do sistema. O principal campo desta tabela é <b>document</b> que 
  possui uma constraint unique, ou seja, permite armazena o campo cpf do cliente como sendo único, isto é, não
  permitindo duplicação do mesmo. Possui relação com tabela <b>order_request</b> (tabela responsável pelos pedidos) 
  através da coluna id_customer. Esta relação é do tipo nullable, permitindo que um pedido seja aberto sem a 
  obrigatoriedade de identificação do cliente
- Gerenciamento de categorias e produtos: Representado pelas tabelas: product (armazena
  informações dos produtos disponíveis no sistema, como name, description, price e id_category) e category (category 
  define as categorias para agrupar os produtos). Produto se relaciona com category através da coluna id_category
- Pedido:
  - order_request registra os pedidos realizados pelos clientes. Possui relação com customer e item
  - item: contém informações dos itens associados a um pedido, como id_product, quantity, total e unit_value. Possui
    relacionamento com tabelas order_request e product.
  - order_payment: registra pagamentos associados a pedidos. Possui relacionamento com order_request

### Melhorias estrutura de banco de dados
- Foi realizada uma analise das principais consultas usadas pela aplicação, com base nas informações abaixo, foi 
  verificado que as tabelas abaixo e suas respectivas colunas poderiam ser agregados índices para melhorar sua 
  performance, visando que o sistema possa ser mais escalável, e consiga comportar mais dados no decorrer do tempo, sem 
  comprometer a performance e experiência do usuário.
- Os campos do tipo uuid já são criados de forma indexada pelo próprio postgres. Os demais campos foi criada uma 
  migration com a criação desses índices.

| Tabela                         | Colunas                                                                           |
|--------------------------------|-----------------------------------------------------------------------------------|
| category                       | name, uuid                                                                        |
| product                        | name, uuid                                                                        |
| customer                       | uuid, document                                                                    |
| order_request                  | uuid, status                                                                      |

- Script migration com a criação dos índices:

````
set schema 'public';

create index category_index_name on public.category (name);
create index customer_index_document on public.customer (document);
create index order_request_index_status on public.order_request (status);
create index product_index_name on public.product (name);
````