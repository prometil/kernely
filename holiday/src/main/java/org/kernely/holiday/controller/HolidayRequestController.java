package org.kernely.holiday.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.configuration.AbstractConfiguration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.controller.AbstractController;
import org.kernely.holiday.dto.CalendarRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Holiday request controller
 * @author b.grandperret
 *
 */
@Path("/holiday/request")
public class HolidayRequestController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayRequestService holidayRequestService;
	
	@Inject
	private AbstractConfiguration configuration;

	/**
	 * Get the template for holiday request page
	 * @return The template
	 */
	@POST
	@Produces( { MediaType.TEXT_HTML })
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response getHolidayRequestDate(@FormParam("from") String from, @FormParam("to") String to){
		String fromReplaced = from.replace('/', '-');
		String toReplaced = to.replace('/', '-');
		
		try {
			URI uri = new URI("/holiday/request/new/#/"+fromReplaced+"/"+toReplaced);
			return Response.temporaryRedirect(uri).status(303).build();
		} catch (URISyntaxException e) {
			UriBuilder uriBuilder = UriBuilder.fromPath("/holiday");
			return Response.temporaryRedirect(uriBuilder.build()).status(303).build();
		}
		
	}
	
	/**
	 * Get the template for holiday request page
	 * @return The template
	 */
	@GET
	@Path("/new")
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayRequestPanel(){		
		return Response.ok(templateRenderer.render("templates/holiday_request.html")).build();
	}
	
	/**
	 * Get the calendar request
	 * @param date1
	 * @param date2
	 * @return calendarRequestDTO
	 */
	@GET
	@Path("/interval")
	@Produces( {MediaType.APPLICATION_JSON} )
	public CalendarRequestDTO getTimeIntervalRepresentation(@QueryParam("date1") String date1, @QueryParam("date2") String date2){
		String fromReplaced = date1.replace('-', '/');
		String toReplaced = date2.replace('-', '/');
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern(configuration.getString("locale.dateformat"));
		DateTime d1 = DateTime.parse(fromReplaced, fmt);
		DateTime d2 = DateTime.parse(toReplaced, fmt);
		return holidayRequestService.getCalendarRequest(d1, d2);
	}
	
	/**
	 * Create a holiday request
	 * @param request the holiday request creation DTO
	 * @return ok 
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.TEXT_HTML})
	public Response createRequest(HolidayRequestCreationRequestDTO request){
		// Check if request is not null.
		// We check the value of the first element of details because Jersey create a list with one empty element
		if(request.details.get(0).day != null && !request.details.get(0).day.equals("")){
			holidayRequestService.registerRequestAndDetails(request);
		}
		return Response.ok().build();
	}
}
