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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.template.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 */

@Path("/")
public class MainController extends AbstractController {
	public static final Logger log = LoggerFactory.getLogger(MainController.class);

	@Inject
	private TemplateRenderer templateRenderer;

	/**
	 * Get the main page of the application
	 * @return The HTML corresponding to the main page of the application
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getUI() {
		String URL = new String("/templates/gsp/home.gsp");
		return templateRenderer.create(URL).render();

	}

}
