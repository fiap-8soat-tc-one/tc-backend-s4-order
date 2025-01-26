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

alter table order_request_historic
    add constraint FKiql9vtdg3qadyne7uy26d9a5v
        foreign key (id_order)
            references order_request;

create index order_request_index_status on public.order_request (status);