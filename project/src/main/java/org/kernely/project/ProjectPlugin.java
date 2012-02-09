package org.kernely.project;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.project.controller.OrganizationAdminController;
import org.kernely.project.controller.ProjectAdminController;
import org.kernely.project.controller.ProjectListController;
import org.kernely.project.migrations.Migration01;
import org.kernely.project.model.Organization;
import org.kernely.project.model.Project;
import org.kernely.project.service.OrganizationService;
import org.kernely.project.service.ProjectService;

/**
 * Plugin for project
 */
public class ProjectPlugin extends AbstractPlugin {
	public static final String NAME = "project";

	/**
	 * Default constructor
	 */
	public ProjectPlugin() {
		super();
		registerName(NAME);
		registerName("organization");
		registerPath("/project");
		registerPath("/organization");
		registerController(ProjectAdminController.class);
		registerController(OrganizationAdminController.class);
		registerController(ProjectListController.class);
		registerModel(Project.class);
		registerModel(Organization.class);
		registerAdminPage("Project admin", "/admin/projects");
		registerAdminPage("organization admin", "/admin/organizations");
		registerMigration(new Migration01());
	}

	@Override
	public void start() {

	}

	@Override
	protected void configure() {
		bind(ProjectService.class);
		bind(OrganizationService.class);
	}

}
