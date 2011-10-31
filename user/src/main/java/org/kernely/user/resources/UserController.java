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

@Path("/user")
public class UserController  extends AbstractController{
	
	

	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private UserService userService;
	
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String getText()
	{
		log.debug("Call to GET on all users");
		List<UserDTO> users = userService.getAllUsers();
		return templateRenderer.create("/templates/gsp/users.gsp").with("users", users).render() ;
	}
	
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
	
	@GET
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public String login() {
		return templateRenderer.create("/templates/gsp/login.gsp").withoutLayout().render();
	}
	
	@POST
	@Path("/login")
	@Produces( { MediaType.TEXT_HTML })
	public String postLogin() {
		log.info("Login attempt : is authenticated {}", SecurityUtils.getSubject().isAuthenticated());
		
		return templateRenderer.create("/templates/gsp/login.gsp").withoutLayout().render();
	}
	
	@GET
	@Path("/logout")
	@Produces( { MediaType.TEXT_HTML })
	public Response logout() {
		log.info("Login attempt");
		SecurityUtils.getSubject().logout();
		return redirect("/");
	}
	
}
