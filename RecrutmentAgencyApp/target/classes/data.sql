
insert into usr (active, cv_application_id, email, password, phone, username, vacancy_id, id)
    values (true, null, 'email@mail.com', 'password', '09912312', 'admin', null, 0);

insert into user_role (user_id, roles)
    values(0, 'ADMIN');