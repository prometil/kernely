/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.model.UserDetails;

/**
 * The details of the user
 * @author b.grandperret
 *
 */
@XmlRootElement
public class UserDetailsDTO {

	/**
	 * The id of the user detail
	 */
	public long id;
	
	/**
	 * The firstname of the user
	 */
	public String firstname = "";
	
	/**
	 * The lastname of the user
	 */
	public String lastname = "";
	
	/**
	 * The image of  the user
	 */
	public String image = "default_user.png";
	
	/**
	 * The  email of the user
	 */
	public String email = "";
	
	/**
	 * The adress of the hser
	 */
	public String adress = "";
	
	/**
	 * The zip code of the user
	 */
	public String zip = "";
	
	/**
	 * The city of the user 
	 */
	public String city = "";
	
	/**
	 * the homephone of the user
	 */
	public String homephone = "";
	
	/**
	 * The mobilephone of the user
	 */
	public String mobilephone = "";
	
	/**
	 * The businessphone of the user
	 */
	public String businessphone = "";
	
	/**
	 * The brth of the user
	 */
	public String birth = "";
	
	/**
	 * The nationality of the user
	 */
	public String nationality = "";
	
	/**
	 * The social security number of the user
	 */
	public String ssn = "";
	
	/**
	 * the civility of the user
	 */
	public Integer civility = 0;
	
	/**
	 * The user link to the user detail 
	 */
	public UserDTO user;
	
	/**
	 * The hire date of this user
	 */
	public Date hire;
	
	/**
	 * The hire date in String format of this user, used fir display
	 */
	public String hireString = "";

	/**
	 * Default Constructor
	 */
	public UserDetailsDTO() {

	}

	/**
	 * UserDetailsDTO Constructor
	 * 
	 * @param fName
	 *            User's firstname
	 * @param lName
	 *            User's lastname
	 * @param id
	 *            User's id
	 * @param u
	 *            User's login/password
	 */
	public UserDetailsDTO(String fName, String lName, int id, UserDTO u) {
		this.firstname = fName;
		this.lastname = lName;
		this.id = id;
		this.user = u;
	}

	/**
	 * Constructor using a model
	 * @param details
	 */
	public UserDetailsDTO(UserDetails details) {
		this.firstname = details.getFirstname();
		this.lastname = details.getName();
		this.image = details.getImage();
		this.email = details.getMail();
		this.adress = details.getAdress();
		this.zip = details.getZip();
		this.city = details.getCity();
		this.homephone = details.getHomephone();
		this.mobilephone = details.getMobilephone();
		this.businessphone = details.getBusinessphone();
		if (details.getBirth() != null) {
			String newDateString;
			Date date = details.getBirth();
			String newPattern = "dd/MM/yyyy";
			newDateString = (new SimpleDateFormat(newPattern)).format(date);
			this.birth = newDateString;
		} else {
			this.birth = "";
		}
		this.nationality = details.getNationality();
		this.ssn = details.getSsn();
		this.civility = details.getCivility();
		this.hire = details.getHire();
		if (details.getHire() != null) {
			String newDateString;
			Date date = details.getHire();
			String newPattern = "dd/MM/yyyy";
			newDateString = (new SimpleDateFormat(newPattern)).format(date);
			this.hireString = newDateString;
		} else {
			this.hireString = "";
		}
		this.id = details.getId();
		this.user = new UserDTO(details.getUser());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
		result = (int) (prime * result + (int) (id ^ (id >>> 32)));
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDetailsDTO other = (UserDetailsDTO) obj;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (id != other.id)
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		return true;
	}
	
	

}
