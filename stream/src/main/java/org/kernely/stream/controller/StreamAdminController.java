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
package org.kernely.stream.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.kernely.controller.AbstractController;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.service.GroupService;
import org.kernely.core.service.PermissionService;
import org.kernely.core.service.UserService;
import org.kernely.stream.dto.RightOnStreamDTO;
import org.kernely.stream.dto.StreamCreationRequestDTO;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamRightsUpdateRequestDTO;
import org.kernely.stream.model.Stream;
import org.kernely.stream.service.StreamService;
import org.kernely.template.SobaTemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Controller for the stream admin page
 */
@Path("/admin/streams")
public class StreamAdminController extends AbstractController {

	private static Logger log = LoggerFactory.getLogger(StreamAdminController.class);

	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject
	private GroupService groupService;

	@Inject
	private PermissionService permissionService;

	@Inject
	private StreamService streamService;

	/**
	 * Gets the administration page for the stream
	 * 
	 * @return the html corresponding to the page
	 */
	@GET
	@Path("/main")
	@Produces({ MediaType.TEXT_HTML })
	public Response getPluginAdminPanel() {
		log.debug("getting stream administration main page");
		if (userService.currentUserIsAdministrator()) {
			return Response.ok(templateRenderer.render("templates/streams_admin.html")).build();
		} else {
			return Response.status(Status.FORBIDDEN).build();
		}
	}

	/**
	 * Gets all existing streams in the database
	 * 
	 * @return A list of DTO associated to the Streams
	 */
	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON})
	public List<StreamDTO> displayAllStreams() {
		if (userService.currentUserIsAdministrator()) {
			log.debug("Call to GET on all streams");
			return streamService.getAllStreams();
			
		} else {
			return new ArrayList<StreamDTO>();
		}
	}

	/**
	 * Create a new group with the given informations
	 * 
	 * @return "Ok", not useful.
	 */
	@POST
	@Path("/create")
	@Produces({ MediaType.APPLICATION_JSON })
	public String create(StreamCreationRequestDTO stream) {
		log.debug("Create a user");

		if (userService.currentUserIsAdministrator()){
			try {
				streamService.createStream(stream.name, stream.category);
			} catch (IllegalArgumentException iae) {
				log.debug(iae.getMessage());
				return "{\"result\":\"" + iae.getMessage() + "\"}";
			}
		}
		
		return "{\"result\":\"ok\"}";
	}
	
	/**
	 * update a stream with the given informations
	 * 
	 * @return "Ok", not useful.
	 */
	@POST
	@Path("/update")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response update(StreamCreationRequestDTO stream) {
		if (userService.currentUserIsAdministrator()){
			streamService.updateStream(stream);
			return Response.ok().build();
		}
		return Response.status(Status.FORBIDDEN).build();
	}

	/**
	 * Locks a stream
	 * 
	 * @param id
	 *            The id of the stream to lock
	 * @return The result of the operation
	 */
	@GET
	@Path("/lock/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String lock(@PathParam("id") int id) {
		if (userService.currentUserIsAdministrator()) {
			streamService.lockStream(id);
			return "{\"result\":\"ok\"}";
		}
		return "{\"result\":\"You are not administrator\"}";
	}

	/**
	 * Unlocks a stream
	 * 
	 * @param id
	 *            The id of the stream to unlock
	 * @return The result of the operation
	 */
	@GET
	@Path("/unlock/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String unlock(@PathParam("id") int id) {
		if (userService.currentUserIsAdministrator()) {
			streamService.unlockStream(id);
			return "{\"result\":\"ok\"}";
		}
		return "{\"result\":\"You are not administrator\"}";
	}

	/**
	 * Gets all rights associated to a stream
	 * 
	 * @param id
	 *            The id of the stream
	 * @return A JSON String containing the rights of all users for the stream
	 */
	@GET
	@Path("/rights/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public String getStreamRights(@PathParam("id") int id) {
		StringBuilder jsonBuilder = new StringBuilder();
		jsonBuilder.append("{\"permission\":[");
		List<UserDTO> allUsers = userService.getAllUsers();
		List<GroupDTO> allGroups = groupService.getAllGroups();

		for (UserDTO user : allUsers) {
			boolean read = permissionService.userHasPermission((int) user.id, false, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, id);
			boolean write = permissionService.userHasPermission((int) user.id, false, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, id);
			boolean delete = permissionService.userHasPermission((int) user.id, false, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, id);

			if (delete) {
				jsonBuilder.append("{\"user\":");
				jsonBuilder.append(user.id);
				jsonBuilder.append(",\"right\":\"delete\"},");
			} else if (write) {
				jsonBuilder.append("{\"user\":");
				jsonBuilder.append(user.id);
				jsonBuilder.append(",\"right\":\"write\"},");
			} else if (read) {
				jsonBuilder.append("{\"user\":");
				jsonBuilder.append(user.id);
				jsonBuilder.append(",\"right\":\"read\"},");
			}
		}
		for (GroupDTO group : allGroups) {
			boolean read = permissionService.groupHasPermission((int) group.id, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, id);
			boolean write = permissionService.groupHasPermission((int) group.id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, id);
			boolean delete = permissionService.groupHasPermission((int) group.id, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, id);

			if (delete) {
				jsonBuilder.append("{\"group\":");
				jsonBuilder.append(group.id);
				jsonBuilder.append(",\"right\":\"delete\"},");
			} else if (write) {
				jsonBuilder.append("{\"group\":");
				jsonBuilder.append(group.id);
				jsonBuilder.append(",\"right\":\"write\"},");
			} else if (read) {
				jsonBuilder.append("{\"group\":");
				jsonBuilder.append(group.id);
				jsonBuilder.append(",\"right\":\"read\"},");
			}
		}
		String json = jsonBuilder.toString(); 
		json = json.substring(0, json.length() - 1);
		json += "]}";
		log.debug(json);
		return json;
	}

	/**
	 * Updates rights about a stream
	 * 
	 * @param request
	 * @return
	 */
	@POST
	@Path("/updaterights")
	@Produces({ MediaType.APPLICATION_JSON })
	public String updateRights(StreamRightsUpdateRequestDTO request) {
		log.debug("Update {} rights of the stream : {}", request.rights.size(),request.streamid);
		for (RightOnStreamDTO right : request.rights) {
			if (right.idType.equals("user")){
				log.debug("Right {} for user with id {}",right.permission,right.id);
				boolean correct = permissionService.userHasPermission(right.id, false, right.permission, Stream.STREAM_RESOURCE, request.streamid);
				if (!correct) {
					// The right requested is not the same than the existing right :
					// delete permissions on the stream for the user
					if (permissionService.userHasPermission((int) right.id, false, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, request.streamid)) {
						permissionService.ungrantPermission(right.id, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, request.streamid);
					}
					if (permissionService.userHasPermission((int) right.id, false, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, request.streamid)) {
						permissionService.ungrantPermission(right.id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, request.streamid);
					}
					if (permissionService.userHasPermission((int) right.id, false, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, request.streamid)) {
						permissionService.ungrantPermission(right.id, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, request.streamid);
					}
					if (!right.permission.equals("nothing")) {
						// Add the requested permission
						permissionService.grantPermission(right.id, right.permission, Stream.STREAM_RESOURCE, request.streamid);
					}
				}
			} else if(right.idType.equals("group")){
				log.debug("Right {} for group with id {}",right.permission,right.id);
				boolean correct = permissionService.groupHasPermission(right.id, right.permission, Stream.STREAM_RESOURCE, request.streamid);
				if (!correct) {
					// The right requested is not the same than the existing right :
					// delete permissions on the stream for the user
					if (permissionService.groupHasPermission((int) right.id, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, request.streamid)) {
						permissionService.ungrantPermissionForGroup(right.id, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, request.streamid);
					}
					if (permissionService.groupHasPermission((int) right.id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, request.streamid)) {
						permissionService.ungrantPermissionForGroup(right.id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, request.streamid);
					}
					if (permissionService.groupHasPermission((int) right.id, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, request.streamid)) {
						permissionService.ungrantPermissionForGroup(right.id, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, request.streamid);
					}
					if (!right.permission.equals("nothing")) {
						// Add the requested permission
						permissionService.grantPermissionToGroup(right.id, right.permission, Stream.STREAM_RESOURCE, request.streamid);
					}
				}
			}
		}
		return "{\"result\":\"ok\"}";
	}

	/**
	 * Display the good category when the administrator edit streams
	 * @return
	 */
	@GET
	@Path("/combo/{stream}")
	@Produces({ MediaType.APPLICATION_JSON })
	public StreamDTO getComboCategory(@PathParam("stream") int id) {
		if (userService.currentUserIsAdministrator()) {
			return streamService.getStream(id);
		}
		return new StreamDTO();
	}

}