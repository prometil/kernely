
package org.kernely.holiday.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.holiday.dto.HolidayProfilesSummaryDTO;
import org.kernely.holiday.service.HolidayService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The holiday controller for the
 * human resource role
 */
@Path("/holiday/summary")
public class HolidaySummaryController extends AbstractController{
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayService profileService;
	
	/**
	 * Get the template for holiday summary.
	 * @return The template
	 */
	@GET
	@RequiresRoles(Role.ROLE_HUMANRESOURCE)
	@Menu("summary")
	@Produces( { MediaType.TEXT_HTML })
	public Response getSummaryPage(){
		return Response.ok(templateRenderer.render("templates/holiday_summary.html")).build();
	}
	
	/**
	 * Get the template for holiday summary.
	 * @return The template
	 */
	@GET
	@Path("/allprofiles")
	@RequiresRoles(Role.ROLE_HUMANRESOURCE)
	@Produces( {MediaType.APPLICATION_JSON} )
	public List<HolidayProfilesSummaryDTO> getSummaryForAllProfiles(@QueryParam("month") int month, @QueryParam("year") int year){
		return profileService.getSummmaryForAllProfiles(month,year);
	}


}
