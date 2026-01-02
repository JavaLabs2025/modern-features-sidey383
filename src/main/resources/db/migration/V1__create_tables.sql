-- Пользователи
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Проекты
CREATE TABLE projects (
    project_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    project_manager_id BIGINT NOT NULL REFERENCES users(user_id),
    team_lead_id BIGINT REFERENCES users(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Связь пользователей с проектами
CREATE TABLE project_users (
    project_id BIGINT NOT NULL REFERENCES projects(project_id),
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    project_role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (project_id, user_id)
);

-- Майлстоуны
CREATE TABLE milestones (
    milestone_id SERIAL PRIMARY KEY,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Связь проектов с майлстоунами
CREATE TABLE project_milestones (
    project_id BIGINT NOT NULL REFERENCES projects(project_id),
    milestone_id BIGINT NOT NULL REFERENCES milestones(milestone_id),
    PRIMARY KEY (project_id, milestone_id)
);

-- Тикеты
CREATE TABLE tickets (
    ticket_id SERIAL PRIMARY KEY,
    milestone_id BIGINT NOT NULL REFERENCES milestones(milestone_id),
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    name VARCHAR(200) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Разработчики тикетов
CREATE TABLE ticket_developers (
    ticket_id BIGINT NOT NULL REFERENCES tickets(ticket_id),
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    PRIMARY KEY (ticket_id, user_id)
);

-- Сообщения об ошибках
CREATE TABLE bug_reports (
    bug_id SERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(project_id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Разработчики для исправления ошибок
CREATE TABLE bug_developers (
    bug_id BIGINT NOT NULL REFERENCES bug_reports(bug_id),
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    PRIMARY KEY (bug_id, user_id)
);