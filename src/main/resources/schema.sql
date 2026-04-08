-- Enable Vector Extension
CREATE EXTENSION IF NOT EXISTS vector;

-- User Table with Initial Data (password is '123456' hashed with BCrypt)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert Default Accounts:
-- Admin: admin / 123456
-- Support: service / 123456
-- User: test / 123456
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'ADMIN'),
('service', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'SUPPORT'),
('test', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'USER'),
('superadmin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'ADMIN')
ON CONFLICT (username) DO NOTHING;

-- Chat History Table
CREATE TABLE IF NOT EXISTS chat_history (
    id BIGSERIAL PRIMARY KEY,
    session_id VARCHAR(64),
    role VARCHAR(20),
    content TEXT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_chat_session ON chat_history(session_id);

-- Associate chat history with user
ALTER TABLE chat_history ADD COLUMN IF NOT EXISTS user_id BIGINT;

-- Knowledge Base Table (Compatible with PgVectorEmbeddingStore)
-- Note: LangChain4j 0.36.2 hardcodes "embedding_id" as the primary key column name
CREATE TABLE IF NOT EXISTS knowledge_base (
    embedding_id UUID PRIMARY KEY,
    embedding vector(1536),
    text TEXT,
    metadata JSONB
);
ALTER TABLE knowledge_base RENAME COLUMN id TO embedding_id;