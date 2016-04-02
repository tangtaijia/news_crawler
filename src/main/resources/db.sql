CREATE DATABASE IF NOT EXISTS `crawler` CHARSET=utf8;

USE DATABASE `crawler`;
-- ----------------------------
-- 新闻表
-- ----------------------------
-- DROP TABLE IF EXISTS `news`;
CREATE TABLE IF NOT EXISTS `news` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `hashkey` varchar(32) NOT NULL DEFAULT '' COMMENT 'hash值',
  `title` varchar(255) NOT NULL DEFAULT '' COMMENT '标题',
  `url` varchar(255) NOT NULL DEFAULT '' COMMENT '链接',
  `source` varchar(255) NOT NULL DEFAULT '' COMMENT '来源',
  PRIMARY KEY (`id`),
  UNIQUE KEY `hash_key` (`hashkey`) USING BTREE
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='新闻表';

-- ----------------------------
-- url操作记录表
-- ----------------------------
-- DROP TABLE IF EXISTS `record`;
CREATE TABLE IF NOT EXISTS `record` (
  `recordID` int(5) NOT NULL AUTO_INCREMENT,
  `URL` text NOT NULL,
  `crawled` tinyint(1) NOT NULL COMMENT '处理过',
  PRIMARY KEY (`recordID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='url操作记录表';