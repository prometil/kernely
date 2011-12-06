package org.kernely.core.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.kernely.core.dto.ManagerCreationRequestDTO;
import org.kernely.core.dto.ManagerDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.Inject;


@Path("/admin/manager")
public class ManagerAdminController extends AbstractController {
	@Inject
	private TemplateRenderer templateRenderer;

	@Inject
	private UserService userService;
	
	/**
	 * Display the manager administration page
	 * @return the manager administration page
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public String displayPage()
	{
		if (userService.currentUserIsAdministrator()){
			return templateRenderer.create("/templates/gsp/administration/manager_admin.gsp").withoutLayout().render() ;
		}
		return templateRenderer.create("/templates/gsp/home.gsp").render();
	}
	
	
	@GET
	@Path("/combobox")
	@Produces({MediaType.APPLICATION_JSON})
	public List<UserDTO> getListUsers(){
		Set<ManagerDTO> manager = userService.getAllManager();
		List<UserDTO> allUser  = userService.getAllUsers();
		List<UserDTO> removeUser = new ArrayList<UserDTO>();
		for (ManagerDTO man : manager){
			for (UserDTO usr : allUser){
				if (man.name.equals(usr.username)){
					removeUser.add(usr);
				}
			}
		}
		allUser.removeAll(removeUser);
		return allUser;
	}
	
	/**
	 * Create a new manager with the given informations
	 * @param manager The DTO containing all informations about the new manager
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/create")
	@Produces({"application/json"})
	public String create(ManagerCreationRequestDTO manager)
	{ 
		if (userService.currentUserIsAdministrator()){
			userService.updateManager(manager.manager, manager.users);
			return "{\"result\":\"ok\"}"; 
		}
		return null;
	}
	
	/**
	 * Update a manager with the given informations
	 * @param manager The DTO containing all informations about the manager
	 * @return A JSON string containing the result of the operation
	 */
	@POST
	@Path("/update")
	@Produces({"application/json"})
	public String update(ManagerCreationRequestDTO manager)
	{ 
		if (userService.currentUserIsAdministrator()){
			userService.updateManager(manager.manager, manager.users); 
			return "{\"result\":\"ok\"}"; 
		}
		return null;
	}
	
	
	
	/**
	 * Get all existing manager in the database
	 * @return A list of all DTO associated to the existing manager in the database
	 */
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<ManagerDTO> displayAllManager(){
		if (userService.currentUserIsAdministrator()){
			Set<ManagerDTO> setManagers = userService.getAllManager();
			List<ManagerDTO> managers = new ArrayList<ManagerDTO>(setManagers);			
			return managers;
		}
		return null;
	}

	@GET
	@Path("/users/{username}")
	@Produces({"application/json"})
	public List<UserDTO> getListManaged(@PathParam("username") String idManager)
	{
		if (userService.currentUserIsAdministrator()){
			return userService.getUsers(idManager);
		}
		return null;
		
	}
	
	/**
	 * Delete the lanager which has the username 'username'
	 * The manager lost all his managed user
	 * @param the username of the manager to delete
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete/{username}")
	@Produces( { MediaType.TEXT_HTML })
	public String lock(@PathParam("username") String username){
		if (userService.currentUserIsAdministrator()){
			userService.deleteManager(username);
			return "Ok";
		}
		return null;
	}
	
}
