CREATE DATABASE IF NOT EXISTS wellness_db;
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'admin') THEN
        CREATE ROLE admin WITH LOGIN PASSWORD 'admin';
    ELSE
            ALTER ROLE admin WITH PASSWORD 'admin';
    END IF;
END
$$;
GRANT ALL PRIVILEGES ON DATABASE wellness_db TO admin;