--CREATE DATABASE internships;
\c internships

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
    competencies TEXT
);

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
    number_seats_students INT NOT NULL
);

CREATE TABLE IF NOT EXISTS events_curators (
    id SERIAL PRIMARY KEY,
    curator_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    curator_status VARCHAR(50) DEFAULT 'SENT_PERSONAL_INFO',
    UNIQUE (curator_id, event_id)
);

CREATE TABLE IF NOT EXISTS event_groups (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    curator_id INT NOT NULL,
    FOREIGN KEY (event_id, curator_id) REFERENCES events_curators(event_id, curator_id) ON DELETE CASCADE,
    UNIQUE (event_id, curator_id)
);

CREATE TABLE IF NOT EXISTS events_students (
    id SERIAL PRIMARY KEY,
    student_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    student_status VARCHAR(50) DEFAULT 'SENT_PERSONAL_INFO',
    group_id INT REFERENCES event_groups(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    event_id INT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACCEPTED', 'DECLINED', 'SENT')),
    edit_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE,
    message_id INT NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS invitations (
    id SERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    role_enum VARCHAR(20),
    expiration_date DATE NOT NULL,
    used BOOLEAN NOT NULL,
    author_id INT NOT NULL REFERENCES users_info(id) ON DELETE CASCADE
);


INSERT INTO users_info (first_name, last_name, email, sign, telegram_url, role_enum, competencies)
VALUES
    ('STUDENT', 'Test', 'student@mail.ru', '$2a$10$RdL7hBEysSncR8i1iPWh.uww2h5SA4yV/Ao4VwskzgZJ7jtc40PmO', 't.me/student', 'STUDENT', 'Java, Spring Boot'),
    ('CURATOR', 'TEST', 'curator@mail.ru', '$2a$10$oHSxJ9Gup3jdVaIkNcfWluMY/hlH/yZHq.wsJdRCHiP2hXNiMENQa', 't.me/curator', 'CURATOR', 'Microservices, PostgreSQL'),
    ('MANAGER', 'TEST', 'manager@mail.ru', '$2a$10$0n7IAwS81oANxZBA1smbDOFordm2ekzK8K2aWvANVxN4ZrMfI0WL6', 't.me/manager', 'MANAGER', 'Leadership, Event Organization'),
    ('ADMIN', 'TEST', 'admin@mail.ru', '$2a$10$yBgtUtwl8KBdyXv56PjnX.IRf.X0S6WuKY2ilzV05r4Y/3kfTx1le', 't.me/admin', 'ADMIN', 'Leadership, Event Organization');

ALTER TABLE events
ADD COLUMN has_test BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE events_tests (
    id SERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    test_url TEXT NOT NULL,
    CONSTRAINT fk_event_test_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);


CREATE TABLE student_test_results (
    id SERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    test_id BIGINT NOT NULL,
    passed BOOLEAN NOT NULL,
    score INT NOT NULL,
    CONSTRAINT fk_student_test_result_student FOREIGN KEY (student_id) REFERENCES users_info(id) ON DELETE CASCADE,
    CONSTRAINT fk_student_test_result_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);


GRANT ALL PRIVILEGES ON DATABASE internships TO crm_admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO crm_admin;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO crm_admin;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO crm_admin;