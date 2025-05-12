
DROP DATABASE IF EXISTS db_projects;
CREATE DATABASE db_projects;
USE db_projects;

-- Projekter
CREATE TABLE tb_projects (
    p_id INT PRIMARY KEY AUTO_INCREMENT,
    p_name VARCHAR(100) NOT NULL,
    p_description VARCHAR(1000),
    p_start_date DATE,
    p_end_date DATE,
    p_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    p_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) AUTO_INCREMENT=1000;

-- Delprojekter
CREATE TABLE tb_subprojects (
    sp_id INT PRIMARY KEY AUTO_INCREMENT,
    sp_p_id INT,
    sp_name VARCHAR(500) NOT NULL,
    sp_description VARCHAR(255),
    start_date DATE,
    end_date DATE,
    sp_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sp_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (sp_p_id) REFERENCES tb_projects(p_id)
) AUTO_INCREMENT=2000;

-- Tasks
CREATE TABLE tb_tasks (
    ts_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    ts_sp_id INT,
    ts_name VARCHAR(500) NOT NULL,
    ts_description VARCHAR(1000),
    ts_estimated_hours DECIMAL(5,2),
    ts_actual_hours DECIMAL(5,2),
    start_date DATE,
    end_date DATE,
    project_id INT,
    ts_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ts_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ts_sp_id) REFERENCES tb_subprojects(sp_id)
) AUTO_INCREMENT=100000;

-- Task employees
CREATE TABLE tb_task_employees (
    tse_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tse_ts_id BIGINT,
    tse_em_id BIGINT,
    tse_hours_worked DECIMAL(5,2),
    FOREIGN KEY (tse_ts_id) REFERENCES tb_tasks(ts_id)
) AUTO_INCREMENT=500000;

-- Ressourcer til opgaver JOIN tabel
CREATE TABLE tb_tasks_resources (
    tsre_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tsre_ts_id BIGINT,
    tsre_re_id BIGINT,
    tsre_hours_used DECIMAL(5,2),
    FOREIGN KEY (tsre_ts_id) REFERENCES tb_tasks(ts_id)
) AUTO_INCREMENT=600000;

-- Ressourcer tabel
CREATE TABLE tb_resources (
    re_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    re_name VARCHAR(55) NOT NULL,
    re_co2_per_hour DECIMAL(8,4),
    re_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) AUTO_INCREMENT=3000;
