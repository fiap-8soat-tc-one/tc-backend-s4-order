create table order_item
(
    id            serial not null,
    active        boolean default true,
    register_date timestamp,
    updated_date  timestamp,
    name          varchar(100),
    quantity      int4,
    total         numeric(19, 2),
    unit_value    numeric(19, 2),
    id_order      int4   not null,
    id_product    uuid   not null,
    primary key (id)
);

create table order_payment
(
    id                   serial      not null,
    active               boolean default true,
    register_date        timestamp,
    updated_date         timestamp,
    payment_type         varchar(20) not null,
    status               varchar(20) not null,
    result_error_date    timestamp,
    result_success_date  timestamp,
    total                numeric(19, 2),
    transaction_document varchar(30) not null,
    transaction_message  varchar(255),
    transaction_number   varchar(255),
    id_order             int4        not null,
    primary key (id)
);

create table order_payment_historic
(
    id               serial      not null,
    register_date    timestamp   not null,
    status           varchar(20) not null,
    transaction_message  varchar(255),
    id_order_payment int4        not null,
    primary key (id)
);

create table order_request (
       id int4 not null,
        active boolean default true,
        register_date timestamp,
        updated_date timestamp,
        order_number varchar(255),
        status varchar(255) not null,
        total numeric(19, 2),
        uuid uuid,
        id_customer uuid,
        primary key (id)
    );

create table order_request_historic
(
    id            serial       not null,
    register_date timestamp    not null,
    status        varchar(255) not null,
    id_order      int4,
    primary key (id)
);

create sequence order_request_id_seq start 1 increment 1;

alter table order_item
    add constraint FKmvybm38wikbsa2eh5vcgq2k8j
        foreign key (id_order)
            references order_request;

alter table order_payment
    add constraint FK22ae8c8eusj6bcfeep1jp8l7s
        foreign key (id_order)
            references order_request;

alter table order_payment_historic
    add constraint FKlb7k4tfm886jv3g9urhhwtqtu
        foreign key (id_order_payment)
            references order_payment;

alter table order_request_historic
    add constraint FKiql9vtdg3qadyne7uy26d9a5v
        foreign key (id_order)
            references order_request;
