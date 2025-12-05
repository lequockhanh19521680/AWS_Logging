INSERT INTO questions (title, difficulty_level, scenario_description, thumbnail_url) VALUES
('Build scalable web app on AWS', 'MEDIUM', 'Use ALB + ECS Fargate + RDS', ''),
('Data lake ingestion pipeline', 'HARD', 'Use S3 + Glue + Athena', '');

INSERT INTO steps (question_id, step_order, content, service_icon) VALUES
(1, 1, 'Create VPC with public/private subnets', 'VPC'),
(1, 2, 'Deploy backend on ECS Fargate', 'ECS'),
(1, 3, 'Provision RDS PostgreSQL', 'RDS');

