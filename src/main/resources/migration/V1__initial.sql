create table "user"
(
    id      int constraint user_pk primary key,
    user_id VARCHAR(36) not null,
    name    varchar(50) not null,
    skill   int         not null
);

create unique index user_name_uindex on "user" (name);

