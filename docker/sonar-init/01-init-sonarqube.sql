-- ═══════════════════════════════════════════════════════
-- Inicialización de base de datos para SonarQube
-- Se ejecuta al crear el contenedor sonarqube-db
-- ═══════════════════════════════════════════════════════

-- Crear base de datos sonarqube
CREATE DATABASE sonarqube
    WITH OWNER = sonar
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Conectar y otorgar privilegios
\connect sonarqube;

GRANT ALL PRIVILEGES ON DATABASE sonarqube TO sonar;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO sonar;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO sonar;

ALTER USER sonar WITH SUPERUSER;
