package org.kernely.project;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.project.controller.ClientAdminController;
import org.kernely.project.controller.ProjectAdminController;
import org.kernely.project.migrations.Migration01;
import org.kernely.project.model.Client;
import org.kernely.project.model.Project;
import org.kernely.project.service.ClientService;
import org.kernely.project.service.ProjectService;

/**
 * Plugin for project
 */
public class ProjectPlugin extends AbstractPlugin {
	public static final String NAME = "project";

	/**
	 * Default constructor
	 */
	@SuppressWarnings({ "unchecked" })
	public ProjectPlugin() {
		super(NAME, "/project");
		registerController(ProjectAdminController.class);
		registerController(ClientAdminController.class);
		registerModel(Project.class);
		registerModel(Client.class);
		registerAdminPage("Project admin", "/admin/projects");
		registerAdminPage("Client admin", "/admin/clients");
		registerMigration(new Migration01());
	}

	@Override
	public void start() {

	}

	@Override
	protected void configure() {
		bind(ProjectService.class);
		bind(ClientService.class);
	}

}
