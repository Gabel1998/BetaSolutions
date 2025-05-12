
USE db_employees;

-- Medarbejdere
INSERT INTO tb_employees (em_first_name, em_last_name, em_efficiency, em_max_weekly_hours)
VALUES
    ('Emil', 'Hansen', 1.10, 37.5),
    ('Sofie', 'Jensen', 0.95, 20),
    ('Mads', 'Larsen', 1.20, 15);

-- Kompetencer
INSERT INTO tb_skills (sk_name)
VALUES
    ('El-installation'),
    ('CAD-tegning'),
    ('Projektledelse');

-- Tilknyt kompetencer (matcher rækkefølge af medarbejdere og skills)
INSERT INTO tb_employee_skills (emsk_em_id, emsk_sk_id)
VALUES
    (7000, 8000),  -- Emil: El-installation
    (7001, 8001),  -- Sofie: CAD-tegning
    (7002, 8002);  -- Mads: Projektledelse
