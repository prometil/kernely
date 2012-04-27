
package org.kernely.holiday.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.kernely.controller.AbstractController;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayProfilesSummaryDTO;
import org.kernely.holiday.service.HolidayService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The holiday controller for the
 * human resource role
 */
@Path("holiday/summary")
public class HolidaySummaryController extends AbstractController{
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayService profileService;
	
	@Inject
	private UserService userService;
	
	
	/**
	 * Get the template for holiday summary.
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getSummaryPage(){
		if(userService.currentUserIsHumanResource()){
			return Response.ok(templateRenderer.render("templates/holiday_summary.html")).build();
		}
		return Response.status(Status.FORBIDDEN).build();
	}
	
	/**
	 * Get the template for holiday summary.
	 * @return The template
	 */
	@GET
	@Path("/allprofiles")
	@Produces( {MediaType.APPLICATION_JSON} )
	public List<HolidayProfilesSummaryDTO> getSummaryForAllProfiles(@QueryParam("month") int month, @QueryParam("year") int year){
		if(!userService.currentUserIsHumanResource()){	
			return new ArrayList<HolidayProfilesSummaryDTO>();
		}
		return profileService.getSummmaryForAllProfiles(month,year);
	}


}
