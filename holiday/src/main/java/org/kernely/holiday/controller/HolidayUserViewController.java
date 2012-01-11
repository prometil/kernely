package org.kernely.holiday.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.service.HolidayRequestService;

import com.google.inject.Inject;


/**
 * Controller for the  manager  request view
 * @author b.grandperret
 *
 */
@Path("/holiday/users/request")
public class HolidayUserViewController extends AbstractController{
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private HolidayRequestService holidayRequestService ;
	
	/**
	 * Set the template
	 * @return the page 
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayRequestPage(){
		return ok(templateRenderer.create("/templates/gsp/holiday_user_request.gsp").addCss("/css/holiday_user_request.css"));
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
		List<HolidayRequestDTO> userRequest = holidayRequestService.getAllRequestsForCurrentUser(); 
		List<HolidayRequestDTO> pendingRequest = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		List<HolidayRequestDTO> userPendingRequest = new ArrayList<HolidayRequestDTO>();
		for (HolidayRequestDTO hrdto : userRequest){
			if (pendingRequest.contains(hrdto)){
				userPendingRequest.add(hrdto);
			}
		}
		return userPendingRequest;
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
		List<HolidayRequestDTO> userRequest = holidayRequestService.getAllRequestsForCurrentUser();
		List<HolidayRequestDTO> lhr = new ArrayList<HolidayRequestDTO>();
		lhr.addAll(holidayRequestService.getAllRequestsWithStatus(HolidayRequest.ACCEPTED_STATUS));
		lhr.addAll(holidayRequestService.getAllRequestsWithStatus(HolidayRequest.DENIED_STATUS));
		List<HolidayRequestDTO> userStatuedRequest = new ArrayList<HolidayRequestDTO>();
		for (HolidayRequestDTO hrdto : userRequest){
			if (lhr.contains(hrdto)){
				userStatuedRequest.add(hrdto);
			}
		}
		return userStatuedRequest;
	}
	
	
	
}
