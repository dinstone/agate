CREATE DATABASE IF NOT EXISTS `agate` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `agate`;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_app
-- ----------------------------
DROP TABLE IF EXISTS `t_app`;
CREATE TABLE `t_app` (
  `id` int NOT NULL AUTO_INCREMENT,
  `gwId` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `json` text NOT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_app
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_cluster
-- ----------------------------
DROP TABLE IF EXISTS `t_cluster`;
CREATE TABLE `t_cluster` (
  `id` int NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_cluster_code_name` (`code`,`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_cluster
-- ----------------------------
BEGIN;
INSERT INTO `t_cluster` (`id`, `code`, `name`, `createTime`, `updateTime`) VALUES (1, 'develop', '开发集群', '2023-10-20 13:26:19', '2023-10-20 13:26:19');
INSERT INTO `t_cluster` (`id`, `code`, `name`, `createTime`, `updateTime`) VALUES (2, 'testing', '测试集群', '2023-10-20 13:26:46', '2023-10-20 13:26:46');
INSERT INTO `t_cluster` (`id`, `code`, `name`, `createTime`, `updateTime`) VALUES (3, 'product', '生产集群', '2023-10-20 13:27:05', '2023-10-20 13:27:05');
COMMIT;

-- ----------------------------
-- Table structure for t_gateway
-- ----------------------------
DROP TABLE IF EXISTS `t_gateway`;
CREATE TABLE `t_gateway` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ccode` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `json` text NOT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_gateway_ccode_name` (`ccode`,`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_gateway
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_route
-- ----------------------------
DROP TABLE IF EXISTS `t_route`;
CREATE TABLE `t_route` (
  `id` int NOT NULL AUTO_INCREMENT,
  `appId` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT '0',
  `json` text NOT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_route
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `json` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of t_user
-- ----------------------------
BEGIN;
INSERT INTO `t_user` (`id`, `username`, `password`, `enabled`, `json`) VALUES (1, 'agate', '123456', b'1', NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;