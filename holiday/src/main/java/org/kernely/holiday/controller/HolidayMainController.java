package org.kernely.holiday.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.controller.AbstractController;
import org.kernely.holiday.dto.CalendarBalanceDetailDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.dto.IntervalDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller for the main page of holidays
 */
@Path("/holiday")
public class HolidayMainController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private HolidayRequestService holidayRequestService ;
	
	/**
	 * Set the template
	 * @return the page 
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@Menu("my_holiday")
	public Response getHolidayHomePage(){
		return Response.ok(templateRenderer.render("templates/holiday_main_page.html")).build();
	}
	
	/**
	 * Get all existing holiday request pending of a specific user in the database
	 * @return A list of all DTO associated to the existing holiday requests of a specific user in the database
	 */
	@GET
	@Path("/all/pending")
	@Produces({MediaType.APPLICATION_JSON})
	public List<HolidayRequestDTO> displayAllHolidayRequestPending()
	{
		List<HolidayRequestDTO> pendingRequest = holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.PENDING_STATUS, -1);
		return pendingRequest;
	}
	
	/**
	 * Get all existing holiday request with a status of a specific user in the database
	 * @return A list of all DTO associated to the existing holiday requests of a specific user in the database
	 */
	@GET
	@Path("/all/status")
	@Produces({MediaType.APPLICATION_JSON})
	public List<HolidayRequestDTO> displayAllHolidayRequestStatus()
	{
		List<HolidayRequestDTO> lhr = new ArrayList<HolidayRequestDTO>();
		lhr.addAll(holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.ACCEPTED_STATUS, -1));
		lhr.addAll(holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.DENIED_STATUS, -1));
		return lhr;
	}
	
	/**
	 * Get all existing holiday request with a status of a specific user in the database for a specific year
	 * @return A list of all DTO associated to the existing holiday requests of a specific user in the database
	 */
	@GET
	@Path("/all/status/date")
	@Produces({MediaType.APPLICATION_JSON})
	public List<HolidayRequestDTO> displayHolidayRequestStatusPerYear(@QueryParam("year") int year)
	{
		List<HolidayRequestDTO> lhr = new ArrayList<HolidayRequestDTO>();
		lhr.addAll(holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.ACCEPTED_STATUS, year));
		lhr.addAll(holidayRequestService.getAllRequestsWithStatusForCurrentUser(HolidayRequest.DENIED_STATUS, year));
		return lhr;
	}
	
	/**
	 * Retrieve the first and last year of all holiday request for the current user
	 * @return A TimeInval containing first and last year
	 */
	@GET
	@Path("/years")
	@Produces({MediaType.APPLICATION_JSON})
	public IntervalDTO getYearCountForCurrentUser()
	{
		return holidayRequestService.getYearsCountForCurrentUser();
	}
	
	/**
	 * cancel a request
	 * @param idRequest
	 * @return ok
	 */
	@GET
	@Path("/cancel/{id}")
	@Produces({MediaType.TEXT_HTML})
	public Response denyHoliday(@PathParam("id")int idRequest){
		holidayRequestService.cancelRequest(idRequest);
		return Response.ok().build(); 			
	}
	
	@GET
	@Path("/balances")
	@Produces({MediaType.APPLICATION_JSON})
	public List<CalendarBalanceDetailDTO> getBalanceSummary(){
		return holidayRequestService.getBalanceSummaryForCurrentUser();
	}
	
}
