INSERT INTO USERS (telegram_id, first_name, last_name, personal_number, is_manager, division) VALUES ('200','Руслан', 'Бурзянцев','22000000', true, 'КХП');

INSERT INTO USERS (telegram_id, first_name, last_name, personal_number,manager_id, is_manager, division) VALUES ('201','Подчненный', 'Хеувича','22000555', '2', false, 'КХП');
INSERT INTO USERS (first_name, last_name, personal_number, is_manager) VALUES ('Дмитрий', 'Кандалов','22065623', true);
INSERT INTO USERS (first_name, last_name, personal_number, is_manager, manager_id) VALUES ('Подчиненный', 'Кандалова','22065624', false, '3');
