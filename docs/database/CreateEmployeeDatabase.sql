DROP DATABASE IF EXISTS db_employees;
CREATE DATABASE db_employees;
USE db_employees;

-- Medarbejdere
CREATE TABLE tb_employees (
                              em_id VARCHAR(500) PRIMARY KEY,
                              em_first_name VARCHAR(100),
                              em_last_name VARCHAR(100),
                              em_efficiency DECIMAL(5,2),
                              em_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              em_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Skills
CREATE TABLE tb_skills (
                           sk_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           sk_name VARCHAR(100)
);

-- Kompetencer
CREATE TABLE tb_employee_skills (
                                    emsk_em_id VARCHAR(500),
                                    emsk_sk_id BIGINT,
                                    PRIMARY KEY (emsk_em_id, emsk_sk_id),
                                    FOREIGN KEY (emsk_em_id) REFERENCES tb_employees(em_id),
                                    FOREIGN KEY (emsk_sk_id) REFERENCES tb_skills(sk_id)
);
