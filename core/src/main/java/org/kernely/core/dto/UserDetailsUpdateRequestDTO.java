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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The update dto of an user detail
 * @author b.grandperret
 *
 */
@XmlRootElement
public class UserDetailsUpdateRequestDTO {

	public int id;
	public String firstname;
	public String lastname;
	public String image;
	public String email;
	public String adress;
	public String zip;
	public String city;
	public String homephone;
	public String mobilephone;
	public String businessphone;
	public String birth;
	public String nationality;
	public String ssn;
	public Integer civility;

	/**
	 * Constructor
	 * @param fName
	 * @param lName
	 * @param img
	 * @param mail
	 * @param adress
	 * @param zip
	 * @param city
	 * @param homephone
	 * @param mobilephone
	 * @param businessphone
	 * @param birth
	 * @param nationality
	 * @param ssn
	 * @param id
	 * @param civility
	 */
	public UserDetailsUpdateRequestDTO(String fName, String lName, String img, String mail, String adress, String zip, String city, String homephone,
			String mobilephone, String businessphone, String birth, String nationality, String ssn, int id, Integer civility) {
		this.firstname = fName;
		this.lastname = lName;
		this.image = img;
		this.email = mail;
		this.adress = adress;
		this.zip = zip;
		this.city = city;
		this.homephone = homephone;
		this.mobilephone = mobilephone;
		this.businessphone = businessphone;
		this.birth = birth;
		this.nationality = nationality;
		this.ssn = ssn;
		this.civility = civility;
		this.id = id;
	}

	/**
	 * Default constructor
	 */
	public UserDetailsUpdateRequestDTO() {

	}

}
