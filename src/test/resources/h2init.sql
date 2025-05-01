CREATE TABLE IF NOT EXISTS tb_projects (
    p_id INT PRIMARY KEY AUTO_INCREMENT,
    p_name VARCHAR(100),
    p_description VARCHAR(1000),
    p_start_date DATE,
    p_end_date DATE,
    p_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    p_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS tb_subprojects (
    sp_id INT PRIMARY KEY AUTO_INCREMENT,
    sp_p_id INT,
    sp_name VARCHAR(500),
    sp_description VARCHAR(255),
    sp_start_date DATE,
    sp_end_date DATE,
    sp_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sp_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sp_p_id) REFERENCES tb_projects(p_id)
    );

CREATE TABLE IF NOT EXISTS tb_tasks (
    ts_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ts_sp_id INT,
    ts_name VARCHAR(500),
    ts_description VARCHAR(1000),
    ts_estimated_hours DECIMAL(5,2),
    ts_actual_hours DECIMAL(5,2),
    ts_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ts_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ts_sp_id) REFERENCES tb_subprojects(sp_id)
    );

DELETE FROM tb_tasks;
DELETE FROM tb_subprojects;
DELETE FROM tb_projects;

INSERT INTO tb_projects (p_name, p_description, p_start_date, p_end_date)
VALUES ('Test Project', 'Integration test project', '2025-01-01', '2025-02-01');

INSERT INTO tb_subprojects (sp_p_id, sp_name, sp_description, sp_start_date, sp_end_date)
VALUES (
           (SELECT p_id FROM tb_projects WHERE p_name='Test Project'),
           'Test SubProject', 'Integration test subproject', '2025-01-01', '2025-02-01'
       );

INSERT INTO tb_tasks (ts_sp_id, ts_name, ts_description, ts_estimated_hours, ts_actual_hours)
VALUES (
           (SELECT sp_id FROM tb_subprojects WHERE sp_name='Test SubProject'),
           'Test Task', 'Integration test task', 50.00, 45.00
       );

