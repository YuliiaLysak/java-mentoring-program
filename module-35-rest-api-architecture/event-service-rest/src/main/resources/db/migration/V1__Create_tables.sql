create table events
(
    id         bigserial    not null,
    title      varchar(255) not null,
    place      varchar(255) not null,
    speaker    varchar(255) not null,
    event_type varchar(255) not null,
    date_time  timestamp    not null,
    primary key (id),
    unique (title, speaker, date_time)
);
