package org.kernely.core.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;

@Path("/admin/users")
public class UserAdminController extends AbstractController{
	
	@Inject
	private TemplateRenderer templateRenderer;
	
	@Inject
	private UserService userService;
	
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String displayPage()
	{
		return templateRenderer.create("/templates/gsp/administration/user_admin.gsp").render() ;
	}
	
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<UserDetailsDTO> displayAllUsers()
	{
		log.debug("Call to GET on all users");
		List<UserDetailsDTO> users = userService.getAllUserDetails();
		return users;
	}

	/**
	 * Create a new user with a random username and as password : "password".
	 * @return "Ok", not useful.
	 */
	@POST
	@Path("/create")
	public String create(UserCreationRequestDTO user)
	{
		log.debug("Create a user");
		
		if(user.id == 0){
			userService.createUser(user);
		}
		else{
			userService.updateUser(user);
		}
		return "Ok";
	}
	
}
