package org.kernely.holiday.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.SobaTemplateRenderer;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayManagerUserService;

import com.google.inject.Inject;

/**
 * Controller for the managed users holidays
 */
@Path("holiday/manager/users")
public class HolidayManagerUserController extends AbstractController{
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayManagerUserService holidayManagerService;
	
	@Inject
	private UserService userService;
	
	/**
	 * Get the template for holiday manager page
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayManagerUsersPanel(){
		if(userService.isManager(userService.getAuthenticatedUserDTO().username)){
			return Response.ok(templateRenderer.render("templates/holiday_manager_users.html")).build();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
	
	/**
	 * Get the main request which build the page table
	 * @param date1
	 * @param date2
	 * @return calendarRequestDTO
	 */
	@GET
	@Path("/all")
	@Produces( {MediaType.APPLICATION_JSON} )
	public HolidayUsersManagerDTO getAllRequestsOfAllUsers(@QueryParam("month") int month, @QueryParam("year") int year){
		if(userService.isManager(userService.getAuthenticatedUserDTO().username)){	
			return  holidayManagerService.getHolidayForAllManagedUsersForMonth(month, year);
		}
		return new HolidayUsersManagerDTO();
	}
}
