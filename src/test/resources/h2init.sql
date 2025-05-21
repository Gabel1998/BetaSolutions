DROP TABLE IF EXISTS tb_task_employees;


DROP TABLE IF EXISTS tb_tasks;
DROP TABLE IF EXISTS tb_subprojects;
DROP TABLE IF EXISTS tb_projects;

CREATE TABLE tb_projects (
                             p_id INT PRIMARY KEY AUTO_INCREMENT,
                             p_name VARCHAR(100),
                             p_description VARCHAR(1000),
                             p_start_date DATE,
                             p_end_date DATE,
                             p_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             p_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_subprojects (
                                sp_id INT PRIMARY KEY AUTO_INCREMENT,
                                sp_p_id INT,
                                sp_name VARCHAR(500),
                                sp_description VARCHAR(255),
                                start_date DATE,
                                end_date DATE,
                                sp_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                sp_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_tasks (
                          ts_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          ts_sp_id INT,
                          ts_name VARCHAR(500),
                          ts_description VARCHAR(1000),
                          ts_estimated_hours DECIMAL(5,2),
                          ts_actual_hours DECIMAL(5,2),
                          start_date DATE,
                          end_date DATE,
                          ts_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          ts_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_task_employees (
                                   tse_id INT PRIMARY KEY AUTO_INCREMENT,
                                   tse_ts_id BIGINT,
                                   tse_employee_id INT,
                                   FOREIGN KEY (tse_ts_id) REFERENCES tb_tasks(ts_id) ON DELETE CASCADE
);

-- Insert test data
INSERT INTO tb_projects (p_name, p_description, p_start_date, p_end_date)
VALUES ('Test Projekt', 'Beskrivelse', '2025-01-01', '2025-06-01');

INSERT INTO tb_subprojects (sp_p_id, sp_name, sp_description, start_date, end_date)
VALUES (1, 'Subprojekt A', 'Test sub', '2025-01-10', '2025-02-10');