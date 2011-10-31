/**
 * 
 */
package org.kernely.core.resources;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author g.breton
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
