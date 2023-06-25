create database ems;

use ems;

create table user(uid int primary key auto_increment,role 
varchar(5),username 
varchar(20) unique,password varchar(20),name varchar(25),money float,tel 
varchar(20),address varchar(100),info varchar(100));

create table eprice(pid int primary key auto_increment,price float,date 
datetime,info varchar(100));

create table record(rid int primary key auto_increment,op tinyint,changes 
float,date 
datetime,info varchar(100),pid int,uid int,foreign key(pid) references 
eprice(pid),foreign key(uid) references user(uid));

insert into user(uid,role,username,password,name,money,tel,address,info) 
values(1,'admin','admin','admin','星野星奏',0,'','','');

insert into eprice(pid,price,date,info) values(1,6.3,'1970-01-01 00:00:00','初期化');
