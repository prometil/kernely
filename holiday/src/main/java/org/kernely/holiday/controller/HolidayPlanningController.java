
package org.kernely.holiday.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.controller.AbstractController;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayHumanResourceService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The holiday controller for the
 * human resource role
 */
@Path("/holiday/planning")
public class HolidayPlanningController extends AbstractController{
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayHumanResourceService holidayHumanResourceService;
	
	/**
	 * Get the template for holiday manager page
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@Menu("planning")
	public Response getHolidayManagerUsersPanel(){
		return Response.ok(templateRenderer.render("templates/holiday_planning.html")).build();
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
		return holidayHumanResourceService.getHolidayForAllUsersForMonth(month,year);
	}


}

