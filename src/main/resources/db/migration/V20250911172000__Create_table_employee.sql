create table t_employee
(
    id         bigint auto_increment primary key,
    age        int          null,
    company_id bigint       null,
    gender     varchar(255) null,
    name       varchar(255) null,
    salary     double       null,
    active     boolean      null
);
