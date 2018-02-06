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
