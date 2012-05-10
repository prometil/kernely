
package org.kernely.holiday.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.joda.time.DateTime;
import org.kernely.controller.AbstractController;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayHumanResourceService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import ch.qos.logback.core.status.Status;

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
	 * Redirect to the planning of the actual day
	 * @return The redirection
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@Menu("planning")
	public Response getHolidayManagerUsersPanel(){
		// Get current date
		try {
			String path = "holiday/planning/view/#/month/" + DateTime.now().getMonthOfYear() + "/" + DateTime.now().getYear();
			URI newUri = new URI(path);
			return Response.temporaryRedirect(newUri).status(303).build();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return Response.status(Status.ERROR).build();
		}
	}

	/**
	 * Get the template for holiday manager page
	 */
	@GET
	@Path("/view")
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayPlanning(){
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

