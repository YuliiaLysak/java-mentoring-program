create table recipes
(
    id          bigserial     not null,
    name        varchar(255)  not null,
    category    varchar(255)  not null,
    date        timestamp,
    description varchar(255)  not null,
    directions  varchar(4000) not null,
    primary key (id)
);

create table products
(
    id   bigserial    not null,
    name varchar(255) not null,
    primary key (id)
);

create table ingredients
(
    id               bigserial    not null,
    recipe_id        int8         not null,
    product_id    int8         not null,
    quantity         float8       not null,
    measurement_unit varchar(255) not null,
    primary key (id)
);

alter table ingredients
    add constraint fk_recipe_id
        foreign key (recipe_id)
            references recipes
            on delete cascade;

alter table ingredients
    add constraint fk_product_id
        foreign key (product_id)
            references products
            on delete cascade;