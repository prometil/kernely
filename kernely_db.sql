drop database IF EXISTS kernely_db;

create database kernely_db;

drop table IF EXISTS kernely_user CASCADE;
create table kernely_user(
	id int primary key,
	username varchar(30),
	password varchar(80)
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

drop table IF EXISTS kernely_favorites c;
create table kernely_favorites (
	user_id int references kernely_user(id),
	message_id int references kernely_message(id),
	primary key(user_id, message_id)
);

drop table IF EXISTS kernely_stream_subscriptions;
create table kernely_stream_subscriptions (
	user_id int references kernely_user(id),
	stream_id int references kernely_stream(id),
	primary key(user_id, stream_id)
);

drop table IF EXISTS kernely_group;
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

drop table IF EXISTS kernely_role;
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

drop table IF EXISTS kernely_permission;
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


insert into kernely_user (id, username, password) values (1, 'bobby', '110812f67fa1e1f0117f6f3d70241c1a42a7b07711a93c2477cc516d9042f9db');
insert into kernely_user (id, username, password) values (2, 'john', '799ef92a11af918e3fb741df42934f3b568ed2d93ac1df74f1b8d41a27932a6f');


insert into kernely_stream (id, title, locked, category, user_id) values (1, 'Stream of bobby', false,'STREAM_USERS',1);
insert into kernely_stream (id, title, locked, category, user_id) values (2, 'Stream of john', false,'STREAM_USERS',1);

insert into kernely_role (role_id, name) values (1, 'User');
insert into kernely_role (role_id, name) values (2, 'Administrator');

insert into kernely_group (group_id, name) values (1, 'Kernely team');

insert into kernely_user_group values (1,1);
insert into kernely_user_group values (2,1);

insert into kernely_user_roles values (2,1);
insert into kernely_user_roles values (2,2);




