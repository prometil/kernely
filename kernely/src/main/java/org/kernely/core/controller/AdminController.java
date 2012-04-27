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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.controller.AbstractController;
import org.kernely.core.dto.PluginDTO;
import org.kernely.core.service.UserService;
import org.kernely.plugin.AbstractPlugin;
import org.kernely.plugin.PluginManager;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * Controller of the administration.
 */
@Path("/admin")
public class AdminController extends AbstractController {

	@Inject
	private SobaTemplateRenderer templateRenderer;

	@Inject
	private UserService userService;

	@Inject
	private PluginManager pluginsLoader;

	/**
	 * Display the administration panel.
	 * 
	 * @return The html content to display the administration.
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getAdmin() {
		Response page;

		Map<String, Object> map =new HashMap<String, Object>();
		
		// Display the admin page only if the user is admin.
		if (userService.currentUserIsAdministrator()) {
			page = Response.ok(templateRenderer.render("templates/admin.html", map)).build();
		} else {
			page = Response.ok(templateRenderer.render("templates/home.html",map)).build();
		}
		return page;
	}

	/**
	 * Get a list of all plugins detected by the application
	 * 
	 * @return A list of all DTO associated to detected plugins
	 */
	@GET
	@Path("/plugins")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<PluginDTO> getAdminList() {
		List<PluginDTO> plugins = new ArrayList<PluginDTO>();
		for (AbstractPlugin plugin : pluginsLoader.getPlugins()) {
			PluginDTO dto = new PluginDTO(plugin.getMenus(), plugin.getPath(), "", plugin.getAdminPages());
			plugins.add(dto);
		}
		return plugins;
	}


}
