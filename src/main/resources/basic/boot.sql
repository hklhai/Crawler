create database boot default character set utf8;

use boot;

CREATE TABLE `tb_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO `tb_user` VALUES ('1', 'xdm', '123456');


CREATE TABLE `pub_map` (
  `pubid` int(6) NOT NULL AUTO_INCREMENT,
  `pubname` varchar(200) DEFAULT NULL,
  `Location` varchar(25) DEFAULT NULL,
  `pubtype` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`pubid`)
) ENGINE=InnoDB AUTO_INCREMENT=329 DEFAULT CHARSET=utf8;

INSERT INTO `pub_map` VALUES ('1', '中国化学工业出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('2', '上海新华传媒连锁有限公司', '上海', '发行-示范');
INSERT INTO `pub_map` VALUES ('3', '中国建筑工业出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('4', '高等教育出版社有限公司 ', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('5', '九州出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('6', '中国科技出版传媒股份有限公司', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('7', '化学工业出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('8', '中国社会科学出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('9', '人民邮电出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('10', '人民交通出版社股份有限公司', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('11', '外语教学与研究出版社', '北京', '出版社-示范');
INSERT INTO `pub_map` VALUES ('12', '中南出版传媒集团', '湖南', '发行-示范');
INSERT INTO `pub_map` VALUES ('13', '中文天地出版传媒股份有限公司', '江西', '发行-示范');
INSERT INTO `pub_map` VALUES ('14', '上海新华传媒连锁有限公司', '上海', '发行-示范');
INSERT INTO `pub_map` VALUES ('15', '浙江省新华书店集团股份有限公司', '浙江', '发行-示范');
INSERT INTO `pub_map` VALUES ('16', '新华文轩出版传媒股份有限公司', '四川', '发行-示范');
INSERT INTO `pub_map` VALUES ('17', '湖南人民出版社', '湖南', '出版社-参与');
INSERT INTO `pub_map` VALUES ('18', '湖南科学技术出版社', '湖南', '出版社-参与');
INSERT INTO `pub_map` VALUES ('19', '湖南少年儿童出版社', '湖南', '出版社-参与');
INSERT INTO `pub_map` VALUES ('20', '湖南美术出版社', '湖南', '出版社-参与');
INSERT INTO `pub_map` VALUES ('21', '湖南文艺出版社', '湖南', '出版社-参与');