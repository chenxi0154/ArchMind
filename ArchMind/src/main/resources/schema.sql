-- 本地开发用 SQLite 建表脚本（接入 MySQL/PostgreSQL 后可替换为对应方言）
CREATE TABLE IF NOT EXISTS "user" (
    id              INTEGER PRIMARY KEY,
    username        VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    email           VARCHAR(255),
    phone           VARCHAR(255),
    create_time     TEXT,
    update_time     TEXT,
    last_login_time TEXT,
    last_login_ip   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS "project" (
    id          INTEGER PRIMARY KEY,
    user_id     INTEGER,
    name        VARCHAR(255),
    description TEXT,
    language    VARCHAR(64),
    framework   VARCHAR(64),
    git_url     VARCHAR(512),
    version     VARCHAR(64),
    status      VARCHAR(32),
    create_time TEXT
);

CREATE TABLE IF NOT EXISTS "project_source" (
    id              INTEGER PRIMARY KEY,
    project_id      INTEGER,
    source_type     VARCHAR(32),
    content         TEXT,
    file_id         INTEGER,
    analysis_status VARCHAR(32),
    create_time     TEXT
);

CREATE TABLE IF NOT EXISTS "file" (
    id          INTEGER PRIMARY KEY,
    project_id  INTEGER,
    parent_id   INTEGER,
    file_name   VARCHAR(255),
    file_path   VARCHAR(1024),
    file_type   VARCHAR(32),
    file_size   INTEGER,
    language    VARCHAR(64),
    hash        VARCHAR(128),
    create_time TEXT
);
