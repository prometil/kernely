drop database IF EXISTS kernely_db;

create database kernely_db;

drop table IF EXISTS kernely_user;
create table kernely_user(
	id int primary key,
	username varchar(30),
	password varchar(80)
);

drop table IF EXISTS stream_messages;
create table stream_messages (
	id int primary key,
	message text,
	date date
);

insert into kernely_user (id, username, password) values (1, 'bobby', '110812f67fa1e1f0117f6f3d70241c1a42a7b07711a93c2477cc516d9042f9db');
insert into kernely_user (id, username, password) values (2, 'john', '799ef92a11af918e3fb741df42934f3b568ed2d93ac1df74f1b8d41a27932a6f');
