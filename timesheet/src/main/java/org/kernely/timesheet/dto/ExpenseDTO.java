package org.kernely.timesheet.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.timesheet.model.Expense;

/**
 * DTO used for the representation of an expense line.
 */
@XmlRootElement
public class ExpenseDTO {
	/**
	 * Id of this expense
	 */
	public long id;
	
	/**
	 * Amount of this expense
	 */
	public float amount;
	
	/**
	 * Comment of this expense
	 */
	public String comment;
	
	/**
	 * Name of the type associated to this expense
	 */
	public String typeName;
	
	/**
	 * Ratio of the type associated to this expense
	 */
	public float typeRatio;
	
	/**
	 * The id of the time sheet
	 */
	public long associatedTimeSheetId;
	
	/**
	 * Default constructor
	 */
	public ExpenseDTO(){}
	
	/**
	 * Constructs the DTO based on a model of Expense
	 * @param expense The model of the expense to represent
	 */
	public ExpenseDTO(Expense expense){
		this.id = expense.getId();
		this.amount = expense.getAmount();
		this.comment = expense.getComment();
		this.typeName = expense.getTypeName();
		this.typeRatio = expense.getTypeRatio();
		this.associatedTimeSheetId = expense.getTimeSheetDay().getTimeSheet().getId();
	}
}