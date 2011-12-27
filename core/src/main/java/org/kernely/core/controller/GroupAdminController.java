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
package org.kernely.core.controller;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.dto.GroupCreationRequestDTO;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.service.user.GroupService;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;

@Path("/admin/groups")
public class GroupAdminController extends AbstractController {

	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private GroupService groupService;
	
	@Inject
	private UserService userService;
	
	/**
	 * Get all existing groups in the database
	 * @return A list of all DTO associated to the existing groups in the database
	 */
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<GroupDTO> displayAllGroups()
	{
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all users");
			return groupService.getAllGroups();
		}
		return null;
	}
	
	/**
	 * Display the gStringroup administration page
	 * @return the group administration page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response displayPage()
	{
		if (userService.currentUserIsAdministrator()){
			return ok(templateRenderer.create("/templates/gsp/administration/group_admin.gsp").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		}
		return ok(templateRenderer.create("/templates/gsp/home.gsp"));
	}
	
	/**
	 * Create a new group with the given informations
	 * @param group The DTO containing all informations about the new group
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({"application/json"})
	public String create(GroupCreationRequestDTO group)
	{
		
		if (userService.currentUserIsAdministrator()){
			try{
				log.debug("Create a user");
				if(group.id == 0){
					groupService.createGroup(group);
				}
				else{
					groupService.updateGroup(group);
				}
				return "{\"result\":\"ok\"}";
			} catch (IllegalArgumentException iae) {
				log.debug(iae.getMessage());
				return "{\"result\":\""+iae.getMessage()+"\"}";
			}
		}
		return null;
	}
	
	/**
	 * Delete the group which has the id 'id'
	 * @param id The id of the group to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@Produces( { MediaType.TEXT_HTML })
	public String lock(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			groupService.deleteGroup(id);
			return "Ok";
		}
		return null;
	}
	
	/**
	 * Get all users associated to the group which has the id 'id'
	 * @param id The id of the group
	 * @return A list of all DTO associated to the users contained in this group
	 */
	@GET
	@Path("/{id}/users")
	@Produces({"application/json"})
	public List<UserDTO> getGroupUsers(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			return groupService.getGroupUsers(id);
		}
		return null;
	}

}
