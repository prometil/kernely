package org.kernely.project;

import org.kernely.project.migrations.Migration01;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.project.controller.ProjectAdminController;
import org.kernely.project.model.Project;
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
		registerModel(Project.class);
		registerAdminPage("Project admin", "/admin/project");
		registerMigration(new Migration01());
	}

	@Override
	public void start() {

	}

	@Override
	protected void configure() {
		bind(ProjectService.class);
	}

}
