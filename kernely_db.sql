drop database IF EXISTS kernely_db;

create database kernely_db;

drop table IF EXISTS kernely_user CASCADE;
create table kernely_user(
	id int primary key,
	username varchar(30),
	password varchar(80),
	salt varchar(300),
	locked boolean default false
);


drop table IF EXISTS kernely_stream CASCADE;
create table kernely_stream (
	id int primary key,
	title varchar(50),
	locked boolean DEFAULT false,
	category varchar(50),
	user_id int references kernely_user(id)
);


drop table IF EXISTS kernely_message CASCADE;
create table kernely_message (
	id int primary key,
	content text,
	message_parent int references kernely_message(id),
        stream int references kernely_stream(id),
	date date
);

drop table IF EXISTS kernely_favorites CASCADE;
create table kernely_favorites (
	user_id int references kernely_user(id),
	message_id int references kernely_message(id),
	primary key(user_id, message_id)
);

drop table IF EXISTS kernely_stream_subscriptions CASCADE;
create table kernely_stream_subscriptions (
	user_id int references kernely_user(id),
	stream_id int references kernely_stream(id),
	primary key(user_id, stream_id)
);

drop table IF EXISTS kernely_group CASCADE;
create table kernely_group (
        group_id int primary key,
        name varchar(50)
);

drop table IF EXISTS kernely_user_group;
create table kernely_user_group(
        fk_user_user_group int,
        fk_group_user_group int,
	primary key(fk_user_user_group, fk_group_user_group)
);

drop table IF EXISTS kernely_role CASCADE;
create table kernely_role(
        role_id int primary key,
        name varchar(50)
);

drop table IF EXISTS kernely_user_roles;
create table kernely_user_roles(
        fk_user int,
        fk_role int,
	primary key(fk_user, fk_role)
);

drop table IF EXISTS kernely_group_roles;
create table kernely_group_roles(
        fk_group int,
        fk_role int,
	primary key(fk_group, fk_role)
);

drop table IF EXISTS kernely_permission CASCADE;
create table kernely_permission(
        permission_id int primary key,
        name varchar(100)
);

drop table IF EXISTS kernely_user_permissions;
create table kernely_user_permissions(
        fk_user int,
        fk_permission int,
	primary key(fk_user, fk_permission)
);

drop table IF EXISTS kernely_group_permissions;
create table kernely_group_permissions(
        fk_group int,
        fk_permission int,
	primary key(fk_group, fk_permission)
);

drop table IF EXISTS kernely_user_details;
create table kernely_user_details (
        id_user_detail int primary key,
        name varchar(50),
        firstname varchar(50),
        mail varchar(50),
        image varchar(100),
        fk_user_id int,
	adress varchar(100),
	zip varchar(5),
	city varchar(30),
	nationality varchar(30), 
	homephone varchar(10),
	mobilephone varchar(10),
	businessphone varchar(10),
	ssn varchar(20),
	civility int,
	birth date
);


insert into kernely_user (id, username, password, salt) values (1, 'bobby', '2ty4LmflO9cRBKi1liWj3WvSrmtf2EnL67SoTa0bNuM=','gNc1mOUoQxGmCzoV2W7YP3CJj9oDML/SfABujWDrBmvx9xN5if4Y0jMckDNK1we/kMRGR75uQggRgr5dKgnd6ZGIVxG0Zr3EiYxiXBU9aDyZkYvBqy9ffwZ9JScQ5Wke1NarH/lZevTgOUMaLMYVV7q/QvzH42rYek3mF0F1ykM=');
insert into kernely_user (id, username, password, salt) values (2, 'john', 'vAT9Kr/2bSbWoxFj3iinD783xrTez+lE2G/HSGaDzVk=','8EiKXghisVxqZ74Nwen+/5NanikCV0DRB9J31tC0jWGip79G1ZCrkwsFYOkD/aw1ggYA8r/nsYHnWXofR7x0nFU8CK87aiZ3BzXyzH4AEu9pzV/YWfWhq1d0W3gAB36gHsVQ6mZubI5UYforzdATLAAGOlQAa4BXF7Cwxs8wuf0=');


insert into kernely_stream (id, title, locked, category, user_id) values (1, 'Stream of bobby', false,'STREAM_USERS',1);
insert into kernely_stream (id, title, locked, category, user_id) values (2, 'Stream of john', false,'STREAM_USERS',1);

insert into kernely_role (role_id, name) values (1, 'User');
insert into kernely_role (role_id, name) values (2, 'Administrator');

insert into kernely_group (group_id, name) values (1, 'Kernely team');

insert into kernely_user_group values (1,1);
insert into kernely_user_group values (2,1);

insert into kernely_user_roles values (2,1);
insert into kernely_user_roles values (2,2);

insert into kernely_user_details (id_user_detail, name, firstname, mail, image, fk_user_id) values (1, 'Joe', 'Bobby', 'bobby.joe@mail.com', null, 1);
insert into kernely_user_details (id_user_detail, name, firstname, mail, image, fk_user_id) values (2, 'Doe', 'John', 'john.doe@mail.com', null, 2);

