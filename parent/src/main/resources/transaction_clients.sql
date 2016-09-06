DROP TABLE IF EXISTS transaction_clients;
CREATE TABLE transaction_clients (
  client_id int(11) NOT NULL AUTO_INCREMENT COMMENT '使用协调系统的客户端的主键ID',
  client_code varchar(30) NOT NULL COMMENT '客户端唯一标识码',
  client_desc varchar(100) NOT NULL COMMENT '对客户端的说明',
  create_time datetime NOT NULL COMMENT '生成时间',
  update_time datetime NOT NULL COMMENT '更新时间',
  is_valid tinyint(4) NOT NULL COMMENT '该客户端是否还在使用 1-使用,2-不使用',
  PRIMARY KEY (`client_id`),
  UNIQUE KEY idx_unique_client_code (client_code)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;