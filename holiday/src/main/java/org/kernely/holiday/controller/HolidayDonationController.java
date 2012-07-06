package org.kernely.holiday.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayDonationDTO;
import org.kernely.holiday.dto.HolidayTypeDTO;
import org.kernely.holiday.service.HolidayDonationService;
import org.kernely.holiday.service.HolidayService;
import org.kernely.menu.Menu;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

@Path("/holiday/donation")
public class HolidayDonationController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private HolidayDonationService holidayDonationService ;
	
	@Inject
	private UserService userService;
	
	@Inject
	private HolidayService holidayService;
	
	@GET
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Menu("holiday_donation")
	@Produces( { MediaType.TEXT_HTML })
	public Response getHolidayDonationPage(){
		return Response.ok(templateRenderer.render("templates/holiday_donation.html")).build();
	}
	
	@GET
	@Path("list")
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces( { MediaType.APPLICATION_JSON })
	public List<HolidayDonationDTO> getHolidayDonationListForThisManager(){
		return holidayDonationService.getAllDonationForCurrentManager();
	}
	
	@GET
	@Path("users")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_USERMANAGER)
	public List<UserDTO> getListManaged() {
		List<UserDTO> list =  new ArrayList<UserDTO>();
		list.addAll(userService.getUsersAuthorizedManaged());
		Collections.sort(list);
		return list;
	}
	
	@GET
	@Path("/balances")
	@Produces({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_USERMANAGER)
	public List<HolidayTypeDTO> getUserBalances(@QueryParam("userId") long userId) {
		return holidayService.getAllTypeInstanceForUser(userId, false, false);
	}
	
	@POST
	@Path("create")
	@Consumes({ MediaType.APPLICATION_JSON })
	@RequiresRoles(Role.ROLE_USERMANAGER)
	@Produces( { MediaType.TEXT_HTML })
	public Response createNewDonation(HolidayDonationDTO donationRequest) {
		HolidayDonationDTO dto = holidayDonationService.createDonation(donationRequest);
		if(dto != null){
			return Response.ok().build();
		}
		else{
			return Response.serverError().build();
		}
	}
	
}
