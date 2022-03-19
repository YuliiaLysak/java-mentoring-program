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

create table ingredients
(
    id   bigserial    not null,
    name varchar(255) not null,
    primary key (id)
);

create table recipes_ingredients
(
    id               bigserial    not null,
    recipe_id        int8         not null,
    ingredient_id    int8         not null,
    quantity         float8       not null,
    measurement_unit varchar(255) not null,
    primary key (id)
);

alter table recipes_ingredients
    add constraint fk_recipe_id
        foreign key (recipe_id)
            references recipes
            on delete cascade;

alter table recipes_ingredients
    add constraint fk_ingredient_id
        foreign key (ingredient_id)
            references ingredients
            on delete cascade;