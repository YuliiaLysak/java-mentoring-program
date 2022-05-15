create table orders
(
    order_id    bigserial    not null,
    order_time  timestamp    not null,
    pizza_size  varchar(255) not null,
    final_price float8       not null,
    status      varchar(255) not null,
    primary key (order_id),
    unique (order_id, order_time)
);

create table pizza_toppings
(
    order_id   int8    not null,
    topping_name varchar(255) not null
);
