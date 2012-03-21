package org.kernely.timesheet.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.kernely.core.service.AbstractService;
import org.kernely.timesheet.dto.ExpenseCreationDTO;
import org.kernely.timesheet.dto.ExpenseDTO;
import org.kernely.timesheet.dto.ExpenseTypeCreationDTO;
import org.kernely.timesheet.dto.ExpenseTypeDTO;
import org.kernely.timesheet.dto.TotalExpenseDTO;
import org.kernely.timesheet.model.Expense;
import org.kernely.timesheet.model.ExpenseType;
import org.kernely.timesheet.model.TimeSheet;
import org.kernely.timesheet.model.TimeSheetDay;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Expense service for the time sheet
 */
@Singleton
public class ExpenseService extends AbstractService {

	/**
	 * Creates a new expense type based on the request
	 * @param request The DTO representing the expense type to create
	 * @return A DTO representing the new expense type created
	 */
	@Transactional
	public ExpenseTypeDTO createOrUpdateExpenseType(ExpenseTypeCreationDTO request){
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name == null) {
			throw new IllegalArgumentException("Name of the type cannot be null.");
		}
		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Name of the type cannot be empty.");
		}
		
		ExpenseType type;
		
		// If the id is 0, it's a new expense type, else it's an update
		if(request.id == 0){
			type = new ExpenseType();
		}
		else{
			type = em.get().find(ExpenseType.class, request.id);
		}
		
		type.setName(request.name);
		type.setDirect(request.direct);
		// A direct type will not modify value of future amount
		if(request.direct){
			type.setRatio(1.0F);
		}
		else{
			type.setRatio(request.ratio);
		}
		
		if(request.id == 0){
			em.get().persist(type);
		}
		else{
			em.get().merge(type);
		}
		
		return new ExpenseTypeDTO(type);
	}

	/**
	 * Retrieves all the expense types stored in the database
	 * @return A list of DTOs containing the expense types stored in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ExpenseTypeDTO> getAllExpenseTypes(){
		Query request = em.get().createQuery("SELECT e FROM ExpenseType e");
		List<ExpenseType> types = (List<ExpenseType>)request.getResultList();
		List<ExpenseTypeDTO> typesDTO = new ArrayList<ExpenseTypeDTO>();
		for(ExpenseType type : types){
			typesDTO.add(new ExpenseTypeDTO(type));
		}
		return typesDTO;
	}
	
	/**
	 * Retrieves the Expense type with the given id
	 * @param id The id of the expense type needed
	 * @return A DTO representing the expense type needed
	 */
	@Transactional
	public ExpenseTypeDTO getExpenseTypeById(long id){
		ExpenseType type = em.get().find(ExpenseType.class, id);
		return new ExpenseTypeDTO(type);
	}
	
	/**
	 * Remove the type with the given id
	 * @param id The id of the type to remove
	 */
	@Transactional
	public void deleteExpenseType(long id){
		ExpenseType type = em.get().find(ExpenseType.class, id);
		em.get().remove(type);
	}
	
	/**
	 * Creates a new expense based on the request
	 * @param request The DTO containing all informations of the new expense
	 * @return A DTO representing the new expense created
	 */
	@Transactional
	public ExpenseDTO createOrUpdateExpenseLine(ExpenseCreationDTO request){
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.amount == 0.0F) {
			throw new IllegalArgumentException("Amount of the expense cannot be 0.");
		}
		
		Expense expense;
		
		// If the id is 0, it's a new expense type, else it's an update
		if(request.id == 0){
			expense = new Expense();
		}
		else{
			expense = em.get().find(Expense.class, request.id);
		}
		
		expense.setAmount(request.amount);
		expense.setComment(request.comment);
		expense.setTimeSheetDay(em.get().find(TimeSheetDay.class, request.timesheetDayId));
		
		if(request.id == 0){
			// In the creation mode, we have the id of the type in the request.
			ExpenseType type = em.get().find(ExpenseType.class, request.expenseTypeId);
			expense.setTypeName(type.getName());
			expense.setTypeRatio(type.getRatio());
			em.get().persist(expense);
		}
		else{
			// In the update mode, we can have either id of the type or name and ratio.
			if(request.expenseTypeId != 0){
				ExpenseType type = em.get().find(ExpenseType.class, request.expenseTypeId);
				expense.setTypeName(type.getName());
				expense.setTypeRatio(type.getRatio());
			}
			else{
				expense.setTypeName(request.typeName);
				expense.setTypeRatio(request.typeRatio);
			}
			em.get().merge(expense);
		}
		
		return new ExpenseDTO(expense);
	}
	
	/**
	 * Remove an expense
	 * @param id The Id of the expense to remove
	 */
	@Transactional
	public void deleteExpenseLine(long id){
		Expense expense = em.get().find(Expense.class, id);
		em.get().remove(expense);
	}
	
	/**
	 * Returns all the expenses associated to the time sheet detail with the given Id
	 * @param idDetail Id of the detail concerned
	 * @return A list of ExpenseDTO representing all the expenses contained in the detail with id idDetail
	 */
	@Transactional
	public List<ExpenseDTO> getAllExpensesForDay(long idDay){
		TimeSheetDay day = em.get().find(TimeSheetDay.class, idDay);
		Set<Expense> expenses = day.getExpenses();
		List<ExpenseDTO> expensesDTO = new ArrayList<ExpenseDTO>();
		for(Expense e : expenses){
			expensesDTO.add(new ExpenseDTO(e));
		}
		return expensesDTO;
	}
	
	/**
	 * Returns a list where for each day, we have the sum of expenses registered
	 * @param timeSheetId The id of the time sheet considered
	 * @return A list of Float representing the sum of expenses done for each day.
	 */
	@Transactional
	public List<TotalExpenseDTO> getTotalLineForWeek(long timeSheetId){
		TimeSheet timeSheet = em.get().find(TimeSheet.class, timeSheetId);
		List<TotalExpenseDTO> totals = new ArrayList<TotalExpenseDTO>(7);
		float count;
		for(TimeSheetDay day : timeSheet.getDays()){
			count = 0.0F;
			for(Expense expense : day.getExpenses()){
				count += (expense.getAmount() * expense.getTypeRatio()); 
			}
			totals.add(new TotalExpenseDTO(count, day.getDay()));
		}
		Collections.sort(totals);
		return totals;
	}
}