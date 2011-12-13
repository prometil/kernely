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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.stream.dto.RightOnStreamDTO;
import org.kernely.stream.dto.StreamCreationRequestDTO;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamRightsUpdateRequestDTO;
import org.kernely.stream.model.Stream;
import org.kernely.stream.service.StreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

@Path("/admin/streams")
public class StreamAdminController extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(StreamAdminController.class);

	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

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
	@Produces( { MediaType.TEXT_HTML })
	public String getPluginAdminPanel() {
		String page;
		if (userService.currentUserIsAdministrator()) {
			page = templateRenderer.create("/templates/gsp/streams_admin.gsp").withLayout(TemplateRenderer.ADMIN_LAYOUT).render();
		} else {
			page = templateRenderer.create("/templates/gsp/home.gsp").render();
		}
		log.debug("getting stream administration main page");
		return page;
	}

	/**
	 * Gets all existing streams in the database
	 * 
	 * @return A list of DTO associated to the Streams
	 */
	@GET
	@Path("/all")
	@Produces( { "application/json" })
	public List<StreamDTO> displayAllStreams() {
		if (userService.currentUserIsAdministrator()) {
			log.debug("Call to GET on all streams");
			List<StreamDTO> streams = streamService.getAllStreams();
			return streams;
		} else {
			return null;
		}
	}

	/**
	 * Create a new group with the given informations
	 * 
	 * @return "Ok", not useful.
	 */
	@POST
	@Path("/create")
	@Produces( { MediaType.APPLICATION_JSON })
	public String create(StreamCreationRequestDTO stream) {
		log.debug("Create a user");

		if (stream.id == 0) {
			try {
				streamService.createStream(stream.name, stream.category);
			} catch (IllegalArgumentException iae) {
				log.debug(iae.getMessage());
				return "{\"result\":\"" + iae.getMessage() + "\"}";
			}
		} else {
			streamService.updateStream(stream);
		}
		return "{\"result\":\"ok\"}";
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
	@Produces( { MediaType.APPLICATION_JSON })
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
	@Produces( { MediaType.APPLICATION_JSON })
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
	@Produces( { MediaType.APPLICATION_JSON })
	public String getStreamRights(@PathParam("id") int id) {
		String json = "{\"permission\":[";
		List<UserDTO> allUsers = userService.getAllUsers();

		for (UserDTO user : allUsers) {
			boolean read = permissionService.userHasPermission((int) user.id, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, id);
			boolean write = permissionService.userHasPermission((int) user.id, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, id);
			boolean delete = permissionService.userHasPermission((int) user.id, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, id);

			if (delete) {
				json += "{\"user\":" + user.id + ",\"right\":\"delete\"},";
			} else if (write) {
				json += "{\"user\":" + user.id + ",\"right\":\"write\"},";
			} else if (read) {
				json += "{\"user\":" + user.id + ",\"right\":\"read\"},";
			}
		}
		json = json.substring(0, json.length() - 1);
		json += "]}";
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
	@Produces( { MediaType.APPLICATION_JSON })
	public String updateRights(StreamRightsUpdateRequestDTO request) {
		log.debug("Update rights of the stream : {}", request.streamid);
		for (RightOnStreamDTO right : request.rights) {
			boolean correct = permissionService.userHasPermission(right.userid, right.permission, Stream.STREAM_RESOURCE, request.streamid);
			if (!correct) {
				// The right requested is not the same than the existing right :
				// delete permissions on the stream for the user
				if (permissionService.userHasPermission((int) right.userid, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, request.streamid)) {
					permissionService.ungrantPermission(right.userid, Stream.RIGHT_READ, Stream.STREAM_RESOURCE, request.streamid);
				}
				if (permissionService.userHasPermission((int) right.userid, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, request.streamid)) {
					permissionService.ungrantPermission(right.userid, Stream.RIGHT_WRITE, Stream.STREAM_RESOURCE, request.streamid);
				}
				if (permissionService.userHasPermission((int) right.userid, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, request.streamid)) {
					permissionService.ungrantPermission(right.userid, Stream.RIGHT_DELETE, Stream.STREAM_RESOURCE, request.streamid);
				}
				if (!right.permission.equals("nothing")) {
					// Add the requested permission
					permissionService.grantPermission(right.userid, right.permission, Stream.STREAM_RESOURCE, request.streamid);
				}
			}
		}
		return "{\"result\":\"ok\"}";
	}

}