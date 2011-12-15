/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.Insert;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

/**
 * Core plugin migration script for version 0.1
 * 
 * @author g.breton
 * 
 */
public class Migration01 extends Migration {

	public Migration01() {
		super("0.1");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kernely.core.migrations.migrator.Migration#getList()
	 */
	@Override
	public List<Command> getList() {
		ArrayList<Command> commands = new ArrayList<Command>();
		// the table kernely _ user
		CreateTable user = CreateTable.name("kernely_user");
		user.column("id", "bigint primary key");
		user.column("username", "varchar(30)");
		user.column("password", "varchar(80)");
		user.column("salt", "varchar(300)");
		user.column("locked", "boolean default false");
		user.column("manager_id", "bigint");		

		Insert insertBoby = Insert.into("kernely_user");
		insertBoby.set("id", "1");
		insertBoby.set("username", "bobby");
		insertBoby.set("password", "2ty4LmflO9cRBKi1liWj3WvSrmtf2EnL67SoTa0bNuM=");
		insertBoby.set("salt", "gNc1mOUoQxGmCzoV2W7YP3CJj9oDML/SfABujWDrBmvx9xN5if4Y0jMckDNK1we/kMRGR75uQggRgr5dKgnd6ZGIVxG0Zr3EiYxiXBU9aDyZkYvBqy9ffwZ9JScQ5Wke1NarH/lZevTgOUMaLMYVV7q/QvzH42rYek3mF0F1ykM=");
		insertBoby.set("locked", "false");

		Insert insertJohn = Insert.into("kernely_user");
		insertJohn.set("id", "2");
		insertJohn.set("username", "john");
		insertJohn.set("password", "vAT9Kr/2bSbWoxFj3iinD783xrTez+lE2G/HSGaDzVk=");
		insertJohn.set("salt", "8EiKXghisVxqZ74Nwen+/5NanikCV0DRB9J31tC0jWGip79G1ZCrkwsFYOkD/aw1ggYA8r/nsYHnWXofR7x0nFU8CK87aiZ3BzXyzH4AEu9pzV/YWfWhq1d0W3gAB36gHsVQ6mZubI5UYforzdATLAAGOlQAa4BXF7Cwxs8wuf0=");
		insertJohn.set("locked", "false");

		RawSql userForeignKey = new RawSql("ALTER TABLE kernely_user ADD CONSTRAINT fk_user_id FOREIGN KEY (manager_id) REFERENCES kernely_user (id)");
		
		commands.add(user);
		commands.add(insertBoby);
		commands.add(insertJohn);
		commands.add(userForeignKey);
		
		
		//the table kernely group
		CreateTable group = CreateTable.name("kernely_group");
		group.column("id", "int primary  key");
		group.column("name", "varchar(30)");
		
		commands.add(group);
		
		// the table kernely permission
		CreateTable permission = CreateTable.name("kernely_permission");
		permission.column("id", "int primary key");
		permission.column("name", "varchar(30)");

		commands.add(permission);
		
		//the table kernely_userDetails
		CreateTable userDetails = CreateTable.name("kernely_user_details");
		userDetails.column("id", "int primary key");
		userDetails.column("name", "varchar(50)");
		userDetails.column("firstname", "varchar(50)");
		userDetails.column("mail", "varchar(50)");
		userDetails.column("image","varchar(100)");
		userDetails.column("user_id", "bigint");
		userDetails.column("adress", "varchar(100)");
		userDetails.column("zip", "varchar(5)");
		userDetails.column("city", "varchar(30)");
		userDetails.column("nationality", "varchar(30)");
		userDetails.column("homephone", "varchar(10)");
		userDetails.column("mobilephone","varchar(10)");
		userDetails.column("businessphone","varchar(10)");
		userDetails.column("ssn", "varchar(20)");
		userDetails.column("civility", "int");
		userDetails.column("birth", "timestamp");
		RawSql  userDetailsForeignKey= new RawSql("ALTER TABLE kernely_user_details ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
	
		Insert insertJohnDetails = Insert.into("kernely_user_details");
		insertJohnDetails.set("id", "5");
		insertJohnDetails.set("name", "doe");
		insertJohnDetails.set("firstname", "john");
		insertJohnDetails.set("mail", "john.doe@prometil.com");
		insertJohnDetails.set("user_id", "2");
		insertJohnDetails.set("adress", "frayssnet le gelat");
		insertJohnDetails.set("zip","46250");
		insertJohnDetails.set("city","toulouse");
		insertJohnDetails.set("nationality","french");
		insertJohnDetails.set("homephone","0123456789");
		insertJohnDetails.set("mobilephone","0632154789");
		insertJohnDetails.set("businessphone","0897654321");
		insertJohnDetails.set("ssn", "123565552221111");
		insertJohnDetails.set("civility","1");
		insertJohnDetails.set("birth", "18/12/1990");
		
		Insert insertBobbyDetails = Insert.into("kernely_user_details");
		insertBobbyDetails.set("id", "6");
		insertBobbyDetails.set("name", "robert");
		insertBobbyDetails.set("firstname", "bobby");
		insertBobbyDetails.set("mail", "robert.bobby@prometil.com");
		insertBobbyDetails.set("user_id", "1");
		insertBobbyDetails.set("adress", "frayssnet le gelat");
		insertBobbyDetails.set("zip","46250");
		insertBobbyDetails.set("city","toulouse");
		insertBobbyDetails.set("nationality","english");
		insertBobbyDetails.set("homephone","0123456789");
		insertBobbyDetails.set("mobilephone","0632154789");
		insertBobbyDetails.set("businessphone","0897654321");
		insertBobbyDetails.set("ssn", "123565552221111");
		insertBobbyDetails.set("civility","1");
		insertBobbyDetails.set("birth", "18/12/1990");
		
		
		commands.add(userDetails);
		commands.add(userDetailsForeignKey);
		commands.add(insertJohnDetails);
		commands.add(insertBobbyDetails);
		
		//the table role
		CreateTable role = CreateTable.name("kernely_role");
		role.column("id","int primary key");
		role.column("name", "varchar(30)");
		
		commands.add(role);
		
		Insert userRole = Insert.into("kernely_role");
		userRole.set("id","3");
		userRole.set("name","User");
		
		Insert adminRole = Insert.into("kernely_role");
		adminRole.set("id","4");
		adminRole.set("name","Administrator");
				
		commands.add(userRole);
		commands.add(adminRole);
		
		
		//the table group permision
		CreateTable groupPermission = CreateTable.name("kernely_group_permissions");
		groupPermission.column("group_id", "int NOT NULL");
		groupPermission.column("permission_id", "int NOT NULL");
		
		RawSql groupPermissionGroup = new RawSql("ALTER TABLE kernely_group_permissions ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES kernely_group (id)");
		RawSql groupPermissionPermission = new RawSql("ALTER TABLE kernely_group_permissions ADD CONSTRAINT fk_permission_id FOREIGN KEY (permission_id) REFERENCES kernely_permission (id)");
		RawSql groupPermissionPrimaryKey = new RawSql("ALTER TABLE kernely_group_permissions ADD PRIMARY KEY (group_id,permission_id)");
		
		commands.add(groupPermission);
		commands.add(groupPermissionPermission);
		commands.add(groupPermissionGroup);
		commands.add(groupPermissionPrimaryKey);
		
		//the table group roles
		CreateTable groupRole = CreateTable.name("kernely_group_roles");
		groupRole.column("group_id", "int NOT NULL");
		groupRole.column("role_id", "int NOT NULL");
		
		RawSql groupRoleGroup = new RawSql("ALTER TABLE kernely_group_roles ADD CONSTRAINT fk_group_id FOREIGN KEY (group_id) REFERENCES kernely_group (id)");
		RawSql groupRoleRole = new RawSql("ALTER TABLE kernely_group_roles ADD CONSTRAINT fk_roles_id FOREIGN KEY (role_id) REFERENCES kernely_role (id)");
		RawSql groupRolePrimaryKey = new RawSql("ALTER TABLE kernely_group_roles ADD PRIMARY KEY (group_id, role_id)");
		
		commands.add(groupRole);
		commands.add(groupRoleGroup);
		commands.add(groupRoleRole);
		commands.add(groupRolePrimaryKey);
		
		//  the table user_group 
		CreateTable userGroup = CreateTable.name("kernely_user_group"); 
		userGroup.column("user_id", "bigint NOT NULL");
		userGroup.column("group_id", "int NOT NULL");
		
		RawSql userGroupGroup = new RawSql("ALTER TABLE kernely_user_group ADD CONSTRAINT fk_group_id FOREIGN KEY ( group_id) REFERENCES kernely_group (id)");
		RawSql userGroupUser = new RawSql("ALTER TABLE kernely_user_group ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql userGroupPrimaryKey = new RawSql("ALTER TABLE kernely_user_group ADD PRIMARY KEY (user_id, group_id)");
		
		commands.add(userGroup);
		commands.add(userGroupUser);
		commands.add(userGroupGroup);
		commands.add(userGroupPrimaryKey);
		
		// the table user_permission
		CreateTable userPermission = CreateTable.name("kernely_user_permissions"); 
		userPermission.column("user_id", "bigint NOT NULL");
		userPermission.column("permission_id", "int NOT NULL");
		
		RawSql userPermissionUser = new RawSql("ALTER TABLE kernely_user_permissions ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");  
		RawSql userPermissionPermission = new RawSql("ALTER TABLE kernely_user_permissions ADD CONSTRAINT fk_permission_id FOREIGN KEY (permission_id) REFERENCES kernely_permission (id)"); 
		RawSql userPermissionPrimaryKey = new RawSql("ALTER TABLE kernely_user_permissions ADD PRIMARY KEY (user_id, permission_id)"); 
		
		commands.add(userPermission);
		commands.add(userPermissionPermission);
		commands.add(userPermissionUser);
		commands.add(userPermissionPrimaryKey);
		
		// the table user_permission
		CreateTable userRoles= CreateTable.name("kernely_user_roles"); 
		userRoles.column("user_id", "bigint NOT NULL");
		userRoles.column("role_id", "int NOT NULL");
		
		RawSql userRoleUser = new RawSql("ALTER TABLE kernely_user_roles ADD CONSTRAINT fk_user_id  FOREIGN KEY (user_id) REFERENCES kernely_user (id)");  
		RawSql userRoleRole = new RawSql("ALTER TABLE kernely_user_roles ADD CONSTRAINT fk_role_id FOREIGN KEY (role_id ) REFERENCES kernely_role (id)");
		RawSql userRolePrimaryKey = new RawSql("ALTER TABLE kernely_user_roles ADD PRIMARY KEY (user_id, role_id)");
		
		commands.add(userRoles);
		commands.add(userRoleRole);
		commands.add(userRoleUser);
		commands.add(userRolePrimaryKey);	
		
		Insert insertUserRole1 = Insert.into("kernely_user_roles");
		insertUserRole1.set("user_id", "2");
		insertUserRole1.set("role_id", "4");
		
		commands.add(insertUserRole1);
		
		Insert insertUserRole2 = Insert.into("kernely_user_roles");
		insertUserRole2.set("user_id", "1");
		insertUserRole2.set("role_id", "3");
		
		commands.add(insertUserRole2);
		
		CreateTable mail = CreateTable.name("kernely_mail");
		mail.column("mail_id", "int primary key");
		mail.column("subject", "text");
		mail.column("content", "text");
		mail.column("recipients", "text");
		mail.column("cc", "text");
		
		commands.add(mail);
		
		//sequence 
		RawSql hibernateSequence = new RawSql("CREATE SEQUENCE hibernate_sequence START 10");
		commands.add(hibernateSequence);
		
		return commands;
	}

}
