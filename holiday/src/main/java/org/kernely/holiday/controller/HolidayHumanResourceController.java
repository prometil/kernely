
package org.kernely.holiday.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.holiday.dto.HolidayProfilesSummaryDTO;
import org.kernely.holiday.dto.HolidayUsersManagerDTO;
import org.kernely.holiday.service.HolidayHumanResourceService;
import org.kernely.holiday.service.HolidayService;

import com.google.inject.Inject;

/**
 * The holiday controller for the
 * human resource role
 */
@Path("holiday/humanresource")
public class HolidayHumanResourceController extends AbstractController{
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private HolidayService profileService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private HolidayHumanResourceService holidayHumanResourceService;
	
	/**
	 * Get the template for holiday manager page
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getHolidayManagerUsersPanel(){
		return templateRenderer.create("/templates/gsp/holiday_human_resource.gsp").addCss("/css/holiday_human_resource.css").render();
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
	
	/**
	 * Get the template for holiday summary.
	 * @return The template
	 */
	@GET
	@Path("/summary")
	@Produces( { MediaType.TEXT_HTML })
	public String getSummaryPage(){
		return templateRenderer.create("/templates/gsp/holiday_summary.gsp").addCss("/css/holiday_human_resource.css").render();
	}
	
	/**
	 * Get the template for holiday summary.
	 * @return The template
	 */
	@GET
	@Path("/summary/allprofiles")
	@Produces( {MediaType.APPLICATION_JSON} )
	public List<HolidayProfilesSummaryDTO> getSummaryForAllProfiles(@QueryParam("month") int month, @QueryParam("year") int year){
		if(!userService.currentUserIsHumanResource()){	
			return null;
		}
		return profileService.getSummmaryForAllProfiles(month,year);
	}


}
