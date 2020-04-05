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
