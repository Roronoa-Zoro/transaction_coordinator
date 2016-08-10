/*
Navicat MySQL Data Transfer

Source Server         : 对账系统-local
Source Server Version : 50548
Source Host           : localhost:3306
Source Database       : transaction_management

Target Server Type    : MYSQL
Target Server Version : 50548
File Encoding         : 65001

Date: 2016-08-10 14:54:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `transaction_participants`
-- ----------------------------
DROP TABLE IF EXISTS `transaction_participants`;
CREATE TABLE `transaction_participants` (
  `participants_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '事务参与者主键ID',
  `trx_id` int(11) NOT NULL COMMENT '主事务ID',
  `participants_state` smallint(6) NOT NULL COMMENT '事务状态',
  `participants_callback` varchar(100) NOT NULL COMMENT '参与者事务回查接口,用于异常情况确认最终提交还是回滚状态',
  `participants_submit_callback` varchar(100) NOT NULL COMMENT '参与者事务提交接口',
  `participants_rollback_callback` varchar(100) NOT NULL COMMENT '参与者事务回滚接口',
  `participants_create_time` datetime NOT NULL COMMENT '生成时间',
  `participants_update_time` datetime NOT NULL COMMENT '更新时间',
  `participants_version` tinyint(4) NOT NULL COMMENT 'version',
  `participants_args` varchar(300) DEFAULT NULL,
  `participants_invoke_state` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`participants_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of transaction_participants
-- ----------------------------
