create table "user"
(
    id    varchar(36) not null constraint table_name_pk primary key,
    name  varchar(50) not null,
    skill int         not null
);

