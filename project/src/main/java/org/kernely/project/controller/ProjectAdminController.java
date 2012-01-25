package org.kernely.project.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.project.service.ProjectService;

import com.google.inject.Inject;

/**
 * Admin controller for project
 */
@Path("/admin/project")
public class ProjectAdminController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject
	private ProjectService projectService;

	/**
	 * Set the template
	 * 
	 * @return the page admin
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getPluginAdminPanel() {
		Response page;
		if (userService.currentUserIsAdministrator()) {
			page = ok(templateRenderer.create("/templates/gsp/project_admin.gsp").addCss("/css/admin.css").addCss("/css/project_admin.css").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		} else {
			page = ok(templateRenderer.create("/templates/gsp/home.gsp"));
		}
		return page;
	}

}
