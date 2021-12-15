create table usr (
                     id bigint not null,
                     active boolean not null,
                     email varchar(255),
                     password varchar(255),
                     phone varchar(255),
                     username varchar(255),
                     cv_application_id bigint,
                     vacancy_id bigint,
                     primary key (id)
);

create table user_role (   user_id bigint not null,
                           roles varchar(255)
);

create table cv (
                    id bigint not null,
                    expected_salary integer,
                    passport_number varchar(255),
                    passport_series varchar(255),
                    skills varchar(255),
                    user_id bigint,
                    primary key (id)
);

create table vacancy (
                         id bigint not null,
                         desired_applicant_skills varchar(255),
                         lower_wage_limit integer,
                         name varchar(255),
                         social_package boolean,
                         upper_wage_limit integer,
                         user_id bigint,
                         primary key (id)
);