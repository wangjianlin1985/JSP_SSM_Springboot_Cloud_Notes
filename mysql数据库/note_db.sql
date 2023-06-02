/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50051
Source Host           : localhost:3306
Source Database       : note_db

Target Server Type    : MYSQL
Target Server Version : 50051
File Encoding         : 65001

Date: 2018-07-13 23:29:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `admin`
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `username` varchar(20) NOT NULL default '',
  `password` varchar(32) default NULL,
  PRIMARY KEY  (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('a', 'a');

-- ----------------------------
-- Table structure for `t_collect`
-- ----------------------------
DROP TABLE IF EXISTS `t_collect`;
CREATE TABLE `t_collect` (
  `collectId` int(11) NOT NULL auto_increment COMMENT '收藏id',
  `noteObj` int(11) NOT NULL COMMENT '被收藏笔记',
  `userObj` varchar(30) NOT NULL COMMENT '收藏用户',
  `collectTime` varchar(20) default NULL COMMENT '收藏时间',
  PRIMARY KEY  (`collectId`),
  KEY `noteObj` (`noteObj`),
  KEY `userObj` (`userObj`),
  CONSTRAINT `t_collect_ibfk_1` FOREIGN KEY (`noteObj`) REFERENCES `t_note` (`noteId`),
  CONSTRAINT `t_collect_ibfk_2` FOREIGN KEY (`userObj`) REFERENCES `t_userinfo` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_collect
-- ----------------------------
INSERT INTO `t_collect` VALUES ('3', '4', 'user2', '2018-04-04 12:16:34');
INSERT INTO `t_collect` VALUES ('4', '2', 'user2', '2018-04-04 12:16:50');

-- ----------------------------
-- Table structure for `t_leaveword`
-- ----------------------------
DROP TABLE IF EXISTS `t_leaveword`;
CREATE TABLE `t_leaveword` (
  `leaveWordId` int(11) NOT NULL auto_increment COMMENT '留言id',
  `leaveTitle` varchar(80) NOT NULL COMMENT '留言标题',
  `leaveContent` varchar(2000) NOT NULL COMMENT '留言内容',
  `userObj` varchar(30) NOT NULL COMMENT '留言人',
  `leaveTime` varchar(20) default NULL COMMENT '留言时间',
  `replyContent` varchar(1000) default NULL COMMENT '管理回复',
  `replyTime` varchar(20) default NULL COMMENT '回复时间',
  PRIMARY KEY  (`leaveWordId`),
  KEY `userObj` (`userObj`),
  CONSTRAINT `t_leaveword_ibfk_1` FOREIGN KEY (`userObj`) REFERENCES `t_userinfo` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_leaveword
-- ----------------------------
INSERT INTO `t_leaveword` VALUES ('1', '我想学英语，有比较吗', '这里可以找到这方面的笔记吗？', 'user2', '2018-03-30 14:03:52', '可以的，只有别人共享你就可以下载', '2018-03-30 14:03:55');
INSERT INTO `t_leaveword` VALUES ('2', '这里的笔记真多啊', '我发现这里的免费资料真多', 'user1', '2018-04-04 10:21:11', '是的，更多分类更新中', '2018-04-04 12:08:14');

-- ----------------------------
-- Table structure for `t_note`
-- ----------------------------
DROP TABLE IF EXISTS `t_note`;
CREATE TABLE `t_note` (
  `noteId` int(11) NOT NULL auto_increment COMMENT '笔记id',
  `noteTypeObj` int(11) NOT NULL COMMENT '笔记类型',
  `title` varchar(80) NOT NULL COMMENT '摘要标题',
  `notebookPhoto` varchar(60) NOT NULL COMMENT '笔记图片',
  `content` varchar(8000) NOT NULL COMMENT '笔记内容描述',
  `noteFile` varchar(60) NOT NULL COMMENT '笔记文件',
  `deleteFlag` varchar(20) NOT NULL COMMENT '是否回收站',
  `userObj` varchar(30) NOT NULL COMMENT '上传用户',
  `addTime` varchar(20) default NULL COMMENT '上传时间',
  PRIMARY KEY  (`noteId`),
  KEY `noteTypeObj` (`noteTypeObj`),
  KEY `userObj` (`userObj`),
  CONSTRAINT `t_note_ibfk_1` FOREIGN KEY (`noteTypeObj`) REFERENCES `t_notetype` (`noteTypeId`),
  CONSTRAINT `t_note_ibfk_2` FOREIGN KEY (`userObj`) REFERENCES `t_userinfo` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_note
-- ----------------------------
INSERT INTO `t_note` VALUES ('2', '1', '学姐学习c语言笔记', 'upload/2853f405-380f-416d-b650-4cba8efc13fc.jpg', '<p>我也算是半个学霸，成绩一直在班上前茅，这是我的c语言学习笔记资料，需要的同学赶快下载收藏！</p>', 'upload/13095229-ad00-4325-990a-074efcb6bb05.doc', '否', 'user1', '2018-04-04 11:31:28');
INSERT INTO `t_note` VALUES ('4', '2', '一元微积分学习笔记', 'upload/43487dfe-513c-4d12-b466-ebae3745822c.jpg', '<p>学姐带你复习高等数学的多元微积分知识！<br/></p>', 'upload/a0aa2f2b-cc55-4e78-a42e-7bf98057639e.doc', '是', 'user1', '2018-04-03 12:07:27');

-- ----------------------------
-- Table structure for `t_notetype`
-- ----------------------------
DROP TABLE IF EXISTS `t_notetype`;
CREATE TABLE `t_notetype` (
  `noteTypeId` int(11) NOT NULL auto_increment COMMENT '笔记类型id',
  `noteTypeName` varchar(20) NOT NULL COMMENT '笔记类型名称',
  `noteTypeDesc` varchar(800) NOT NULL COMMENT '笔记类型描述',
  PRIMARY KEY  (`noteTypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notetype
-- ----------------------------
INSERT INTO `t_notetype` VALUES ('1', '计算机类', '计算机专业的笔记');
INSERT INTO `t_notetype` VALUES ('2', '数学笔记', '数学相关的笔记');
INSERT INTO `t_notetype` VALUES ('3', '英语笔记', '英语相关的笔记');

-- ----------------------------
-- Table structure for `t_notice`
-- ----------------------------
DROP TABLE IF EXISTS `t_notice`;
CREATE TABLE `t_notice` (
  `noticeId` int(11) NOT NULL auto_increment COMMENT '公告id',
  `title` varchar(80) NOT NULL COMMENT '标题',
  `content` varchar(5000) NOT NULL COMMENT '公告内容',
  `publishDate` varchar(20) default NULL COMMENT '发布时间',
  PRIMARY KEY  (`noticeId`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_notice
-- ----------------------------
INSERT INTO `t_notice` VALUES ('1', '云笔记网成立了', '<p>同学们有啥宝贵的学习笔记，可以分享上来，为更多的人造福！</p>', '2018-03-30 14:04:03');

-- ----------------------------
-- Table structure for `t_userinfo`
-- ----------------------------
DROP TABLE IF EXISTS `t_userinfo`;
CREATE TABLE `t_userinfo` (
  `user_name` varchar(30) NOT NULL COMMENT 'user_name',
  `password` varchar(30) NOT NULL COMMENT '登录密码',
  `name` varchar(20) NOT NULL COMMENT '姓名',
  `gender` varchar(4) NOT NULL COMMENT '性别',
  `birthDate` varchar(20) default NULL COMMENT '出生日期',
  `userPhoto` varchar(60) NOT NULL COMMENT '用户照片',
  `telephone` varchar(20) NOT NULL COMMENT '联系电话',
  `email` varchar(50) NOT NULL COMMENT '邮箱',
  `address` varchar(80) default NULL COMMENT '家庭地址',
  `regTime` varchar(20) default NULL COMMENT '注册时间',
  PRIMARY KEY  (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_userinfo
-- ----------------------------
INSERT INTO `t_userinfo` VALUES ('user1', '123', '王小琴', '女', '2018-03-13', 'upload/70600671-2142-4748-9415-791d79297ead.jpg', '13830852442', 'xinmeng@163.com', '福建福州滨海路', '2018-03-30 14:01:42');
INSERT INTO `t_userinfo` VALUES ('user2', '123', '李兴萌', '女', '2018-04-02', 'upload/73ffb33c-3d83-45af-9694-a1a2eb712201.jpg', '13958342342', 'gasfsf@163.com', '四川南充滨江路', '2018-04-04 11:33:38');
