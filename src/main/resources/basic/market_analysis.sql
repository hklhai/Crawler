/*
Navicat MySQL Data Transfer

Source Server         : HXQH-spark4-root
Source Server Version : 50721
Source Host           : spark4:3306
Source Database       : market_analysis

Target Server Type    : MYSQL
Target Server Version : 50721
File Encoding         : 65001

Date: 2018-03-14 17:27:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for baidu_info
-- ----------------------------
DROP TABLE IF EXISTS `baidu_info`;
CREATE TABLE `baidu_info` (
  `bid` int(12) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `source` varchar(50) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `other_info` varchar(3000) DEFAULT NULL,
  `issue` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`bid`)
) ENGINE=InnoDB AUTO_INCREMENT=4605 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_douban_score
-- ----------------------------
DROP TABLE IF EXISTS `crawler_douban_score`;
CREATE TABLE `crawler_douban_score` (
  `category` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `scorenum` int(11) DEFAULT NULL,
  `scorevalue` float(3,1) DEFAULT NULL,
  `did` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`did`)
) ENGINE=InnoDB AUTO_INCREMENT=632 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_problem
-- ----------------------------
DROP TABLE IF EXISTS `crawler_problem`;
CREATE TABLE `crawler_problem` (
  `url` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT '0',
  `add_time` varchar(255) DEFAULT NULL,
  `success` int(11) DEFAULT NULL,
  `category` varchar(50) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `sorted` varchar(50) DEFAULT NULL,
  `addtime` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_url
-- ----------------------------
DROP TABLE IF EXISTS `crawler_url`;
CREATE TABLE `crawler_url` (
  `url` varchar(255) NOT NULL,
  `add_time` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `category` varchar(60) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `sorted` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_url_book
-- ----------------------------
DROP TABLE IF EXISTS `crawler_url_book`;
CREATE TABLE `crawler_url_book` (
  `url` varchar(255) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `platform` varchar(20) DEFAULT NULL,
  `add_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `category` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_url_literature
-- ----------------------------
DROP TABLE IF EXISTS `crawler_url_literature`;
CREATE TABLE `crawler_url_literature` (
  `lid` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `add_time` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `sorted` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`lid`)
) ENGINE=InnoDB AUTO_INCREMENT=2681 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_url_soap
-- ----------------------------
DROP TABLE IF EXISTS `crawler_url_soap`;
CREATE TABLE `crawler_url_soap` (
  `url` varchar(255) DEFAULT NULL,
  `add_time` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `category` varchar(60) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `sorted` varchar(50) DEFAULT NULL,
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=1681 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for crawler_url_variety
-- ----------------------------
DROP TABLE IF EXISTS `crawler_url_variety`;
CREATE TABLE `crawler_url_variety` (
  `vid` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(255) NOT NULL,
  `add_time` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `category` varchar(60) DEFAULT NULL,
  `platform` varchar(50) DEFAULT NULL,
  `sorted` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`vid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pub_map
-- ----------------------------
DROP TABLE IF EXISTS `pub_map`;
CREATE TABLE `pub_map` (
  `pubid` int(6) NOT NULL AUTO_INCREMENT,
  `pubname` varchar(200) DEFAULT NULL,
  `Location` varchar(25) DEFAULT NULL,
  `pubtype` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`pubid`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
