package org.kernely.holiday.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The dto corresponding to a cell of holiday Type available in the holiday request page
 *
 */
@XmlRootElement
public class CalendarBalanceDetailDTO {
	/**
	 * The color of the day selected
	 */
	public String color;
	
	/**
	 * name of holiday type
	 */
	public String nameOfType;
	
	/**
	 * Number of day of holiday still available
	 */
	public float nbAvailable;
	
	/**
	 * Id of type of holiday
	 */
	public int idOfType;
	
	/**
	 * Default constructor
	 */
	public CalendarBalanceDetailDTO(){}
	
	/**
	 * Constructor
	 * @param name Name of the balance
	 * @param available Quantity available. If set to -1, the balance is unlimited
	 * @param color Color associated to this balance
	 * @param idType HolidayType associated to this balance
	 */
	public CalendarBalanceDetailDTO(String name, float available, String color, int idType){
		this.color = color;
		this.nameOfType = name;
		this.nbAvailable = available;
		this.idOfType = idType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idOfType;
		result = prime * result + ((nameOfType == null) ? 0 : nameOfType.hashCode());
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
		CalendarBalanceDetailDTO other = (CalendarBalanceDetailDTO) obj;
		if (idOfType != other.idOfType) {
			return false;
		}
		if (nameOfType == null) {
			if (other.nameOfType != null) {
				return false;
			}
		} else if (!nameOfType.equals(other.nameOfType)) {
			return false;
		}
		return true;
	}
	
	
}
