# icon外卖

#### 介绍
icon外卖，练手项目

#### 软件架构
软件架构说明
通过Nginx反向代理使用了前后端分离技术，加入了MySQL主从复制。


#### 安装教程

1. git clone
2. 导入数据库在自己的本地文件里，修改yml里面数据库的配置改为自己的
3. redis的配置也要修改为自己的，没有redis服务的话去创建一个，ip改为自己的

#### 使用说明

1. 配置两台服务器用来MySQL主从配置，分别安装Mysql并启动服务成功
2. 在主库中安装Nginx:部署前端项目、配置反向代理 Mysql，Redis:缓存中间件
3. 在从库中安装jdk:运行Java项目，git:版本控制工具，maven:项目构建工具，jar: Spring Boot项目打成jar包基于内置Tomcat运行，Mysql:主从复制结构中的从库

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request

