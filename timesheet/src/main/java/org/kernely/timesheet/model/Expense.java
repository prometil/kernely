package org.kernely.timesheet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kernely.persistence.AbstractModel;

@Entity
@Table(name="kernely_expense")
public class Expense extends AbstractModel {
	private float amount;
	private String comment;
	
	@Column(name="type_name")
	private String typeName;
	
	@Column(name="type_ratio")
	private float typeRatio;
	
	@ManyToOne
	@JoinColumn(name="timesheet_day_id")
	private TimeSheetDay timeSheetDay;

	/**
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the typeRatio
	 */
	public float getTypeRatio() {
		return typeRatio;
	}

	/**
	 * @param typeRatio the typeRatio to set
	 */
	public void setTypeRatio(float typeRatio) {
		this.typeRatio = typeRatio;
	}

	/**
	 * @return the timeSheetDetail
	 */
	public TimeSheetDay getTimeSheetDay() {
		return timeSheetDay;
	}

	/**
	 * @param timeSheetDetail the timeSheetDetail to set
	 */
	public void setTimeSheetDay(TimeSheetDay timeSheetDay) {
		this.timeSheetDay = timeSheetDay;
	}
	
}