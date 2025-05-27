-- OBS MIDLERTIDIG QUERY --
CREATE USER 'betasolutions'@'localhost' IDENTIFIED BY 'StrongPassword@123';

-- GIV ROOT PRIV. PÅ BEGGE DB
GRANT ALL PRIVILEGES ON db_projects.* TO 'betasolutions'@'localhost';
GRANT ALL PRIVILEGES ON db_employees.* TO 'betasolutions'@'localhost';

FLUSH PRIVILEGES;