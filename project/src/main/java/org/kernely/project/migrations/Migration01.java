package org.kernely.project.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.migrations.migrator.Command;
import org.kernely.core.migrations.migrator.CreateTable;
import org.kernely.core.migrations.migrator.Migration;
import org.kernely.core.migrations.migrator.RawSql;

/**
 * Holiday migration script
 * 
 */
public class Migration01 extends Migration {
	/**
	 * constructor 
	 */
	public Migration01() {
		super("0.1");
	}

	/**
	 * migration script
	 * @return the list of command
	 */
	@Override
	public List<Command> getList() {
		//the table kernely_project
		ArrayList<Command> commands = new ArrayList<Command>();
		CreateTable project = CreateTable.name("kernely_project");
		project.column("id", "int primary key");
		project.column("name", "varchar(50)");
		project.column("icon", "varchar(100)");
		commands.add(project);
		
		//the table kernely_organization
		CreateTable organization = CreateTable.name("kernely_organization");
		organization.column("id", "int primary key");
		organization.column("name", "varchar(50)");
		organization.column("address", "varchar(200)");
		organization.column("email", "varchar(50)");
		organization.column("zip", "varchar(5)");
		organization.column("city", "varchar(50)");
		organization.column("phone", "varchar(10)");
		organization.column("fax", "varchar(10)");
		organization.column("active", "int");
		
		commands.add(organization);
		
		//  the table user_project 
		CreateTable userProject = CreateTable.name("kernely_user_project"); 
		userProject.column("user_id", "bigint NOT NULL");
		userProject.column("project_id", "int NOT NULL");
		
		RawSql userProjectProject = new RawSql("ALTER TABLE kernely_user_project ADD CONSTRAINT fk_project_id FOREIGN KEY ( project_id) REFERENCES kernely_project (id)");
		RawSql userProjectUser = new RawSql("ALTER TABLE kernely_user_project ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql userProjectPrimaryKey = new RawSql("ALTER TABLE kernely_user_project ADD PRIMARY KEY (user_id, project_id)");
		
		commands.add(userProject);
		commands.add(userProjectUser);
		commands.add(userProjectProject);
		commands.add(userProjectPrimaryKey);
		
		//  the table user_organization
		CreateTable userOrganization = CreateTable.name("kernely_user_organization"); 
		userOrganization.column("user_id", "bigint NOT NULL");
		userOrganization.column("organization_id", "int NOT NULL");
		
		RawSql userOrganizationOrganization = new RawSql("ALTER TABLE kernely_user_organization ADD CONSTRAINT fk_organization_id FOREIGN KEY ( organization_id) REFERENCES kernely_organization (id)");
		RawSql userOrganizationUser = new RawSql("ALTER TABLE kernely_user_organization ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql userOrganizationPrimaryKey = new RawSql("ALTER TABLE kernely_user_organization ADD PRIMARY KEY (user_id, organization_id)");
		
		commands.add(userOrganization);
		commands.add(userOrganizationUser);
		commands.add(userOrganizationOrganization);
		commands.add(userOrganizationPrimaryKey);
		
		
		return commands;
	}
}
