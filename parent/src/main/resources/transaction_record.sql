/*
Navicat MySQL Data Transfer

Source Server         : 对账系统-local
Source Server Version : 50548
Source Host           : localhost:3306
Source Database       : transaction_management

Target Server Type    : MYSQL
Target Server Version : 50548
File Encoding         : 65001

Date: 2016-08-10 14:54:06
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `transaction_record`
-- ----------------------------
DROP TABLE IF EXISTS `transaction_record`;
CREATE TABLE transaction_record (
  trx_id int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  trx_state smallint(6) NOT NULL COMMENT '事务状态',
  trx_parti_num tinyint(4) NOT NULL COMMENT '事务参与者数量',
  trx_initiator_id int(11) NOT NULL comment '事务发起者id',
  trx_type tinyint(4) NOT NULL COMMENT '发起者 还是 参与者',
  callback_monitor_url varchar(100) NOT NULL COMMENT '主事务会查接口,用于异常情况确认最终提交还是回滚状态',
  callback_commit_url varchar(100) NOT NULL COMMENT '主事务提交接口',
  callback_rollback_url varchar(100) NOT NULL COMMENT '主事务回滚接口',
  create_time datetime NOT NULL COMMENT '生成时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  version tinyint(4) NOT NULL COMMENT 'version',
  process_status tinyint(4) NOT NULL DEFAULT '1',
  callback_invoke_status tinyint(4) NOT NULL COMMENT '回调接口调用结果状态, 1-未调用，2-调用成功，3-调用失败',
  trx_source tinyint(4) NOT NULL COMMENT '事务来源, 即使用该系统的客户端',
  PRIMARY KEY (trx_id)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of transaction_record
-- ----------------------------
