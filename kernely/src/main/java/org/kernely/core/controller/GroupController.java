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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.controller.AbstractController;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.service.GroupService;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The controller of the group page
 */
@Path("/group")
public class GroupController extends AbstractController{
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private GroupService groupService;
	
	/**
	 * Display the list of groups.
	 * @return The html content to display the list.
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getText()
	{
		log.debug("Call to GET on all groups");
		Map<String,Object> map = new HashMap<String,Object>();
		List<GroupDTO> groups = groupService.getAllGroups();
		map.put("groups", groups);
		return Response.ok(templateRenderer.render("templates/groups.html", map)).build();
	}
}
