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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.kernely.controller.AbstractController;
import org.kernely.stream.dto.StreamCommentCreationRequestDTO;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamMessageCreationRequestDTO;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.model.Stream;
import org.kernely.stream.service.StreamService;
import org.kernely.template.SobaTemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The controller for stream pages
 */
@Path("/streams")
public class StreamController extends AbstractController {

	private static Logger log = LoggerFactory.getLogger(StreamController.class);

	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private StreamService streamService;

	/**
	 * Display the stream page
	 * 
	 * @return the html corresponding to the stream page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response display() {
		log.debug("Call to GET on streams");

		return Response.ok(templateRenderer.render("templates/streams.html")).build();
	}

	/**
	 * Gets a list of messages contained in a specific stream
	 * 
	 * @return a list of DTO corresponding to the messages in the specific
	 *         stream
	 */
	@GET
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamMessageDTO> getMessages() {
		return streamService.getMessages();
	}

	/**
	 * Gets all streams on which current user can write
	 * 
	 * @return A list of DTO associated to the streams on which current user
	 *         have at least write right
	 */
	@GET
	@Path("/combobox")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamDTO> getAuthorizedStreams() {
		List<StreamDTO> allStreams = streamService.getAllStreams();
		ArrayList<StreamDTO> authStreams = new ArrayList<StreamDTO>();
		for (StreamDTO streams : allStreams) {
			if (streamService.currentUserHasRightsOnStream(Stream.RIGHT_WRITE, (int) streams.id)
					|| streamService.currentUserHasRightsOnStream(Stream.RIGHT_DELETE, (int) streams.id)) {
				authStreams.add(streams);
			}
		}
		return authStreams;
	}

	/**
	 * adds a message to a stream
	 * 
	 * @param message
	 *            the request DTO associated to the message
	 * @return a DTO associated to the new message created
	 */
	@POST
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public StreamMessageDTO addMessage(StreamMessageCreationRequestDTO message) {

		log.debug("{} create a new message : {}", SecurityUtils.getSubject().getPrincipal(), message.message);
		log.debug("{} post in the stream with the id : {}", SecurityUtils.getSubject().getPrincipal(), message.idStream);

		return streamService.addMessage(message.message, message.idStream);
	}

	/**
	 * Adds a comment to a message
	 * 
	 * @param comment
	 *            The request DTO associated to the new comment
	 * @return A DTO associated to the new comment created
	 */
	@POST
	@Path("/comment")
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public StreamMessageDTO addComment(StreamCommentCreationRequestDTO comment) {

		log.debug("{} create a new comment : {}", SecurityUtils.getSubject().getPrincipal(), comment.message);
		log.debug("{} post in the message with the id : {}", SecurityUtils.getSubject().getPrincipal(), comment.idMessageParent);

		return streamService.addComment(comment.message, comment.idStream, comment.idMessageParent);
	}

	/**
	 * Deletes a message
	 * 
	 * @param id
	 *            The id of the message to delete
	 * @return The result of the operation
	 */
	@POST
	@Path("/delete/{id}")
	@Produces( { MediaType.APPLICATION_JSON })
	public String deleteMessage(@PathParam("id") int id) {
		log.trace("Delete message " + id);
		streamService.deleteMessage((int) id);
		return "{'result': 'ok'}";
	}

	/**
	 * Gets all streams on which current user has rights
	 * 
	 * @return A list of DTO associated to the streams on which current user has
	 *         rights
	 */
	@GET
	@Path("/current/all")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamDTO> getCurrentUserStreams() {
		return streamService.getCurrentUserStreams();
	}

	/**
	 * Gets all messages for the current user
	 * 
	 * @param flag
	 *            Id of the message where the research will begin
	 * @return A list of DTO associated to all messages of the current user
	 */
	@GET
	@Path("/current/messages")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamMessageDTO> getAllMessagesForCurrentUser(@QueryParam("last") int flag) {
		return streamService.getAllMessagesForCurrentUser(flag);
	}

	/**
	 * Gets all comments for a given message
	 * 
	 * @param id
	 *            The id of the message
	 * @return A list of DTO associated to the comments of the message
	 */
	@GET
	@Path("/{id}/comments")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<StreamMessageDTO> getAllCommentsForMessage(@PathParam("id") int id) {
		return streamService.getAllCommentsForMessage(id);
	}

	/**
	 * Gets the number of messages to display for the current user
	 * 
	 * @return The total of messages for the current user
	 */
	@GET
	@Path("/current/nb")
	@Produces( { MediaType.APPLICATION_JSON })
	public String getNbMessages() {
		Long count = streamService.getCurrentNbMessages();
		return "{\"count\":\"" + count + "\"}";
	}
}