package org.kernely.timesheet.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO used for creation of an expense line.
 */
@XmlRootElement
public class ExpenseCreationDTO {
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
	 * Id of of the type associated to this expense
	 */
	public long expenseTypeId;
	
	/**
	 * Name of the type associated to this expense
	 */
	public String typeName;
	
	/**
	 * Ratio of the type associated to this expense
	 */
	public float typeRatio;
	
	/**
	 * Id of the detail of the timesheet
	 */
	public long timesheetDayId;
	
	/**
	 * Default Constructor
	 */
	public ExpenseCreationDTO(){}
	
	/**
	 * Construct an expense DTO
	 * @param id Id of this expense
	 * @param amount Amount of this expense
	 * @param comment Comment of this expense
	 * @param expenseTypeId Type's id of this expense
	 * @param typeName Type's name of this expense
	 * @param typeRatio Type's ratio of this expense
	 */
	public ExpenseCreationDTO(long id, float amount, String comment, long expenseTypeId, String typeName, float typeRatio, long timesheetDayId){
		this.id = id;
		this.amount = amount;
		this.expenseTypeId = expenseTypeId;
		this.comment = comment;
		this.typeName = typeName;
		this.typeRatio = typeRatio;
		this.timesheetDayId = timesheetDayId;
	}
}