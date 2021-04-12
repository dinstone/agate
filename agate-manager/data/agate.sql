CREATE TABLE t_cluster (
    id           INTEGER  PRIMARY KEY AUTOINCREMENT,
    code      	 STRING   NOT NULL,
    name         STRING   NOT NULL,
    createTime   DATETIME NOT NULL,
    updateTime   DATETIME NOT NULL
);

CREATE UNIQUE INDEX i_code_name ON t_cluster (
    code,
    name
);

CREATE TABLE t_gateway (
    id           INTEGER  PRIMARY KEY AUTOINCREMENT,
    name         STRING   NOT NULL,
    cluster      STRING   NOT NULL,
    remark       STRING,
    host         STRING,
    port         INT      NOT NULL,
    serverConfig STRING,
    clientConfig STRING,
    status       INT      NOT NULL DEFAULT (0),
    createTime   DATETIME NOT NULL,
    updateTime   DATETIME NOT NULL
);

CREATE UNIQUE INDEX i_cluster_name ON t_gateway (
    cluster,
    name
);

CREATE UNIQUE INDEX i_cluster_port ON t_gateway (
    cluster,
    port
);

CREATE TABLE t_api (
    arId      INTEGER  PRIMARY KEY AUTOINCREMENT,
    gwId      INTEGER  NOT NULL,
    name       STRING   NOT NULL,
    remark     STRING,
    status     INT      NOT NULL DEFAULT (0),
    request    STRING   NOT NULL,
    routing    STRING   NOT NULL,
    response   STRING,
    handlers   STRING,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);

CREATE TABLE DUAL (
	id       INTEGER PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE t_user (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username STRING  NOT NULL
                     UNIQUE,
    password STRING  NOT NULL,
    enabled  BOOLEAN NOT NULL
                     DEFAULT (1) 
);

insert into t_user(username,password,enabled) values('agate','123456',1);
