CREATE DATABASE internships;
\c internships
\conninfo
-- Создание типа ENUM для роли пользователя
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
        CREATE TYPE user_role AS ENUM ('admin', 'curator', 'manager', 'student');
    END IF;
END $$;

-- Создание типа ENUM для статуса студента
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'student_status') THEN
        CREATE TYPE student_status AS ENUM ('sent_personal_info', 'added_in_chat', 'started_event', 'ended_event', 'deleted_from_event');
    END IF;
END $$;

CREATE TYPE event_condition AS ENUM ('HIDDEN', 'ACTIVE', 'DELETED', 'CLOSED');

-- Создание таблицы user
CREATE TABLE IF NOT EXISTS users_info (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    surname VARCHAR(50),
    email VARCHAR(255) UNIQUE NOT NULL,
    sign VARCHAR(255) NOT NULL,
    telegram_url VARCHAR(255) NOT NULL,
    vk_url VARCHAR(255),
    role_enum user_role DEFAULT 'student',
    competencies TEXT NOT NULL
);

-- Создание таблицы event
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    condition event_condition NOT NULL DEFAULT 'HIDDEN',
    description_text TEXT,
    title VARCHAR(255) NOT NULL,
    admin_id INT REFERENCES users_info(id) ON DELETE SET NULL,
    manager_id INT REFERENCES users_info(id) ON DELETE SET NULL,
    event_start_date TIMESTAMPTZ NOT NULL,
    event_end_date TIMESTAMPTZ NOT NULL,
    chat_url VARCHAR(255),
    enrollment_start_date TIMESTAMPTZ NOT NULL,
    enrollment_end_date TIMESTAMPTZ NOT NULL,
    number_seats INT NOT NULL
);

-- Создание таблицы event_student
CREATE TABLE IF NOT EXISTS events_students (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    student_status student_status DEFAULT 'sent_personal_info'
);

-- Создание таблицы event_curator
CREATE TABLE IF NOT EXISTS events_curators (
    id SERIAL PRIMARY KEY,
    curator_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE
);

-- Создание таблицы messages
CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    message_text TEXT NOT NULL,
    edit_date TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);


CREATE USER crm_admin WITH PASSWORD 'bitrix24';

GRANT ALL PRIVILEGES ON DATABASE internships TO crm_admin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO crm_admin;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO crm_admin;

GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO crm_admin;

-- Добавление 5 пользователей в таблицу users_info
INSERT INTO users_info (first_name, last_name, surname, email, sign, telegram_url, vk_url, role_enum, competencies)
VALUES
    ('Alexey', 'Ivanov', 'Sergeevich', 'alexey.ivanov@example.com', 'sign1', 'tg://alexey1', 'vk://alexey1', 'admin', 'Java, SQL'),
    ('Maria', 'Petrova', NULL, 'maria.petrova@example.com', 'sign2', 'tg://maria', 'vk://maria', 'curator', 'Project management, HR'),
    ('Ivan', 'Sidorov', 'Nikolaevich', 'ivan.sidorov@example.com', 'sign3', 'tg://ivan', NULL, 'student', 'Python, Data Science'),
    ('Olga', 'Kuznetsova', 'Alexeevna', 'olga.kuznetsova@example.com', 'sign4', 'tg://olga', 'vk://olga', 'manager', 'Marketing, Communication'),
    ('Sergey', 'Smirnov', NULL, 'sergey.smirnov@example.com', 'sign5', 'tg://sergey', 'vk://sergey', 'student', 'HTML, CSS, JavaScript');

-- Добавление 5 мероприятий в таблицу events
INSERT INTO events (condition, description_text, title, admin_id, manager_id, event_start_date, event_end_date, chat_url, enrollment_start_date, enrollment_end_date, number_seats)
VALUES
    ('ACTIVE', 'Java programming basics', 'Java Basics Workshop', 1, 4, '2024-11-01 10:00:00+05', '2024-11-01 18:00:00+05', 'chat1', '2024-10-01 10:00:00+05', '2024-10-31 23:59:00+05', 30),
    ('HIDDEN', 'Introduction to Data Science', 'Data Science Bootcamp', 3, 4, '2024-12-01 10:00:00+05', '2024-12-10 18:00:00+05', 'chat2', '2024-11-01 10:00:00+05', '2024-11-30 23:59:00+05', 25),
    ('DELETED', 'Marketing and Communication', 'Marketing Strategies', 2, 4, '2024-11-15 09:00:00+05', '2024-11-15 17:00:00+05', 'chat3', '2024-10-15 09:00:00+05', '2024-11-10 23:59:00+05', 50),
    ('CLOSED', 'Advanced Python', 'Python Advanced Workshop', 3, 4, '2024-12-05 10:00:00+05', '2024-12-05 18:00:00+05', 'chat4', '2024-11-05 10:00:00+05', '2024-11-30 23:59:00+05', 20),
    ('ACTIVE', 'Web Development Basics', 'Web Dev Workshop', 1, 4, '2024-10-25 10:00:00+05', '2024-10-25 18:00:00+05', 'chat5', '2024-10-01 10:00:00+05', '2024-10-20 23:59:00+05', 40);

-- Добавление 5 студентов к мероприятиям в таблицу events_students
INSERT INTO events_students (student_id, event_id, student_status)
VALUES
    (3, 1, 'sent_personal_info'),
    (3, 2, 'added_in_chat'),
    (5, 1, 'started_event'),
    (5, 3, 'ended_event'),
    (3, 4, 'deleted_from_event');

-- Добавление 5 кураторов к мероприятиям в таблицу events_curators
INSERT INTO events_curators (curator_id, event_id)
VALUES
    (2, 1),
    (2, 2),
    (2, 3),
    (2, 4),
    (2, 5);

-- Добавление 5 сообщений в таблицу messages
INSERT INTO messages (event_id, message_text)
VALUES
    (1, 'Welcome to the Java Basics Workshop!'),
    (2, 'The Data Science Bootcamp will start soon.'),
    (3, 'The Marketing Strategies event is now closed.'),
    (4, 'Advanced Python Workshop materials are available.'),
    (5, 'Web Development Basics has started.');

ALTER TABLE events ALTER COLUMN event_start_date TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE events ALTER COLUMN event_end_date TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE events ALTER COLUMN enrollment_start_date TYPE TIMESTAMP WITH TIME ZONE;
ALTER TABLE events ALTER COLUMN enrollment_end_date TYPE TIMESTAMP WITH TIME ZONE;


