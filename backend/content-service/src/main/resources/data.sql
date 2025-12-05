INSERT INTO questions (title, difficulty_level, scenario_description, thumbnail_url, tags) VALUES
('Scalable web app on AWS', 'MEDIUM', 'ALB + ECS Fargate + RDS', '', 'ecs,alb,rds,vpc'),
('Data lake ingestion pipeline', 'HARD', 'S3 + Glue + Athena', '', 's3,glue,athena,etl');

INSERT INTO steps (question_id, step_order, content, service_icon) VALUES
(1, 1, 'Create VPC with public/private subnets', 'VPC'),
(1, 2, 'Deploy backend on ECS Fargate', 'ECS'),
(1, 3, 'Provision RDS PostgreSQL', 'RDS');
