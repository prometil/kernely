package org.kernely.timesheet.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.kernely.core.service.AbstractService;
import org.kernely.timesheet.dto.ExpenseTypeCreationDTO;
import org.kernely.timesheet.dto.ExpenseTypeDTO;
import org.kernely.timesheet.model.ExpenseType;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * Expense service for the timesheet
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
}
