package org.kernely.holiday.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller for the main page of holidays
 */
@Path("/holiday")
public class HolidayMainController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private UserService userService;

	/**
	 * Set the template
	 * @return the page 
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayRequestPage(){
		String isManager = "";
		String isHumanResource ="";
		if (userService.isManager(userService.getAuthenticatedUserDTO().username)){
			isManager = "manager";
		}
		if (userService.currentUserIsHumanResource()){
			isHumanResource = "HumanResource";
		}
		return ok(templateRenderer.create("/templates/gsp/holiday_main_page.gsp").with("manager",isManager).with("human_resource", isHumanResource).addCss("/css/holiday_main.css"));
	}
	
}
