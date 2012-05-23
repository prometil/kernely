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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.GroupCreationRequestDTO;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.GroupService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The controller of the group administration page
 */
@Path("/admin/groups")
public class GroupAdminController extends AbstractController {

	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private GroupService groupService;
	
	/**
	 * Get all existing groups in the database
	 * @return A list of all DTO associated to the existing groups in the database
	 */
	@GET
	@Path("/all")
	@Produces({MediaType.APPLICATION_JSON})
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public List<GroupDTO> displayAllGroups()
	{
		log.debug("Call to GET on all groups");
		return groupService.getAllGroups();
	}
	
	/**
	 * Display the gStringroup administration page
	 * @return the group administration page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response displayPage()
	{
		Map<String, Object> map =new HashMap<String, Object>();
		Response page;
		// Display the admin page only if the user is admin.
		page = Response.ok(templateRenderer.render("templates/admin/group_admin.html", map)).build();
		return page;
	}
	
	/**
	 * Create a new group with the given informations
	 * @param group The DTO containing all informations about the new group
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({MediaType.APPLICATION_JSON})
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public String create(GroupCreationRequestDTO group)
	{
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
	
	/**
	 * Delete the group which has the id 'id'
	 * @param id The id of the group to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{id}")
	@Produces( { MediaType.TEXT_HTML })
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public Response lock(@PathParam("id") int id){
		groupService.deleteGroup(id);
		return Response.ok().build();
	}
	
	/**
	 * Get all users associated to the group which has the id 'id'
	 * @param id The id of the group
	 * @return A list of all DTO associated to the users contained in this group
	 */
	@GET
	@Path("/{id}/users")
	@Produces({MediaType.APPLICATION_JSON})
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public List<UserDTO> getGroupUsers(@PathParam("id") int id){
		return groupService.getGroupUsers(id);
	}
	
	/**
	 * Get a group.
	 * @param id The id of the group
	 * @return The group DTO.
	 */
	@GET
	@Path("/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	public GroupDTO getGroup(@PathParam("id") long id){
		return groupService.getGroup(id);
	}

}
