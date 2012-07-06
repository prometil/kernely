package org.kernely.holiday.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kernely.core.model.User;
import org.kernely.persistence.AbstractModel;

@Entity
@Table(name = "kernely_holiday_donation")
public class HolidayDonation  extends AbstractModel {
	private float amount;
	private String comment;
	private Date date;
	
	@ManyToOne
	@JoinColumn(name = "holiday_type_instance_id")
	private HolidayTypeInstance holidayTypeInstance;
	
	@ManyToOne
	@JoinColumn(name = "manager_id")
	private User manager;
	
	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private User receiver;

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
	 * @return the holidayTypeInstance
	 */
	public HolidayTypeInstance getHolidayTypeInstance() {
		return holidayTypeInstance;
	}

	/**
	 * @param holidayTypeInstance the holidayTypeInstance to set
	 */
	public void setHolidayTypeInstance(HolidayTypeInstance holidayTypeInstance) {
		this.holidayTypeInstance = holidayTypeInstance;
	}

	/**
	 * @return the manager
	 */
	public User getManager() {
		return manager;
	}

	/**
	 * @param manager the manager to set
	 */
	public void setManager(User manager) {
		this.manager = manager;
	}

	/**
	 * @return the receiver
	 */
	public User getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(User receiver) {
		this.receiver = receiver;
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
		result = prime * result + ((holidayTypeInstance == null) ? 0 : holidayTypeInstance.hashCode());
		result = prime * result + ((manager == null) ? 0 : manager.hashCode());
		result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
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
		HolidayDonation other = (HolidayDonation) obj;
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
		if (holidayTypeInstance == null) {
			if (other.holidayTypeInstance != null) {
				return false;
			}
		} else if (!holidayTypeInstance.equals(other.holidayTypeInstance)) {
			return false;
		}
		if (manager == null) {
			if (other.manager != null) {
				return false;
			}
		} else if (!manager.equals(other.manager)) {
			return false;
		}
		if (receiver == null) {
			if (other.receiver != null) {
				return false;
			}
		} else if (!receiver.equals(other.receiver)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
}
