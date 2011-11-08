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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.resources.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.stream.dto.StreamMessageCreationRequestDTO;
import org.kernely.stream.dto.StreamMessageDTO;
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
		return streamService.addMessage(message.message);
	}
}
