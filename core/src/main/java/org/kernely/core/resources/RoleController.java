package org.kernely.core.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.kernely.core.dto.RoleDTO;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;

import com.google.inject.Inject;

@Path("/roles")
public class RoleController extends AbstractController {

	
	@Inject
	private RoleService roleService;
	
	@Inject
	private UserService userService;
	
	@GET
	@Path("/all")
	@Produces({"application/json"})
	public List<RoleDTO> getAllRoles()
	{
		if (userService.currentUserIsAdministrator()){
			List<RoleDTO> roles = roleService.getAllRoles();
			return roles;
		}
		return null;
	}
	
}
