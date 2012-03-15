package org.kernely.timesheet.controller;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.dto.TimeSheetDayDTO;
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
	private UserService userService;

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
	@Path("/calendar")
	@Produces( { MediaType.APPLICATION_JSON })
	public TimeSheetCalendarDTO getTimeSheetForWeekOfYear(@QueryParam("week") int week, @QueryParam("year") int year ) {
		if(week == 0 || year == 0){
			week = DateTime.now().getWeekOfWeekyear();
			year = DateTime.now().getYear();
		}
		TimeSheetCalendarDTO timeSheetCalendar = timeSheetService.getTimeSheetCalendar(week, year, userService.getAuthenticatedUserDTO().id);
		return timeSheetCalendar;
	}
	
	/**
	 * Update a day in a timesheet
	 * 
	 * @return A JSON String containing the updated day detail
	 */
	@POST
	@Path("/update")
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public TimeSheetDayDTO updateTimeSheet(TimeSheetDayDTO timeSheetDay) {
		return timeSheetService.createOrUpdateDayAmountForProject(timeSheetDay);
	}
	
	/**
	 * Remove a line in a time sheet
	 */
	@GET
	@Path("/removeline")
	public void deleteTimeSheet(@QueryParam("timeSheetUniqueId") long timeSheetId, @QueryParam("projectUniqueId") long projectId) {
		timeSheetService.removeLine(timeSheetId, projectId);
	}
}
