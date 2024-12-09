set schema 'public';

create index category_index_name on public.category (name);
create index customer_index_document on public.customer (document);
create index order_request_index_status on public.order_request (status);
create index product_index_name on public.product (name);