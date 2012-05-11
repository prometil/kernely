package org.kernely.project;

import org.kernely.plugin.AbstractPlugin;
import org.kernely.project.controller.OrganizationAdminController;
import org.kernely.project.controller.OrganizationController;
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
		registerController(OrganizationController.class);
		registerController(ProjectListController.class);
		registerModel(Project.class);
		registerModel(Organization.class);
		registerAdminPage("Project admin", "/admin/projects");
		registerAdminPage("Organization admin", "/admin/organizations");
		registerMigration(new Migration01());
	}

	@Override
	public void start() {

	}

	/**
	 * Configure the plugin
	 */
	@Override
	public void configurePlugin() {
		bind(ProjectService.class);
		bind(OrganizationService.class);
	}

}
