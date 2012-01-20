package org.kernely.holiday.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayManagerUserService;

import com.google.inject.Inject;

/**
 * Controller for the managed users holidays
 */
@Path("holiday/manager/users")
public class HolidayManagerUserController extends AbstractController{
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private HolidayManagerUserService holidayManagerService;
	
	/**
	 * Get the template for holiday manager page
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getHolidayManagerUsersPanel(){
		return templateRenderer.create("/templates/gsp/holiday_manager_users.gsp").addCss("/css/holiday_manager_users.css").render();
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
		return  holidayManagerService.getHolidayForAllManagedUsersForMonth(month, year);
	}
}
