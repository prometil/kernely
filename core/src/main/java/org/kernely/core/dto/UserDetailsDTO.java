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

@XmlRootElement
public class UserDetailsDTO {

	public int id;
	public String firstname = "";
	public String lastname = "";
	public String image = "default_user.png";
	public String email = "";
	public String adress = "";
	public String zip = "";
	public String city = "";
	public String homephone = "";
	public String mobilephone = "";
	public String businessphone = "";
	public String birth = "";
	public String nationality = "";
	public String ssn = "";
	public Integer civility = 0;
	public UserDTO user;

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
		this.id = details.getIdUserDetail();
		this.user = new UserDTO(details.getUser());
	}

}
