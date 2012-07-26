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

package org.kernely.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Handle the server response
 *
 */
public abstract class AbstractController {

	protected  final Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Redirect the response to a new URI
	 * @param uri
	 * @return response
	 */
	public Response redirect (String uri){
		try {
			return Response.temporaryRedirect(new URI(uri)).build();
		} catch (URISyntaxException e) {
			log.error("Cannot redirect, invalid url: {}", uri);
			return null;
		}
	}
}
