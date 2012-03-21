package org.kernely.timesheet.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(amount);
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
		result = prime * result + Float.floatToIntBits(typeRatio);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Expense other = (Expense) obj;
		if (Float.floatToIntBits(amount) != Float.floatToIntBits(other.amount)) {
			return false;
		}
		if (comment == null) {
			if (other.comment != null) {
				return false;
			}
		} else if (!comment.equals(other.comment)) {
			return false;
		}
		if (typeName == null) {
			if (other.typeName != null) {
				return false;
			}
		} else if (!typeName.equals(other.typeName)) {
			return false;
		}
		if (Float.floatToIntBits(typeRatio) != Float.floatToIntBits(other.typeRatio)) {
			return false;
		}
		return true;
	}

	
}