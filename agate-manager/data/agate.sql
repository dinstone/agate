CREATE TABLE t_app (
    id           INTEGER  PRIMARY KEY AUTOINCREMENT,
    name         STRING   NOT NULL,
    cluster      STRING   NOT NULL,
    remarks      STRING,
    host         STRING,
    port         INT      NOT NULL,
    prefix       STRING   NOT NULL,
    serverConfig STRING,
    clientConfig STRING,
    createTime   DATETIME NOT NULL,
    updateTime   DATETIME NOT NULL
);

CREATE UNIQUE INDEX i_cluster_app ON t_app (
    cluster,
    name
);

CREATE UNIQUE INDEX i_cluster_port ON t_app (
    cluster,
    port
);

CREATE TABLE t_api (
    apiid      INTEGER  PRIMARY KEY AUTOINCREMENT,
    appId      INTEGER  NOT NULL,
    name       STRING   NOT NULL,
    remark     STRING,
    status     INT      NOT NULL,
    frontend   STRING   NOT NULL,
    backend    STRING   NOT NULL,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);

CREATE TABLE t_user (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username STRING  NOT NULL
                     UNIQUE,
    password STRING  NOT NULL,
    enabled  BOOLEAN NOT NULL
                     DEFAULT (1) 
);

