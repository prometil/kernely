package org.kernely.holiday.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kernely.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.holiday.dto.CalendarRequestDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller for the  manager  request view
 *
 */
@Path("/holiday/managers/request")
public class HolidayManagerRequestController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private HolidayRequestService holidayRequestService ; 
	
	/**
	 * Set the template
	 * @return the page 
	 */
	@GET
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Menu("request_management")
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayRequestPage(){
		return Response.ok(templateRenderer.render("templates/holiday_request_management.html")).build();
	}
	
	/**
	 * Get all existing holiday request  pending in the database
	 * @return A list of all DTO associated to the existing holiday requests in the database
	 */
	@GET
	@Path("/all/pending")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces({MediaType.APPLICATION_JSON})
	public List<HolidayRequestDTO> displayAllHolidayRequestPending()
	{
		log.debug("Call to GET on all holiday request pending");
		return holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.PENDING_STATUS);
	}
	
	/**
	 * Get all existing holiday request with a status in the database
	 * @return A list of all DTO associated to the existing holiday requests in the database
	 */
	@GET
	@Path("/all/status")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces({MediaType.APPLICATION_JSON})
	public List<HolidayRequestDTO> displayAllHolidayRequestStatus()
	{
		log.debug("Call to GET on all holiday request accepted or denied");
		List<HolidayRequestDTO> lhr = new ArrayList<HolidayRequestDTO>();
		lhr.addAll(holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.ACCEPTED_STATUS));
		lhr.addAll(holidayRequestService.getSpecificRequestsForManagers(HolidayRequest.DENIED_STATUS));
		return lhr; 
		
	}
	
	/**
	 * Accept a request
	 * @param idRequest
	 * @return ok
	 */
	@GET
	@Path("/accept/{id}")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces({MediaType.TEXT_HTML})
	public Response acceptHoliday(@PathParam("id")int idRequest){
		holidayRequestService.acceptRequest(idRequest);
		return Response.ok().build(); 			
	}
	
	/**
	 * deny a request
	 * @param idRequest
	 * @return ok
	 */
	@GET
	@Path("/deny/{id}")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces({MediaType.TEXT_HTML})
	public Response denyHoliday(@PathParam("id")int idRequest){
		holidayRequestService.denyRequest(idRequest);
		return Response.ok().build();			
	}
	
	/**
	 * comment a request
	 * @param idRequest
	 * @param the comment of the manager
	 * @return ok
	 */
	@GET
	@Path("/comment/{id}/{comment}")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces({MediaType.TEXT_HTML})
	public Response managerCommentHoliday(@PathParam("id")int idRequest, @PathParam("comment")String managerComment){
		holidayRequestService.addManagerCommentary(idRequest, managerComment);
		return Response.ok().build();			
	}
	
	/**
	 * Get the first and last date of the holiday request
	 * @param idRequest
	 * @return the firs element is the first date, the second is the second date
	 */
	@GET
	@Path("/get/{id}")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces(MediaType.APPLICATION_JSON)
	public List<HolidayDetailDTO> getDetailsInterval(@PathParam("id")int idRequest){
			List<HolidayDetailDTO> hddto =  holidayRequestService.getHolidayRequestDetails(idRequest);
			Date firstDay = hddto.get(0).day;
			Date lastDay = hddto.get(0).day;
			HolidayDetailDTO firstHoliday = hddto.get(0);
			HolidayDetailDTO lastHoliday = hddto.get(0);
			
			for (HolidayDetailDTO holiday : hddto){
				if (holiday.day.equals(firstHoliday.day) && (holiday.am)){
						firstHoliday = holiday;
				}
				if (holiday.day.before(firstDay)){
					firstDay = holiday.day;
					firstHoliday = holiday;
				}
				if (holiday.day.equals(lastHoliday.day) && (holiday.pm)){
						lastHoliday = holiday;
				}
				if (holiday.day.after(lastDay)){
					lastDay = holiday.day;
					lastHoliday = holiday;
				}
			}
			hddto.clear();
			hddto.add(firstHoliday);
			hddto.add(lastHoliday);
			return hddto;
	}
	
	/**
	 * Get the holiday request details by order (first date to last date)
	 * @param idRequest
	 * @return The list ordered of the holiday request details
	 */
	@GET
	@Path("/details/{id}")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces(MediaType.APPLICATION_JSON)
	public List<HolidayDetailDTO> getDetails(@PathParam("id")int idRequest){
		return holidayRequestService.getHolidayRequestDetailsByOrder(idRequest);
	}
	
	/**
	 * Construct the calendar in function of the begin date and end date   
	 * @param dateBegin
	 * @param dateEnd
	 * @return Calendar request
	 */
	@GET
	@Path("construct")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces(MediaType.APPLICATION_JSON)
	public CalendarRequestDTO constructCalendar(@QueryParam("dateBegin")String dateBegin,@QueryParam("dateEnd")String dateEnd){
		DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
		DateTime d1 = DateTime.parse(dateBegin, fmt);
		DateTime d2 = DateTime.parse(dateEnd, fmt);
		return holidayRequestService.getCalendarRequest(d1, d2);
	}
}
