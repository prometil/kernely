package org.kernely.holiday.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * Model for an instance of an holiday type.
 */
@Entity
@Table(name = "kernely_holiday_type_instance")
public class HolidayTypeInstance extends AbstractModel {
	private String name;

	private String color;

	private boolean anticipated;

	private int quantity;

	@Column(name = "period_unit")
	private int periodUnit;

	@OneToMany(mappedBy = "holidayTypeInstance")
	private Set<HolidayBalance> balances;

	@ManyToMany
	@JoinTable(name = "kernely_holiday_type_instance_user", joinColumns = @JoinColumn(name = "type_instance_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users;

	/**
	 * @return the type
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the balances associated to this type
	 */
	public Set<HolidayBalance> getBalances() {
		return balances;
	}

	/**
	 * @param balances
	 *            the balances (associated to this type) to set
	 */
	public void setBalances(Set<HolidayBalance> balances) {
		this.balances = balances;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	/**
	 * @return the anticipated
	 */
	public boolean isAnticipated() {
		return anticipated;
	}

	/**
	 * @param anticipated
	 *            the anticipated to set
	 */
	public void setAnticipated(boolean anticipated) {
		this.anticipated = anticipated;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (anticipated ? 1231 : 1237);
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = (int) (prime * result + id);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		HolidayTypeInstance other = (HolidayTypeInstance) obj;
		if (anticipated != other.anticipated) {
			return false;
		}
		if (color == null) {
			if (other.color != null) {
				return false;
			}
		} else if (!color.equals(other.color)) {
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
		return true;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the periodUnit
	 */
	public int getPeriodUnit() {
		return periodUnit;
	}

	/**
	 * @param periodUnit
	 *            the periodUnit to set
	 */
	public void setPeriodUnit(int periodUnit) {
		this.periodUnit = periodUnit;
	}
}
