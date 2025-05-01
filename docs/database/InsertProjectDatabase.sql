USE db_projects;

-- Projekter
INSERT INTO tb_projects (p_name, p_description, p_start_date, p_end_date)
VALUES
    ('Grøn Energi', 'Projekt om grøn omstilling', '2025-01-01', '2025-06-30'),
    ('Byg Nyt HQ', 'Nyt hovedkontor i Aarhus', '2025-03-01', '2025-12-31');

-- Delprojekter
INSERT INTO tb_subprojects (sp_p_id, sp_name, sp_description, sp_start_date, sp_end_date)
VALUES
    (1, 'Solcelleanlæg', 'Opsætning af solceller', '2025-01-10', '2025-03-01'),
    (1, 'Elbil ladepunkter', 'Installation af ladestandere', '2025-03-05', '2025-04-15'),
    (2, 'Arkitekturplan', 'Design af bygning', '2025-03-10', '2025-05-20');

-- Opgaver
INSERT INTO tb_tasks (ts_sp_id, ts_name, ts_description, ts_estimated_hours, ts_actual_hours)
VALUES
    (1, 'Montering af solpaneler', 'Installer 100 paneler', 80.00, 85.50),
    (2, 'Trækning af kabler', 'El-installation', 60.00, 58.25),
    (3, 'Bygningsdesign', 'Udarbejdelse af tegninger', 100.00, 105.75);

-- Ressourcer
INSERT INTO tb_resources (re_name, re_co2_per_hour)
VALUES
    ('Elektriker', 1.2500),
    ('Ingeniør', 0.8000),
    ('Maskine – Lift', 3.2000);

-- Ressourcer brugt i opgaver
INSERT INTO tb_tasks_resources (tsre_ts_id, tsre_re_id, tsre_hours_used)
VALUES
    (1, 1, 40.00), -- Elektriker på opgave 1
    (2, 1, 35.00),
    (3, 2, 90.00);

-- Tildel medarbejdere (brug em_id fra næste database)
INSERT INTO tb_task_employees (tse_ts_id, tse_em_id, tse_hours_worked)
VALUES
    (1, 'emil.hansen@firma.dk', 42.00),
    (2, 'sofie.jensen@firma.dk', 38.00),
    (3, 'mads.larsen@firma.dk', 90.00);
