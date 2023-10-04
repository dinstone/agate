--
-- SQLiteStudio v3.4.4 生成的文件，周三 8月 2 19:35:41 2023
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

INSERT INTO t_app (
                      id,
                      gwId,
                      name,
                      json,
                      createTime,
                      updateTime
                  )
                  VALUES (
                      1,
                      1,
                      'PC应用',
                      '{"id":1,"name":"PC应用","gwId":1,"domain":"","prefix":"/*"}',
                      1690966243337,
                      1690966253238
                  );

INSERT INTO t_app (
                      id,
                      gwId,
                      name,
                      json,
                      createTime,
                      updateTime
                  )
                  VALUES (
                      2,
                      1,
                      'APP应用',
                      '{"name":"APP应用","gwId":1,"domain":"","prefix":"/api/*"}',
                      1690966919692,
                      1690966919692
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

INSERT INTO t_cluster (
                          id,
                          code,
                          name,
                          createTime,
                          updateTime
                      )
                      VALUES (
                          1,
                          'product',
                          '生产集群',
                          1690207733267,
                          1690792174695
                      );

INSERT INTO t_cluster (
                          id,
                          code,
                          name,
                          createTime,
                          updateTime
                      )
                      VALUES (
                          2,
                          'develop',
                          '开发集群',
                          1690805121082,
                          1690805121082
                      );

INSERT INTO t_cluster (
                          id,
                          code,
                          name,
                          createTime,
                          updateTime
                      )
                      VALUES (
                          3,
                          'testing',
                          '测试集群',
                          1690897289773,
                          1690897289773
                      );


-- 表：t_gateway
DROP TABLE IF EXISTS t_gateway;

CREATE TABLE IF NOT EXISTS t_gateway (
    id         INTEGER  PRIMARY KEY AUTOINCREMENT,
    cluster    STRING   NOT NULL
                        REFERENCES t_cluster (code),
    name       STRING   NOT NULL,
    status     INT      NOT NULL
                        DEFAULT (0),
    json       TEXT     NOT NULL,
    createTime DATETIME NOT NULL,
    updateTime DATETIME NOT NULL
);

INSERT INTO t_gateway (
                          id,
                          cluster,
                          name,
                          status,
                          json,
                          createTime,
                          updateTime
                      )
                      VALUES (
                          1,
                          'product',
                          '生产默认网关',
                          1,
                          '{"id":1,"cluster":"product","name":"生产默认网关","remark":"生产默认网关","status":0,"host":"","port":1000,"serverConfig":"","clientConfig":""}',
                          1690802826445,
                          1690966760691
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

INSERT INTO t_route (
                        id,
                        appId,
                        name,
                        status,
                        json,
                        createTime,
                        updateTime
                    )
                    VALUES (
                        1,
                        1,
                        'ReverseProxy',
                        1,
                        '{"id":1,"status":0,"appId":1,"name":"ReverseProxy","remark":"Http Reverse Proxy","frontend":{"path":"/*","method":"","consumes":"","produces":""},"backend":{"type":0,"algorithm":0,"timeout":2000,"method":"","path":"/*","registry":"{\"type\": \"consul\",\"host\": \"192.168.1.120\",\"port\": 8500}","connection":"{\"connectTimeout\": 1000,\"idleTimeout\": 300,\"maxPoolSize\": 50,\"maxWaitQueueSize\": 1000}","urls":["https://cn.bing.com"]}}',
                        1690808553424,
                        1690975557453
                    );

INSERT INTO t_route (
                        id,
                        appId,
                        name,
                        status,
                        json,
                        createTime,
                        updateTime
                    )
                    VALUES (
                        3,
                        2,
                        'ServiceDiscovery',
                        0,
                        '{"status":0,"appId":2,"name":"ServiceDiscovery","remark":"Http Service Discovery","frontend":{"path":"/us/(?<path>.*)","method":"","consumes":"","produces":""},"backend":{"type":1,"algorithm":0,"timeout":1200,"method":"","path":"/:path","registry":"{\"type\": \"consul\",\"host\": \"192.168.1.120\",\"port\": 8500}","connection":" {\"connectTimeout\": 500,\"idleTimeout\": 300,\"maxPoolSize\": 100,\"maxWaitQueueSize\": 1000}","urls":["http://user-service"],"params":[{"feParamName":"path","feParamType":"PATH","beParamName":"path","beParamType":"PATH"}]}}',
                        1690967226473,
                        1690967724718
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

INSERT INTO t_user (
                       id,
                       username,
                       password,
                       enabled
                   )
                   VALUES (
                       1,
                       'agate',
                       123456,
                       1
                   );


-- 索引：i_cluster_name
DROP INDEX IF EXISTS i_cluster_name;

CREATE UNIQUE INDEX IF NOT EXISTS i_cluster_name ON t_gateway (
    cluster,
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
