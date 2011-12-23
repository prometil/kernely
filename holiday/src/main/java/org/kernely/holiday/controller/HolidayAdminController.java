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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayUpdateRequestDTO;
import org.kernely.holiday.service.HolidayService;

import com.google.inject.Inject;
/**
 * 
 * @author b.grandperret
 *
 */
@Path("/admin/holiday")
public class HolidayAdminController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private UserService userService;
	
	@Inject
	private HolidayService holidayService ; 

	/**
	 * set the template
	 * @return the pae admin
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getPluginAdminPanel(){
		Response page;
		if (userService.currentUserIsAdministrator()){
			page = ok(templateRenderer.create("/templates/gsp/holiday_admin.gsp").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		} else{
			page = ok(templateRenderer.create("/templates/gsp/home.gsp"));
		}

		return page;
	}
	/**
	 * display the list of holiday for the table
	 * @return
	 */
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<HolidayDTO> displayAllHoliday(){
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all holidays");
			List<HolidayDTO> holidays = holidayService.getAllHoliday();
			return holidays;
		}
		return null;
	}
	
	/**
	 * Display the good unity when the administrator edit holidays
	 * @return
	 */
	@GET
	@Path("/combo/{holiday}")
	@Produces({"application/json"})
	public HolidayDTO getComboUnity(@PathParam("holiday") int id ) {
		if (userService.currentUserIsAdministrator()){
			HolidayDTO hdto= holidayService.getHolidayDTO(id) ;
			return hdto;		
		}
		return null;
	}

	/**
	 * Delete the holiday which has the id 'id'
	 * @param id The id of the holiday to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@Produces( { MediaType.TEXT_HTML })
	public String deleteHoliday(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			holidayService.deleteHoliday(id);
			return "{\"result\":\"Ok\"}"; 
		}
		return null;
	}
	
	/**
	 * Create a new holiday with the given informations
	 * @param holiday The DTO containing all informations about the new holiday
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({"application/json"})
	public String create(HolidayCreationRequestDTO holiday){
		if (userService.currentUserIsAdministrator()){
			holidayService.createHoliday(holiday);
			return "{\"result\":\"Ok\"}"; 
		}
		return null;		
	}
	

	/**
	 *  Update a new holiday with the given informations
	 * @param holiday The DTO containing all informations about the new holiday
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/update")
	@Produces({"application/json"})
	public String update(HolidayUpdateRequestDTO holiday){
		if (userService.currentUserIsAdministrator()){
			holidayService.updateHoliday(holiday);
			return "{\"result\":\"Ok\"}"; 
		}
		return null;
	}
}
