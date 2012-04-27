package org.kernely.project.migrations;

import java.util.ArrayList;
import java.util.List;

import org.kernely.migrator.Command;
import org.kernely.migrator.CreateTable;
import org.kernely.migrator.DataBaseConstants;
import org.kernely.migrator.Migration;
import org.kernely.migrator.RawSql;

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
		ArrayList<Command> commands = new ArrayList<Command>();
		
		//the table kernely_organization
		CreateTable organization = CreateTable.name("kernely_organization");
		organization.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		organization.column("name", DataBaseConstants.VARCHAR_50);
		organization.column("address", DataBaseConstants.VARCHAR_300);
		organization.column("zip", DataBaseConstants.VARCHAR_5);
		organization.column("city", DataBaseConstants.VARCHAR_50);
		organization.column("phone", DataBaseConstants.VARCHAR_20);
		organization.column("fax", DataBaseConstants.VARCHAR_20);
		
		commands.add(organization);
		
		//the table kernely_project
		CreateTable project = CreateTable.name("kernely_project");
		project.column(DataBaseConstants.ID_COLUMN, DataBaseConstants.LONG_PK);
		project.column("name", DataBaseConstants.VARCHAR_100);
		project.column("status", DataBaseConstants.VARCHAR_20);
		project.column("description", DataBaseConstants.TEXT);
		project.column("icon", DataBaseConstants.VARCHAR_100);
		project.column("organization_id", DataBaseConstants.LONG_NOT_NULL);
		project.column("inter_organization_id", DataBaseConstants.LONG_NOT_NULL);
		RawSql projectForeignKey= new RawSql("ALTER TABLE kernely_project ADD CONSTRAINT fk_organization_id FOREIGN KEY (organization_id) REFERENCES kernely_organization (id)");
		
		commands.add(project);
		commands.add(projectForeignKey);

		
		//  the table user_project 
		CreateTable userProject = CreateTable.name("kernely_user_project"); 
		userProject.column("user_id", DataBaseConstants.LONG_NOT_NULL);
		userProject.column("project_id", DataBaseConstants.LONG_NOT_NULL);
		
		RawSql userProjectProject = new RawSql("ALTER TABLE kernely_user_project ADD CONSTRAINT fk_project_id FOREIGN KEY ( project_id) REFERENCES kernely_project (id)");
		RawSql userProjectUser = new RawSql("ALTER TABLE kernely_user_project ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES kernely_user (id)");
		RawSql userProjectPrimaryKey = new RawSql("ALTER TABLE kernely_user_project ADD PRIMARY KEY (user_id, project_id)");
		
		commands.add(userProject);
		commands.add(userProjectUser);
		commands.add(userProjectProject);
		commands.add(userProjectPrimaryKey);
		
		//  the table user_organization
		CreateTable userOrganization = CreateTable.name("kernely_user_organization"); 
		userOrganization.column("user_id", DataBaseConstants.LONG_NOT_NULL);
		userOrganization.column("organization_id", DataBaseConstants.LONG_NOT_NULL);
		
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
