USE db_employees;

-- Medarbejdere
INSERT INTO tb_employees (em_id, em_first_name, em_last_name, em_efficiency)
VALUES
    ('emil.hansen@firma.dk', 'Emil', 'Hansen', 1.10),
    ('sofie.jensen@firma.dk', 'Sofie', 'Jensen', 0.95),
    ('mads.larsen@firma.dk', 'Mads', 'Larsen', 1.20);

-- Kompetencer
INSERT INTO tb_skills (sk_name)
VALUES
    ('El-installation'),
    ('CAD-tegning'),
    ('Projektledelse');

-- Tilknyt kompetencer
INSERT INTO tb_employee_skills (emsk_em_id, emsk_sk_id)
VALUES
    ('emil.hansen@firma.dk', 1), -- El-installation
    ('sofie.jensen@firma.dk', 2), -- CAD-tegning
    ('mads.larsen@firma.dk', 3); -- Projektledelse
