package org.kernely.holiday.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.joda.time.DateTime;
import org.kernely.controller.AbstractController;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayManagerUserService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller for the managed users holidays
 */
@Path("/holiday/manager/users")
public class HolidayManagerUserController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private HolidayManagerUserService holidayManagerService;

	@Inject
	private UserService userService;

	/**
	 * Redirect to the page for today month
	 * 
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@Menu("holiday_menu_manager_summary")
	public Response getHolidayManagerUsersPage() {
		// Get current date
		try {
			String path = "holiday/manager/users/view/#/month/" + DateTime.now().getMonthOfYear() + "/" + DateTime.now().getYear();
			URI newUri = new URI(path);
			if (userService.isManager(userService.getAuthenticatedUserDTO().username)) {
				return Response.temporaryRedirect(newUri).status(303).build();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return Response.status(Status.FORBIDDEN).build();
	}
	
	/**
	 * Get the template for holiday manager page
	 * 
	 * @return The template
	 */
	@GET
	@Path("/view")
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayManagerUsersPanel() {
		return Response.ok(templateRenderer.render("templates/holiday_manager_users.html")).build();
	}

	/**
	 * Get the main request which build the page table
	 * 
	 * @param date1
	 * @param date2
	 * @return calendarRequestDTO
	 */
	@GET
	@Path("/all")
	@Produces( { MediaType.APPLICATION_JSON })
	public HolidayUsersManagerDTO getAllRequestsOfAllUsers(@QueryParam("month") int month, @QueryParam("year") int year) {
		if (userService.isManager(userService.getAuthenticatedUserDTO().username)) {
			return holidayManagerService.getHolidayForAllManagedUsersForMonth(month, year);
		}
		return new HolidayUsersManagerDTO();
	}
}
