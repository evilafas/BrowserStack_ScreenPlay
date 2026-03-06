-- Crear el schema 'healenium' que requiere el backend
CREATE SCHEMA IF NOT EXISTS healenium;

-- Otorgar todos los privilegios sobre el schema al usuario
GRANT ALL ON SCHEMA healenium TO healenium_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA healenium TO healenium_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA healenium TO healenium_user;

-- Configurar el search_path del usuario
ALTER USER healenium_user SET search_path TO healenium, public;
