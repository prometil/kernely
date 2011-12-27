package org.kernely.holiday.controller;

import javax.ws.rs.GET;
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
import org.kernely.holiday.service.HolidayBalanceService;
import org.kernely.holiday.service.HolidayRequestService;

import com.google.inject.Inject;

@Path("/holiday/request")
public class HolidayRequestController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private HolidayRequestService holidayRequestService;

	@Inject
	private HolidayBalanceService holidayBalanceService;

	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getHolidayRequestPanel(){
		return templateRenderer.create("/templates/gsp/holiday_request.gsp").addCss("/css/holiday_request.css").render();
	}
	
	@GET
	@Path("/interval")
	@Produces( {"application/json"} )
	public CalendarRequestDTO getTimeIntervalRepresentation(@QueryParam("date1") String date1, @QueryParam("date2") String date2){
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		DateTime d1 = DateTime.parse(date1, fmt);
		DateTime d2 = DateTime.parse(date2, fmt);
		return holidayRequestService.getCalendarRequest(d1, d2);
	}
}
