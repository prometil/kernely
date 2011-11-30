/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.stream.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.resources.AbstractController;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.stream.dto.RightOnStreamDTO;
import org.kernely.stream.dto.StreamCreationRequestDTO;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamMessageCreationRequestDTO;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.dto.StreamRightsUpdateRequestDTO;
import org.kernely.stream.model.Stream;
import org.kernely.stream.service.StreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

@Path("/streams")
public class StreamResource extends AbstractController {

	private static final Logger log = LoggerFactory.getLogger(StreamResource.class);

	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private StreamService streamService;
	
	@Inject
	private UserService userService;

	@Inject
	private PermissionService permissionService;

	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String display() {
		log.debug("Call to GET on streams");
		
		return templateRenderer.create("/templates/gsp/streams.gsp").addCss("/css/stream.css").render();
	}

	@GET
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamMessageDTO> getMessages() {
		return streamService.getMessages();
	}

	@POST
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public StreamMessageDTO addMessage(StreamMessageCreationRequestDTO message) {

		log.debug("{} create a new message : {}", SecurityUtils.getSubject().getPrincipal(), message.message);
		return streamService.addMessage(message.message,1);
	}
	
	@GET
	@Path("/admin")
	@Produces( { MediaType.TEXT_HTML })
	public String getPluginAdminPanel(){
		String page;
		if (userService.currentUserIsAdministrator()){
			page = templateRenderer.create("/templates/gsp/streams_admin.gsp").withoutLayout().render();
		} else{
			page = templateRenderer.create("/templates/gsp/home.gsp").render();
		}
		return page;
	}
	
	@GET
	@Path("/admin/all")
	@Produces({"application/json"})
	public List<StreamDTO> displayAllStreams()
	{
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all streams");
			List<StreamDTO> streams= streamService.getAllStreams();
			return streams;
		} else {
			return null;
		}
	}
	
	/**
	 * Create a new group with the given informations
	 * @return "Ok", not useful.
	 */
	@POST
	@Path("/admin/create")
	@Produces( { MediaType.APPLICATION_JSON })
	public String create(StreamCreationRequestDTO stream)
	{
		log.debug("Create a user");
		
		if(stream.id == 0){
			try {
				streamService.createStream(stream.name,stream.category);
			} catch (IllegalArgumentException iae) {
				log.debug(iae.getMessage());
			    return "{\"result\":\""+iae.getMessage()+"\"}";
			}
		}
		else{
			streamService.updateStream(stream);
		}
		return "{\"result\":\"ok\"}";
	}
	
	@GET
	@Path("/admin/lock/{id}")
	@Produces( { MediaType.TEXT_HTML })
	public String lock(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			streamService.lockStream(id);
			return "Ok";
		}
		return "";
	}

	@GET
	@Path("/admin/unlock/{id}")
	@Produces( { MediaType.TEXT_HTML })
	public String unlock(@PathParam("id") int id){
		if (userService.currentUserIsAdministrator()){
			streamService.unlockStream(id);
		return "Ok";
		}
		return "";
	}
	

	/**
	 * Gets rights associated to a stream.
	 */
	@GET
	@Path("/admin/rights/{id}")
	@Produces({MediaType.APPLICATION_JSON})
	public String getStreamRights(@PathParam("id") int id){
		log.debug("JSON IN CONSTRUCTION FOR THE STREAM " + id);
		String json = "{\"permission\":[";
		List<UserDTO> allUsers = userService.getAllUsers();
		
		for (UserDTO user : allUsers){
			boolean read = permissionService.userHasPermission((int) user.id, Stream.RIGHT_READ+":streams:"+id);
			boolean write = permissionService.userHasPermission((int) user.id, Stream.RIGHT_WRITE+":streams:"+id);
			boolean delete = permissionService.userHasPermission((int) user.id, Stream.RIGHT_DELETE+":streams:"+id);
			
			if (delete){
				json += "{\"user\":"+user.id+",\"right\":\"delete\"},";
			} else if (write) {
				json += "{\"user\":"+user.id+",\"right\":\"write\"},";
			} else if (read) {
				json += "{\"user\":"+user.id+",\"right\":\"read\"},";
			}
		}
		json = json.substring(0, json.length() -1);
		json += "]}";
		return json;
	}
	
	@POST
	@Path("/admin/updaterights")
	@Produces( { MediaType.APPLICATION_JSON })
	public String updateRights(StreamRightsUpdateRequestDTO request) {
		log.debug("{} udate rights of the stream : {}", request.streamid);
		for (RightOnStreamDTO right : request.rights){
			boolean correct = permissionService.userHasPermission(right.userid,right.permission+":streams:"+request.streamid);
			if (!correct){
				// The right requested is not the same than the existing right : delete permissions on the stream for the user
				if (permissionService.userHasPermission((int) right.userid, Stream.RIGHT_READ+":streams:"+request.streamid)){
					permissionService.ungrantPermission(right.userid, Stream.RIGHT_READ+":streams:"+request.streamid);
				}
				if (permissionService.userHasPermission((int) right.userid, Stream.RIGHT_WRITE+":streams:"+request.streamid)){
					permissionService.ungrantPermission(right.userid, Stream.RIGHT_WRITE+":streams:"+request.streamid);
				}
				if (permissionService.userHasPermission((int) right.userid, Stream.RIGHT_DELETE+":streams:"+request.streamid)){
					permissionService.ungrantPermission(right.userid, Stream.RIGHT_DELETE+":streams:"+request.streamid);
				}
				if (! right.permission.equals("nothing")){
					// Add the requested permission
					permissionService.grantPermission(right.userid, right.permission +":streams:"+request.streamid);
				}
			}
		}
		return "{\"result\":\"ok\"}";
	}
	
	@GET
	@Path("/current/all")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamDTO> getCurrentUserStreams(){
		return streamService.getCurrentUserStreams();
	}
	
	@GET
	@Path("/current/messages")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamMessageDTO> getAllMessagesForCurrentUser(){
		return streamService.getAllMessagesForCurrentUser();
	}
}
