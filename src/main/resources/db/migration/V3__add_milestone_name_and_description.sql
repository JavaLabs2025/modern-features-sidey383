ALTER TABLE milestones ADD COLUMN name varchar(255);
ALTER TABLE milestones ADD COLUMN description varchar(255);
ALTER TABLE milestones ADD COLUMN project_id BIGINT NOT NULL REFERENCES projects(project_id);