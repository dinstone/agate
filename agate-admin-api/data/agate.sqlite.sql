--
-- SQLiteStudio v3.4.4 生成的文件，周四 10月 19 20:36:07 2023
--
-- 所用的文本编码：UTF-8
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- 表：DUAL
DROP TABLE IF EXISTS DUAL;

CREATE TABLE IF NOT EXISTS DUAL (
    id INTEGER PRIMARY KEY AUTOINCREMENT
);


-- 表：t_app
DROP TABLE IF EXISTS t_app;

CREATE TABLE IF NOT EXISTS t_app (
    id         INTEGER  PRIMARY KEY AUTOINCREMENT,
    gwId       INTEGER  NOT NULL
                        REFERENCES t_gateway (id),
    name       STRING   NOT NULL,
    json       TEXT     NOT NULL,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);


-- 表：t_cluster
DROP TABLE IF EXISTS t_cluster;

CREATE TABLE IF NOT EXISTS t_cluster (
    id         INTEGER  PRIMARY KEY AUTOINCREMENT,
    code       STRING   NOT NULL,
    name       STRING   NOT NULL,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);


-- 表：t_gateway
DROP TABLE IF EXISTS t_gateway;

CREATE TABLE IF NOT EXISTS t_gateway (
    id         INTEGER  PRIMARY KEY AUTOINCREMENT,
    ccode      STRING   NOT NULL
                        REFERENCES t_cluster (code),
    name       STRING   NOT NULL,
    status     INT      NOT NULL
                        DEFAULT (0),
    json       TEXT     NOT NULL,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);


-- 表：t_route
DROP TABLE IF EXISTS t_route;

CREATE TABLE IF NOT EXISTS t_route (
    id         INTEGER  PRIMARY KEY AUTOINCREMENT,
    appId      INTEGER  NOT NULL
                        REFERENCES t_app (id),
    name       STRING   NOT NULL,
    status     INT      NOT NULL
                        DEFAULT (0),
    json       TEXT     NOT NULL,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);


-- 表：t_user
DROP TABLE IF EXISTS t_user;

CREATE TABLE IF NOT EXISTS t_user (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username STRING  NOT NULL
                     UNIQUE,
    password STRING  NOT NULL,
    enabled  BOOLEAN NOT NULL
                     DEFAULT (1) 
);


-- 索引：i_cluster_name
DROP INDEX IF EXISTS i_cluster_name;

CREATE UNIQUE INDEX IF NOT EXISTS i_cluster_name ON t_gateway (
    ccode,
    name
);


-- 索引：i_code_name
DROP INDEX IF EXISTS i_code_name;

CREATE UNIQUE INDEX IF NOT EXISTS i_code_name ON t_cluster (
    code,
    name
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
