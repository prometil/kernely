package org.kernely.timesheet.controller;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.kernely.core.controller.AbstractController;
import org.kernely.timesheet.dto.ExpenseCreationDTO;
import org.kernely.timesheet.dto.ExpenseDTO;
import org.kernely.timesheet.dto.ExpenseTypeDTO;
import org.kernely.timesheet.dto.TotalExpenseDTO;
import org.kernely.timesheet.service.ExpenseService;

import com.google.inject.Inject;

/**
 * Controller for the expenses.
 */
@Path("/expense")
public class ExpenseController extends AbstractController  {
	
	@Inject
	private ExpenseService expenseService;

	/**
	 * Creates a new expense type based on the given request
	 * @param request The DTO representing the new expense type
	 * @return The result of the operation
	 */
	@POST
	@Path("/create")
	@Produces( { MediaType.APPLICATION_JSON })
	public ExpenseDTO createExpenseLine(ExpenseCreationDTO request) {
		try{
			return expenseService.createOrUpdateExpenseLine(request);
		}
		catch (IllegalArgumentException iae) {
			log.debug("An error has occured during expense creation !");
			return null;
		}
	}
	
	/**
	 * Remove the type with the given id
	 * @param id The id of the type to remove
	 * @return The result of the operation
	 */
	@GET
	@Path("/delete")
	@Consumes( { MediaType.APPLICATION_JSON })
	@Produces( { MediaType.APPLICATION_JSON })
	public String deleteExpenseType(@QueryParam("idExpense") long id) {
		expenseService.deleteExpenseLine(id);
		return "{\"result\":\"Ok\"}";
	}
	
	/**
	 * Retrieves all expenses done for the day according to the detail with the given Id
	 * @param idDetail The id of the detail containing all expenses needed
	 * @return A list of DTO representing all expenses for this day.
	 */
	@GET
	@Path("/all")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<ExpenseDTO> getAllExpensesForDetail(@QueryParam("idDay") long idDay){
		return expenseService.getAllExpensesForDay(idDay);
	}
	
	/**
	 * Returns all the DTO representing all expense types in the database
	 * @return A list of ExpenseTypeDTO
	 */
	@GET
	@Path("/type/all")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<ExpenseTypeDTO> displayAllExpenseTypes(){
		return expenseService.getAllExpenseTypes();
	}
	
	@GET
	@Path("/totals")
	@Produces( { MediaType.APPLICATION_JSON })
	public List<TotalExpenseDTO> getAllTotalsForTheTimeSheet(@QueryParam("idTimeSheet") long idTimeSheet){
		return expenseService.getTotalLineForWeek(idTimeSheet);
	}
}