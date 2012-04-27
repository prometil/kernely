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
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.controller.AbstractController;
import org.kernely.template.SobaTemplateRenderer;

import com.google.inject.Inject;

/**
 * The controller of the main page
 */
@Path("/")
public class MainController extends AbstractController {
	
	@Inject
	private SobaTemplateRenderer templateRenderer;


	/**
	 * Get the main page of the application
	 * 
	 * @return The HTML corresponding to the main page of the application
	 */
	@GET
	@Produces({ MediaType.TEXT_HTML })
	public Response getUI() {
		Map<String, Object> map =new HashMap<String, Object>();
		return Response.ok(templateRenderer.render("templates/home.html", map)).build();
	}

}
