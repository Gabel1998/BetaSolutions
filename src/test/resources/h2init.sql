-- =======================================
-- H2 Init Script til Integrationstest
-- =======================================

-- 1) Drop alle tabeller hvis de findes
DROP TABLE IF EXISTS tb_employee_skills;
DROP TABLE IF EXISTS tb_skills;
DROP TABLE IF EXISTS tb_employees;
DROP TABLE IF EXISTS tb_tasks_resources;
DROP TABLE IF EXISTS tb_task_employees;
DROP TABLE IF EXISTS tb_tasks;
DROP TABLE IF EXISTS tb_subprojects;
DROP TABLE IF EXISTS tb_projects;
DROP TABLE IF EXISTS tb_resources;

-- 2) Opret Employee-schema
CREATE TABLE tb_employees (
                              em_id               BIGINT PRIMARY KEY AUTO_INCREMENT,
                              em_first_name       VARCHAR(100) NOT NULL,
                              em_last_name        VARCHAR(100) NOT NULL,
                              em_username         VARCHAR(100) NOT NULL UNIQUE,
                              em_password         VARCHAR(255) NOT NULL,
                              em_efficiency       DECIMAL(3,2),
                              em_max_weekly_hours DECIMAL(5,2),
                              em_created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              em_updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_skills (
                           sk_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
                           sk_name VARCHAR(100) NOT NULL
);

CREATE TABLE tb_employee_skills (
                                    emsk_em_id BIGINT,
                                    emsk_sk_id BIGINT,
                                    PRIMARY KEY (emsk_em_id, emsk_sk_id),
                                    FOREIGN KEY (emsk_em_id) REFERENCES tb_employees(em_id) ON DELETE CASCADE,
                                    FOREIGN KEY (emsk_sk_id) REFERENCES tb_skills(sk_id) ON DELETE CASCADE
);

-- 3) Opret Project-schema
CREATE TABLE tb_projects (
                             p_id         INT PRIMARY KEY AUTO_INCREMENT,
                             p_name       VARCHAR(100) NOT NULL,
                             p_description VARCHAR(1000),
                             p_start_date DATE,
                             p_end_date   DATE,
                             p_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             p_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_subprojects (
                                sp_id        INT PRIMARY KEY AUTO_INCREMENT,
                                sp_p_id      INT,
                                sp_name      VARCHAR(500) NOT NULL,
                                sp_description VARCHAR(255),
                                start_date   DATE,
                                end_date     DATE,
                                sp_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                sp_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (sp_p_id) REFERENCES tb_projects(p_id) ON DELETE CASCADE
);

CREATE TABLE tb_tasks (
                          ts_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
                          ts_sp_id         INT,
                          ts_name          VARCHAR(500) NOT NULL,
                          ts_description   VARCHAR(1000),
                          ts_estimated_hours DECIMAL(5,2),
                          ts_actual_hours  DECIMAL(5,2),
                          start_date       DATE,
                          end_date         DATE,
                          project_id       INT,
                          ts_created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          ts_updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (ts_sp_id) REFERENCES tb_subprojects(sp_id) ON DELETE CASCADE
);

CREATE TABLE tb_task_employees (
                                   tse_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   tse_ts_id       BIGINT,
                                   tse_em_id       BIGINT,
                                   tse_hours_worked DECIMAL(5,2),
                                   start_date      DATE,
                                   end_date        DATE,
                                   FOREIGN KEY (tse_ts_id) REFERENCES tb_tasks(ts_id) ON DELETE CASCADE,
                                   FOREIGN KEY (tse_em_id) REFERENCES tb_employees(em_id) ON DELETE CASCADE
);

CREATE TABLE tb_tasks_resources (
                                    tsre_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    tsre_ts_id      BIGINT,
                                    tsre_re_id      BIGINT,
                                    tsre_hours_used DECIMAL(5,2),
                                    FOREIGN KEY (tsre_ts_id) REFERENCES tb_tasks(ts_id) ON DELETE CASCADE,
                                    FOREIGN KEY (tsre_re_id) REFERENCES tb_resources(re_id) ON DELETE CASCADE
);

CREATE TABLE tb_resources (
                              re_id            BIGINT PRIMARY KEY AUTO_INCREMENT,
                              re_name          VARCHAR(55) NOT NULL,
                              re_co2_per_hour  DECIMAL(8,4),
                              re_created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4) Indsæt testdata fra MySQL-scripts
-- Employees & Skills
INSERT INTO tb_employees (em_id, em_first_name, em_last_name, em_username, em_password, em_efficiency, em_max_weekly_hours)
VALUES
    (7000, 'Emil', 'Hansen', 'emil.hansen', 'password123', 1.10, 37.5),
    (7001, 'Sofie', 'Jensen', 'sofie.jensen', 'password123', 0.95, 20),
    (7002, 'Mads',  'Larsen', 'mads.larsen',  'password123', 1.20, 15);
ALTER TABLE tb_employees ALTER COLUMN em_id RESTART WITH 7003;

INSERT INTO tb_skills (sk_id, sk_name)
VALUES
    (8000, 'El-installation'),
    (8001, 'CAD-tegning'),
    (8002, 'Projektledelse');
ALTER TABLE tb_skills ALTER COLUMN sk_id RESTART WITH 8003;

INSERT INTO tb_employee_skills (emsk_em_id, emsk_sk_id)
VALUES
    (7000, 8000),
    (7001, 8001),
    (7002, 8002);

-- Projects & Subprojects & Tasks
INSERT INTO tb_projects (p_id, p_name, p_description, p_start_date, p_end_date)
VALUES
    (1000, 'Grøn Energi', 'Projekt om grøn omstilling', '2025-01-01', '2025-06-30'),
    (1001, 'Byg Nyt HQ',  'Nyt hovedkontor i Aarhus', '2025-03-01', '2025-12-31');
ALTER TABLE tb_projects ALTER COLUMN p_id RESTART WITH 1002;

INSERT INTO tb_subprojects (sp_id, sp_p_id, sp_name, sp_description, start_date, end_date)
VALUES
    (2000, 1000, 'Solcelleanlæg',     'Opsætning af solceller',        '2025-01-10', '2025-03-01'),
    (2001, 1000, 'Elbil ladepunkter', 'Installation af ladestandere', '2025-03-05', '2025-04-15'),
    (2002, 1001, 'Arkitekturplan',    'Design af bygning',             '2025-03-10', '2025-05-20');
ALTER TABLE tb_subprojects ALTER COLUMN sp_id RESTART WITH 2003;

INSERT INTO tb_tasks (ts_id, ts_sp_id, ts_name, ts_description, ts_estimated_hours, ts_actual_hours, start_date, end_date)
VALUES
    (100000, 2000, 'Montering af solpaneler', 'Installer 100 paneler',  80.00,  85.50, '2025-01-10', '2025-03-01'),
    (100001, 2001, 'Trækning af kabler',       'El-installation',        60.00,  58.25, '2025-03-05', '2025-04-15'),
    (100002, 2002, 'Bygningsdesign',          'Udarbejdelse af tegninger',100.00,105.75,'2025-03-10','2025-05-20');
ALTER TABLE tb_tasks ALTER COLUMN ts_id RESTART WITH 100003;

-- Resources & assignments
INSERT INTO tb_resources (re_id, re_name, re_co2_per_hour)
VALUES
    (3000, 'Elektriker', 1.2500),
    (3001, 'Ingeniør',   0.8000),
    (3002, 'Maskine – Lift', 3.2000);
ALTER TABLE tb_resources ALTER COLUMN re_id RESTART WITH 3003;

INSERT INTO tb_tasks_resources (tsre_ts_id, tsre_re_id, tsre_hours_used)
VALUES
    (100000, 3000, 40.00),
    (100001, 3000, 35.00),
    (100002, 3001, 90.00);

INSERT INTO tb_task_employees (tse_ts_id, tse_em_id, tse_hours_worked, start_date, end_date)
VALUES
    (100000, 7000, 42.00, '2025-05-12', '2025-05-16'),
    (100001, 7001, 38.00, '2025-05-12', '2025-05-15'),
    (100002, 7002, 90.00, '2025-05-12', '2025-05-19'),
    (100000, 7000,  8.00, '2025-05-12', '2025-05-12'),
    (100001, 7001, 16.00, '2025-05-12', '2025-05-13');
ALTER TABLE tb_task_employees ALTER COLUMN tse_id RESTART WITH 6;
