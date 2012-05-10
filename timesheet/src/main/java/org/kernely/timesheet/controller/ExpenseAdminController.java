package org.kernely.timesheet.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.model.Role;
import org.kernely.template.SobaTemplateRenderer;
import org.kernely.timesheet.dto.ExpenseTypeCreationDTO;
import org.kernely.timesheet.dto.ExpenseTypeDTO;
import org.kernely.timesheet.service.ExpenseService;

import com.google.inject.Inject;

/**
 * ExpenseAdminController
 */
@Path("/admin/expense")
public class ExpenseAdminController extends AbstractController {
	@Inject
	private SobaTemplateRenderer templateRenderer;
	
	@Inject
	private ExpenseService expenseService;
	
	/**
	 * Set the template
	 * @return the page admin
	 */
	@GET
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.TEXT_HTML })
	public Response getPluginAdminPanel(){
		return Response.ok(templateRenderer.render("templates/expense_type_admin.html")).build();
	}
	
	/**
	 * Returns all the DTO representing all expense types in the database
	 * @return A list of ExpenseTypeDTO
	 */
	@GET
	@Path("/type/all")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public List<ExpenseTypeDTO> displayAllExpenseTypes(){
		log.debug("Call to GET on all expense types");
		return expenseService.getAllExpenseTypes();
	}
	
	/**
	 * Creates a new expense type based on the given request
	 * @param request The DTO representing the new expense type
	 * @return The result of the operation
	 */
	@POST
	@Path("/type/create")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public String createExpenseType(ExpenseTypeCreationDTO request) {
		try{
			expenseService.createOrUpdateExpenseType(request);
			return "{\"result\":\"Ok\"}";
		}
		catch (IllegalArgumentException iae) {
			return "{\"result\":\""+ iae.getMessage() +"\"}";
		}
	}
	
	/**
	 * Remove the type with the given id
	 * @param id The id of the type to remove
	 * @return The result of the operation
	 */
	@GET
	@Path("/type/delete")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public Response deleteExpenseType(@QueryParam("idType") long id) {
		expenseService.deleteExpenseType(id);
		return Response.ok().build();
	}
	
	/**
	 * Get the expense with the given id
	 * @param id The id of the expense
	 * @return The expense DTO
	 */
	@GET
	@Path("/type/{id}")
	@RequiresRoles(Role.ROLE_ADMINISTRATOR)
	@Produces( { MediaType.APPLICATION_JSON })
	public ExpenseTypeDTO getExpenseType(@PathParam("id") long id) {
		return expenseService.getExpenseTypeById(id);
	}
}