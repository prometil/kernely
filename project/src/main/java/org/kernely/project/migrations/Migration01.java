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
		commands.add(project);
		
		//the table kernely_client
		CreateTable client = CreateTable.name("kernely_client");
		client.column("id", "int primary key");
		client.column("name", "varchar(50)");
		client.column("address", "varchar(200)");
		client.column("email", "varchar(50)");
		client.column("zip", "varchar(5)");
		client.column("city", "varchar(50)");
		client.column("phone", "varchar(10)");
		client.column("fax", "varchar(10)");
		client.column("active", "int");
		
		commands.add(client);
		
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

		return commands;
	}
}
