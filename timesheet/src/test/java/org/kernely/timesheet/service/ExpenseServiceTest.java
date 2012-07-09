package org.kernely.timesheet.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.timesheet.dto.ExpenseTypeCreationDTO;
import org.kernely.timesheet.dto.ExpenseTypeDTO;

import com.google.inject.Inject;

public class ExpenseServiceTest extends AbstractServiceTest{
	
	private static final String NAME_TYPE = "Expense_type_1";
	private static final float RATIO_TYPE = 1.25F;

	private static final String NAME_TYPE_MODIFIED = "Expense_type_modified_1";
	private static final float RATIO_TYPE_MODIFIED = 2.25F;
	
	@Inject
	private ExpenseService expenseService;
	
	@Test(expected=IllegalArgumentException.class)
	public void  createExpenseWithNullRequest(){
		expenseService.createOrUpdateExpenseType(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void  createExpenseWithEmptyName(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = "";
		expenseService.createOrUpdateExpenseType(request);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void  createExpenseWithSpacedName(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = "             ";
		expenseService.createOrUpdateExpenseType(request);
	}
	
	@Test
	public void  createNewDirectExpenseType(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = true;
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE, type.name);
		assertTrue(type.direct);
		assertEquals(1.0F, type.ratio, 0);
	}
	
	@Test
	public void  createNewDirectExpenseTypeWithFakeRatio(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = true;
		request.ratio = RATIO_TYPE;
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE, type.name);
		assertTrue(type.direct);
		// Ratio isn't considered due to direct type.
		assertEquals(1.0F, type.ratio, 0);
	}
	
	@Test
	public void  createNewInDirectExpenseType(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = false;
		request.ratio = RATIO_TYPE;
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE, type.name);
		assertFalse(type.direct);
		assertEquals(RATIO_TYPE, type.ratio, 0);
	}
	
	@Test
	public void updateIndirectExpenseType(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = false;
		request.ratio = RATIO_TYPE;
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE, type.name);
		assertFalse(type.direct);
		assertEquals(RATIO_TYPE, type.ratio, 0);
		
		request.id = type.id;
		request.ratio = RATIO_TYPE_MODIFIED;
		request.name = NAME_TYPE_MODIFIED;
		type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE_MODIFIED , type.name);
		assertFalse(type.direct);
		assertEquals(RATIO_TYPE_MODIFIED, type.ratio, 0);
	}
	
	@Test
	public void updateDirectExpenseType(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = true;
		request.ratio = RATIO_TYPE;
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE, type.name);
		assertTrue(type.direct);
		assertEquals(1.0F, type.ratio, 0);
		
		request.id = type.id;
		request.ratio = RATIO_TYPE_MODIFIED;
		request.name = NAME_TYPE_MODIFIED;
		type = expenseService.createOrUpdateExpenseType(request);
		assertEquals(NAME_TYPE_MODIFIED, type.name);
		assertTrue(type.direct);
		assertEquals(1.0F, type.ratio, 0);
	}
	
	@Test
	public void getAllExpenseTypes(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = true;
		
		expenseService.createOrUpdateExpenseType(request);
		
		request.ratio = RATIO_TYPE_MODIFIED;
		request.direct = false;
		request.name = NAME_TYPE_MODIFIED;
		
		expenseService.createOrUpdateExpenseType(request);
		
		List<ExpenseTypeDTO> types = expenseService.getAllExpenseTypes();
		assertEquals(2, types.size());
	}
	
	@Test
	public void getAllExpenseTypesWhenNoTypes(){
		List<ExpenseTypeDTO> types = expenseService.getAllExpenseTypes();
		assertEquals(0, types.size());
	}
	
	@Test
	public void getExpenseTypeById(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = true;
		
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		ExpenseTypeDTO typeByMethod = expenseService.getExpenseTypeById(type.id);
		assertEquals(NAME_TYPE, typeByMethod.name);
		assertTrue(typeByMethod.direct);
		assertEquals(1.0F, typeByMethod.ratio, 0);
	}
	
	@Test
	public void deleteExpenseType(){
		ExpenseTypeCreationDTO request = new ExpenseTypeCreationDTO();
		request.name = NAME_TYPE;
		request.direct = true;
		
		ExpenseTypeDTO type = expenseService.createOrUpdateExpenseType(request);
		List<ExpenseTypeDTO> types = expenseService.getAllExpenseTypes();
		assertEquals(1, types.size());
		
		expenseService.deleteExpenseType(type.id);
		types = expenseService.getAllExpenseTypes();
		assertEquals(0, types.size());
	}
}
