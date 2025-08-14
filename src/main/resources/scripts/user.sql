CREATE TABLE USERS
(
    id            serial PRIMARY KEY,
    username      VARCHAR(25) unique not null,
    password_hash text               not null,
    email         text        unique not null,
    user_role     int                not null default 4,
    created_at    TIMESTAMP          not null default now(),
    updated_at    TIMESTAMP                   default now(),
    deleted       BOOLEAN                     default false
);

create table refresh_tokens
(
    id            serial primary key,
    refresh_token varchar(255) not null unique,
    user_id       int          not null,
    expiry_date   TIMESTAMP    not null
);