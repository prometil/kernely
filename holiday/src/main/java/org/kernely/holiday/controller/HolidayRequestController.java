package org.kernely.holiday.controller;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.core.controller.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.holiday.dto.CalendarRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.service.HolidayRequestService;

import com.google.inject.Inject;

/**
 * Holiday request controller
 * @author b.grandperret
 *
 */
@Path("/holiday/request")
public class HolidayRequestController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private HolidayRequestService holidayRequestService;

	/**
	 * Get the template for holiday request page
	 * @return The template
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getHolidayRequestPanel(){
		return templateRenderer.create("/templates/gsp/holiday_request.gsp").addCss("/css/holiday_request.css").render();
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
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		DateTime d1 = DateTime.parse(date1, fmt);
		DateTime d2 = DateTime.parse(date2, fmt);
		return holidayRequestService.getCalendarRequest(d1, d2);
	}
	
	/**
	 * Create a holiday request
	 * @param request the holiday request creation DTO
	 * @return ok 
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.APPLICATION_JSON})
	public String createRequest(HolidayRequestCreationRequestDTO request){
		// Check if request is not null.
		// We check the value of the first element of details because Jersey create a list with one empty element
		if(request.details.get(0).day != null && !request.details.get(0).day.equals("")){
			holidayRequestService.registerRequestAndDetails(request);
		}
		return "{\"result\":\"Ok\"}";
	}
}
