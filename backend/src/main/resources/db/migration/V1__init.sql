CREATE TABLE IF NOT EXISTS users (
  id UUID PRIMARY KEY,
  email VARCHAR(255),
  provider VARCHAR(32),
  provider_id VARCHAR(255),
  role VARCHAR(32),
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
  id SERIAL PRIMARY KEY,
  title VARCHAR(255),
  difficulty_level VARCHAR(32),
  scenario_description TEXT,
  thumbnail_url TEXT
);

CREATE TABLE IF NOT EXISTS steps (
  id SERIAL PRIMARY KEY,
  question_id INTEGER REFERENCES questions(id) ON DELETE CASCADE,
  step_order INTEGER,
  content TEXT,
  service_icon VARCHAR(128)
);

