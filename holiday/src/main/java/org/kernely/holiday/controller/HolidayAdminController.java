/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.kernely.holiday.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayProfileCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.dto.HolidayProfileUpdateUsersRequestDTO;
import org.kernely.holiday.dto.HolidayProfileUsersDTO;
import org.kernely.holiday.service.HolidayService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;
/**
 * Admin controller for holiday
 */
@Path("/admin/holiday")
public class HolidayAdminController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private UserService userService;
	
	@Inject
	private HolidayService holidayService ; 

	/**
	 * Set the template
	 * @return the page admin
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response getPluginAdminPanel(){
		Response page;
		if (userService.currentUserIsAdministrator()){
			page = Response.ok(templateRenderer.render("templates/holiday_profile_admin.html")).build();
		} else{
			page = Response.ok(templateRenderer.render("templates/home.html")).build();
		}
		return page;
	}
	
	/**
	 * Display the list of holiday profiles
	 * @return List of holiday 
	 */
	@GET
	@Path("/all")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public List<HolidayProfileDTO> displayAllHoliday(){
		log.debug("Call to GET on all holiday profiles");
		return holidayService.getAllProfiles();
	}

	/**
	 * Get a specific holiday profile.
	 * @return The holiday profile.
	 */
	@GET
	@Path("/{id}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public HolidayProfileDTO getHolidayProfile(@PathParam("id") long id){
		log.debug("Get holiday profile {}",id);
		return holidayService.getHolidayProfile(id);
	}
	
	/**
	 * Display the good unity when the administrator edit holidays
	 * @return the holiday
	 */
	@GET
	@Path("/combo/{holiday}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public HolidayDTO getComboUnity(@PathParam("holiday") int id ) {
		return holidayService.getHolidayDTO(id) ;		
	}

	/**
	 * Create a new holiday profile with the given informations
	 * @param holiday The DTO containing all informations about the new holiday profile
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public Response create(HolidayProfileCreationRequestDTO holiday){
		holidayService.createOrUpdateHolidayProfile(holiday);
		return Response.ok().build();
	}
	
	/**
	 * Create a new holiday type with the given informations
	 * @param holiday The DTO containing all informations about the new holiday type
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/createtype")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public HolidayDTO createType(HolidayCreationRequestDTO holiday){
		return holidayService.createOrUpdateHoliday(holiday);
	}
	
	/**
	 * Get all users and separates those who are associated to the profile and those who are not
	 * @param id the id of the concerned profile
	 * @return A JSON string containing the result of the operation
	 */
	@GET
	@Path("/profile/users")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces({MediaType.APPLICATION_JSON})
	public HolidayProfileUsersDTO getProfileUsers(@QueryParam("id") int id){
		List<UserDetailsDTO> in = holidayService.getUsersInProfile(id);
		List<UserDetailsDTO> out = holidayService.getUsersNotInProfile(id);
		log.debug("Profile {} is associated to {} users",id,in.size());
		return new HolidayProfileUsersDTO(id,in,out);
	}
	
	/**
	 * Create a holiday request
	 * @param request the holiday request creation DTO
	 * @return ok 
	 */
	@POST
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Path("/profile/users/update")
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateUsers(HolidayProfileUpdateUsersRequestDTO request){
		holidayService.updateProfileUsers(request.id,request.usernames);
		return Response.ok().build();
	}

}
