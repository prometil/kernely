package org.kernely.holiday.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayManagerUserService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller for the managed users holidays
 */
@Path("/holiday/manager/users")
public class HolidayManagerUserController extends AbstractController{
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayManagerUserService holidayManagerService;
	
	/**
	 * Get the template for holiday manager page
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_USERMANAGER)
	public Response getHolidayManagerUsersPanel(){
		return Response.ok(templateRenderer.render("templates/holiday_manager_users.html")).build();
	}
	
	/**
	 * Get the main request which build the page table
	 * @param date1
	 * @param date2
	 * @return calendarRequestDTO
	 */
	@GET
	@Path("/all")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces( {MediaType.APPLICATION_JSON} )
	public HolidayUsersManagerDTO getAllRequestsOfAllUsers(@QueryParam("month") int month, @QueryParam("year") int year){
		return  holidayManagerService.getHolidayForAllManagedUsersForMonth(month, year);
	}
}
