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
package org.kernely.user.resources;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.resources.AbstractController;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.user.dto.UserCreationRequestDTO;
import org.kernely.user.dto.UserDTO;
import org.kernely.user.service.UserService;

import com.google.inject.Inject;

/**
 * Controler of the user plugin.
 * @author jerome
 */
@Path("/user")
public class UserController  extends AbstractController{
	
	

	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private UserService userService;

	/**
	 * Display the list of users.
	 * @return The html content to display the list.
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getText()
	{
		log.debug("Call to GET on all users");
		List<UserDTO> users = userService.getAllUsers();
		return templateRenderer.create("/templates/gsp/users.gsp").with("users", users).render() ;
	}

	/**
	 * Create a new user with a random username and as password : "password".
	 * @return "Ok", not useful.
	 */
	@GET
	@Path("/create")
	@Produces( { MediaType.TEXT_PLAIN })
	public String create()
	{
		log.debug("Create a user");
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = UUID.randomUUID().toString();
		request.password = "password";
		userService.createUser(request);
		return "Ok";
	}

	/**
	 * Displays the login page.
	 * @return The html content of the login page.
	 */
	@GET
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public String login() {
		return templateRenderer.create("/templates/gsp/login.gsp").withoutLayout().render();
	}

	/**
	 * Displays the login page.
	 * @return The html content of the login page.
	 */
	@POST
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public String postLogin() {
		log.info("Login attempt : is authenticated {}", SecurityUtils.getSubject().isAuthenticated());
		
		return templateRenderer.create("/templates/gsp/login.gsp").withoutLayout().render();
	}
	
	/**
	 * Logs out the user and redirect to the main page.
	 * @return a redirection to the main page;
	 */
	@GET
	@Path("/logout")
	@Produces( { MediaType.TEXT_HTML })
	public Response logout() {
		log.info("Login attempt");
		SecurityUtils.getSubject().logout();
		return redirect("/");
	}
	
	
	@GET
	@Path("/profile")
	@Produces( { MediaType.TEXT_HTML })
	public String profil() {
		return templateRenderer.create("/templates/gsp/profile.gsp").with("username",  SecurityUtils.getSubject().getPrincipal()).render() ;
	}
}
