
-- === Projekter & Delprojekter ===

SELECT * FROM tb_projects;
SELECT * FROM tb_projects WHERE p_id = 1000;
SELECT * FROM tb_subprojects WHERE sp_p_id = 1000;

-- === Tasks ===

SELECT * FROM tb_tasks;
SELECT * FROM tb_tasks WHERE ts_sp_id = 2000;
SELECT * FROM tb_tasks
WHERE sp_start_date >= '2025-01-01' AND sp_end_date <= '2025-06-30';

-- === Medarbejdere & Kompetencer ===

SELECT * FROM db_employees.tb_employees;
SELECT s.sk_name
FROM db_employees.tb_employee_skills es
JOIN db_employees.tb_skills s ON es.emsk_sk_id = s.sk_id
WHERE es.emsk_em_id = 7000;

-- === Ressourcer ===

SELECT * FROM tb_resources;
SELECT r.re_name, tr.tsre_hours_used
FROM tb_tasks_resources tr
JOIN tb_resources r ON tr.tsre_re_id = r.re_id
WHERE tr.tsre_ts_id = 100000;

-- === Task-Medarbejder koblinger ===

SELECT e.em_first_name, e.em_last_name, te.tse_hours_worked
FROM tb_task_employees te
JOIN db_employees.tb_employees e ON te.tse_em_id = e.em_id
WHERE te.tse_ts_id = 100000;

-- === Projektsummering ===

SELECT p.p_name, SUM(t.ts_estimated_hours) AS total_estimeret, SUM(t.ts_actual_hours) AS total_faktisk
FROM tb_projects p
JOIN tb_subprojects sp ON p.p_id = sp.sp_p_id
JOIN tb_tasks t ON sp.sp_id = t.ts_sp_id
WHERE p.p_id = 1000
GROUP BY p.p_name;
