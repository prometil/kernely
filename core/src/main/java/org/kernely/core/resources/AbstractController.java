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
package org.kernely.core.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public abstract class AbstractController {

	protected  final Logger log = LoggerFactory.getLogger(this.getClass());
	
	public Response redirect (String uri){
		try {
			return Response.temporaryRedirect(new URI("uri")).build();
		} catch (URISyntaxException e) {
			log.error("Invalid redirect url");
			return null;
		}
	}
}
