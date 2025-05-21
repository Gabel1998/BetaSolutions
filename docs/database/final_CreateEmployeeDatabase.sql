
DROP DATABASE IF EXISTS db_employees;
CREATE DATABASE db_employees;
USE db_employees;

-- Medarbejdere
CREATE TABLE tb_employees (
                              em_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              em_first_name VARCHAR(100) NOT NULL,
                              em_last_name VARCHAR(100) NOT NULL,
                              em_username VARCHAR(100) UNIQUE NOT NULL,
                              em_password VARCHAR(255) NOT NULL,
                              em_efficiency DECIMAL(3,2),
                              em_max_weekly_hours DECIMAL(5,2),
                              em_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              em_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) AUTO_INCREMENT=7000;

-- Skills
CREATE TABLE tb_skills (
                           sk_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           sk_name VARCHAR(100) NOT NULL
) AUTO_INCREMENT=8000;

-- Kompetencer
CREATE TABLE tb_employee_skills (
                                    emsk_em_id BIGINT,
                                    emsk_sk_id BIGINT,
                                    PRIMARY KEY (emsk_em_id, emsk_sk_id),
                                    FOREIGN KEY (emsk_em_id) REFERENCES tb_employees(em_id),
                                    FOREIGN KEY (emsk_sk_id) REFERENCES tb_skills(sk_id)
);
