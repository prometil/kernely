package org.kernely.timesheet.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.service.user.UserService;
import org.kernely.core.template.TemplateRenderer;
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
	private TemplateRenderer templateRenderer;
	
	@Inject
	private ExpenseService expenseService;
	
	@Inject
	private UserService userService;
	
	/**
	 * Set the template
	 * @return the page admin
	 */
	@GET
	@Produces( { MediaType.TEXT_HTML })
	public Response getPluginAdminPanel(){
		Response page;
		if (userService.currentUserIsAdministrator()){
			page = ok(templateRenderer.create("/templates/gsp/expense_type_admin.gsp").addCss("/css/admin.css").addCss("/css/expense_type_admin.css").withLayout(TemplateRenderer.ADMIN_LAYOUT));
		} else{
			page = ok(templateRenderer.create("/templates/gsp/home.gsp"));
		}
		return page;
	}
	
	/**
	 * Returns all the DTO representing all expense types in the database
	 * @return A list of ExpenseTypeDTO
	 */
	@GET
	@Path("/type/all")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<ExpenseTypeDTO> displayAllExpenseTypes(){
		if (userService.currentUserIsAdministrator()){
			log.debug("Call to GET on all expense types");
			return expenseService.getAllExpenseTypes();
		}
		return null;
	}
	
	/**
	 * Creates a new expense type based on the given request
	 * @param request The DTO representing the new expense type
	 * @return The result of the operation
	 */
	@POST
	@Path("/type/create")
	@Produces( { MediaType.APPLICATION_JSON })
	public String createExpenseType(ExpenseTypeCreationDTO request) {
		if (userService.currentUserIsAdministrator()){
			try{
				expenseService.createOrUpdateExpenseType(request);
				return "{\"result\":\"Ok\"}";
			}
			catch (IllegalArgumentException iae) {
				return "{\"result\":\""+ iae.getMessage() +"\"}";
			}
		}
		return "{\"result\":\"Error\"}";
	}
	
	/**
	 * Remove the type with the given id
	 * @param id The id of the type to remove
	 * @return The result of the operation
	 */
	@GET
	@Path("/type/delete")
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public String deleteExpenseType(@QueryParam("idType") long id) {
		if (userService.currentUserIsAdministrator()){
			expenseService.deleteExpenseType(id);
			return "{\"result\":\"Ok\"}";
		}
		return "{\"result\":\"Error\"}";
	}
}