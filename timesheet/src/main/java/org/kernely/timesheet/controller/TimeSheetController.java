package org.kernely.timesheet.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.configuration.AbstractConfiguration;
import org.joda.time.DateTime;
import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.GroupService;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.service.TimeSheetService;

import com.google.inject.Inject;

/**
 * Main controller for timesheet
 */
@Path("/timesheet")
public class TimeSheetController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private AbstractConfiguration configuration;

	@Inject
	private UserService userService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private GroupService groupService;

	@Inject
	private TimeSheetService timeSheetService;

	/**
	 * Set the template
	 * 
	 * @return the main time sheet page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getPluginAdminPanel() {
		Response page = ok(templateRenderer.create("/templates/gsp/timesheet_main_page.gsp").addCss("/css/timesheet.css"));

		return page;
	}
	
	

	/**
	 * Gets days associated to the current week.
	 * 
	 * @return A JSON String containing the rights of all users for the project
	 */
	@GET
	@Path("/current")
	@Produces( { MediaType.APPLICATION_JSON })
	public TimeSheetCalendarDTO getCurrentTimeSheet() {
		int week = DateTime.now().getWeekOfWeekyear();
		int year = DateTime.now().getYearOfCentury();
		TimeSheetCalendarDTO timeSheetCalendar = timeSheetService.getTimeSheetCalendar(week, year, userService.getAuthenticatedUserDTO().id, true);
		return timeSheetCalendar;
	}
}
