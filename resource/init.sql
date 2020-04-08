DROP DATABASE IF EXISTS test;
CREATE DATABASE test; 
USE test;
CREATE TABLE `customer` (
    `id` bigint(20) unsigned NOT NULL,`name` varchar(32) NOT NULL,
    `telephone` varchar(16) NOT NULL,`provinceid` tinyint(3) unsigned NOT NULL DEFAULT '0',
    `province` enum('Anhui','Aomen','Beijing','Chongqing','Fujian','Gansu','Guangdong','Guangxi','Guizhou','Hainan','Hebei','Heilongjiang','Henan','Hubei','Hunan','Jiangsu','Jiangxi','Jilin','Liaoning','Neimenggu','Ningxia','Qinghai','Shaanxi','Shandong','Shanghai','Shanxi','Sichuan','Taiwan','Tianjin','Xianggang','Xinjiang','Xizang','Yunnan','Zhejiang') DEFAULT NULL,
    `city` varchar(16) DEFAULT '',
    `address` varchar(64) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `telephone` (`telephone`))
     ENGINE=InnoDB DEFAULT CHARSET=utf8;