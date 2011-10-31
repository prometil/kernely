/**
 * 
 */
package org.kernely.core.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.template.TemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author g.breton
 * 
 */

@Path("/")
public class MainController extends AbstractController {
	public static final Logger log = LoggerFactory.getLogger(MainController.class);

	@Inject
	private TemplateRenderer templateRenderer;

	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getUI() {
		String URL = new String("/templates/gsp/home.gsp");
		return templateRenderer.create(URL).render();

	}

}
