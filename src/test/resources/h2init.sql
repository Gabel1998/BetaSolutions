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

DROP TABLE IF EXISTS tb_subprojects;
CREATE TABLE tb_subprojects (
                                sp_id INT PRIMARY KEY AUTO_INCREMENT,
                                sp_p_id INT,
                                sp_name VARCHAR(500),
                                sp_description VARCHAR(255),
                                sp_start_date DATE,
                                sp_end_date DATE,
                                sp_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                sp_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS tb_tasks;
CREATE TABLE tb_tasks (
                          ts_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          ts_sp_id INT,
                          ts_name VARCHAR(500),
                          ts_description VARCHAR(1000),
                          ts_estimated_hours DECIMAL(5,2),
                          ts_actual_hours DECIMAL(5,2),
                          sp_start_date DATE,
                          sp_end_date DATE,
                          ts_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          ts_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert 1 projekt og 1 subprojekt til test
INSERT INTO tb_projects (p_name, p_description, p_start_date, p_end_date)
VALUES ('Test Projekt', 'Beskrivelse', '2025-01-01', '2025-06-01');

INSERT INTO tb_subprojects (sp_p_id, sp_name, sp_description, sp_start_date, sp_end_date)
VALUES (1, 'Subprojekt A', 'Test sub', '2025-01-10', '2025-02-10');
