package org.kernely.timesheet.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;

@Entity
@Table(name="kernely_expense_type")
public class ExpenseType extends AbstractModel {

	private String name;
	private boolean direct;
	private float ratio;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the direct
	 */
	public boolean isDirect() {
		return direct;
	}
	/**
	 * @param direct the direct to set
	 */
	public void setDirect(boolean direct) {
		this.direct = direct;
	}
	/**
	 * @return the ratio
	 */
	public float getRatio() {
		return ratio;
	}
	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (direct ? 1231 : 1237);
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + Float.floatToIntBits(ratio);
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
		ExpenseType other = (ExpenseType) obj;
		if (direct != other.direct) {
			return false;
		}
		if (id != other.id) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (Float.floatToIntBits(ratio) != Float.floatToIntBits(other.ratio)) {
			return false;
		}
		return true;
	}
	
	
}
