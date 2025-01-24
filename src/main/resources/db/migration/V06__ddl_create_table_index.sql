set schema 'public';

create index order_request_index_status on public.order_request (status);
