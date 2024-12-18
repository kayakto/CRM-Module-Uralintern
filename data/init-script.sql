--CREATE DATABASE internships;
\c internships

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
    role_enum VARCHAR(20) DEFAULT 'STUDENT',
    competencies TEXT NOT NULL
);

-- Создание таблицы event
CREATE TABLE IF NOT EXISTS events (
    id SERIAL PRIMARY KEY,
    condition VARCHAR(20) NOT NULL DEFAULT 'PREPARATION',
    description_text TEXT,
    title VARCHAR(255) NOT NULL,
    admin_id INT REFERENCES users_info(id) ON DELETE SET NULL,
    manager_id INT REFERENCES users_info(id) ON DELETE SET NULL,
    event_start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    event_end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    chat_url VARCHAR(255),
    enrollment_start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    enrollment_end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    number_seats_students INT NOT NULL,
    number_seats_curators INT DEFAULT 10
);

-- Создание таблицы event_student
CREATE TABLE IF NOT EXISTS events_students (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    student_status VARCHAR(50) DEFAULT 'SENT_PERSONAL_INFO'
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
    edit_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);


--CREATE USER crm_admin WITH PASSWORD 'bitrix24';

-- Заполнение таблицы users_info
INSERT INTO users_info (first_name, last_name, surname, email, sign, telegram_url, vk_url, role_enum, competencies)
VALUES
('John', 'Doe', 'A', 'john.doe@example.com', 'signature1', 'https://t.me/johndoe', 'https://vk.com/johndoe', 'STUDENT', 'Competence1, Competence2'),
('Jane', 'Smith', 'B', 'jane.smith@example.com', 'signature2', 'https://t.me/janesmith', 'https://vk.com/janesmith', 'CURATOR', 'Competence3, Competence4'),
('Alice', 'Johnson', 'C', 'alice.j@example.com', 'signature3', 'https://t.me/alicej', 'https://vk.com/alicej', 'MANAGER', 'Competence5'),
('Bob', 'Williams', 'D', 'bob.w@example.com', 'signature4', 'https://t.me/bobw', 'https://vk.com/bobw', 'ADMIN', 'Competence6, Competence7'),
('Charlie', 'Brown', 'E', 'charlie.b@example.com', 'signature5', 'https://t.me/charlieb', NULL, 'STUDENT', 'Competence8');

-- Заполнение таблицы events
INSERT INTO events (condition, description_text, title, admin_id, manager_id, event_start_date, event_end_date, chat_url, enrollment_start_date, enrollment_end_date, number_seats_students)
VALUES
    ('PREPARATION', 'Description for Event 1', 'Event 1', 4, 3, '2024-11-01T09:00:00+00', '2024-11-01T17:00:00+00', 'https://chat.url/event1', '2024-10-01T09:00:00+00', '2024-10-30T17:00:00+00', 100),
    ('REGISTRATION_OPEN', 'Description for Event 2', 'Event 2', 4, 2, '2024-11-05T09:00:00+00', '2024-11-05T17:00:00+00', 'https://chat.url/event2', '2024-10-05T09:00:00+00', '2024-10-29T17:00:00+00', 50),
    ('NO_SEATS', 'Description for Event 3', 'Event 3', 4, 1, '2024-12-01T09:00:00+00', '2024-12-01T17:00:00+00', NULL, '2024-11-01T09:00:00+00', '2024-11-30T17:00:00+00', 0),
    ('IN_PROGRESS', 'Description for Event 4', 'Event 4', 4, 1, '2024-12-05T09:00:00+00', '2024-12-05T17:00:00+00', 'https://chat.url/event4', '2024-11-05T09:00:00+00', '2024-11-30T17:00:00+00', 75),
    ('HIDDEN', 'Description for Event 5', 'Event 5', 4, 2, '2024-12-10T09:00:00+00', '2024-12-10T17:00:00+00', NULL, '2024-11-10T09:00:00+00', '2024-12-01T17:00:00+00', 60);

-- Заполнение таблицы events_students
INSERT INTO events_students (student_id, event_id, student_status)
VALUES
(1, 1, 'SENT_PERSONAL_INFO'),
(2, 1, 'SENT_PERSONAL_INFO'),
(3, 2, 'SENT_PERSONAL_INFO'),
(4, 3, 'SENT_PERSONAL_INFO'),
(5, 4, 'SENT_PERSONAL_INFO');

-- Заполнение таблицы events_curators
INSERT INTO events_curators (curator_id, event_id)
VALUES
(2, 1),
(2, 2),
(2, 3),
(4, 4),
(4, 5);

-- Заполнение таблицы messages
INSERT INTO messages (event_id, message_text, edit_date)
VALUES
(1, 'Welcome to Event 1!', '2024-10-01T09:15:00+00'),
(1, 'Event 1 reminder', '2024-10-01T10:15:00+00'),
(2, 'Welcome to Event 2!', '2024-10-05T09:15:00+00'),
(3, 'Event 3 instructions', '2024-11-01T09:15:00+00'),
(4, 'Details about Event 4', '2024-12-05T09:15:00+00');

ALTER TABLE events_curators
ADD COLUMN curator_status VARCHAR(50) DEFAULT 'SENT_PERSONAL_INFO';

ALTER TABLE events_curators
ADD CONSTRAINT unique_curator_event UNIQUE (curator_id, event_id);

CREATE TABLE IF NOT EXISTS event_groups (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    curator_id INT NOT NULL,
    FOREIGN KEY (event_id, curator_id) REFERENCES events_curators(event_id, curator_id) ON DELETE CASCADE,
    UNIQUE(event_id, curator_id)
);

ALTER TABLE events_students
ADD COLUMN group_id INT REFERENCES event_groups(id) ON DELETE SET NULL;

GRANT ALL PRIVILEGES ON DATABASE internships TO crm_admin;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO crm_admin;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO crm_admin;

GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO crm_admin;

ALTER TABLE events ALTER COLUMN number_seats_curators SET NOT NULL;

ALTER TABLE events DROP COLUMN number_seats_curators;

DROP TABLE messages;

-- Создание таблицы messages
CREATE TABLE messages (
    id SERIAL PRIMARY KEY, -- Автоматически увеличивающийся ID
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE, -- Ссылка на мероприятие
    text TEXT NOT NULL,       -- Текст сообщения
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACCEPTED', 'DECLINED')), -- Статус сообщения
    edit_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP -- Дата редактирования
);

-- Создание таблицы notifications
CREATE TABLE notifications (
    id SERIAL PRIMARY KEY, -- Автоматически увеличивающийся ID
    user_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,  -- Ссылка на пользователя
    message_id INT NOT NULL REFERENCES messages (id) ON DELETE CASCADE, -- Ссылка на сообщение
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP -- Дата отправки уведомления
);


-- Вставка мероприятия со статусом "REGISTRATION_OPEN"
INSERT INTO events (condition, description_text, title, enrollment_start_date, enrollment_end_date, event_start_date, event_end_date, number_seats_students, chat_url)
VALUES ('REGISTRATION_OPEN', 'Описание мероприятия: Java Workshop', 'Java Workshop', NOW() - INTERVAL '1 day', NOW() + INTERVAL '15 days', NOW() + INTERVAL '16 days', NOW() + INTERVAL '30 days', 50, 'https://chat.url/java-workshop');

-- Проверка вставки мероприятия
SELECT id FROM events WHERE title = 'Java Workshop'; -- Вернет ID мероприятия

-- Вставка пользователей
INSERT INTO users_info (first_name, last_name, email, sign, telegram_url, role_enum, competencies)
VALUES
    ('Alice', 'Johnson', 'alice@example.com', 'Student Signature', 't.me/alice_j', 'STUDENT', 'Java, Spring Boot'),
    ('Bob', 'Smith', 'bob@example.com', 'Student Signature', 't.me/bob_smith', 'STUDENT', 'Microservices, PostgreSQL'),
    ('Charlie', 'Brown', 'charlie@example.com', 'Curator Signature', 't.me/charlie_b', 'CURATOR', 'Leadership, Event Organization');

-- Проверка вставки пользователей
SELECT id FROM users_info;



