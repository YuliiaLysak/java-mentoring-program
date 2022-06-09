create table sport
(
    sport_id     bigserial not null,
    decathlon_id bigint,
    name         varchar(255),
    description  varchar(1024),
    slug         varchar(255),
    icon         varchar(255),
    primary key (sport_id)
);