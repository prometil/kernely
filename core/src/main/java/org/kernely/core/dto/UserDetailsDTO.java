/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.core.model.UserDetails;

@XmlRootElement
public class UserDetailsDTO {
	/**
	 * Default Constructor
	 */
	public UserDetailsDTO(){
		
	}
	
	/**
	 * UserDetailsDTO Constructor
	 * @param fName User's firstname
	 * @param lName User's lastname
	 * @param img User's image
	 * @param mail User's mail address
	 * @param adress User's address
	 * @param zip User's zip
	 * @param city User's city
	 * @param homephone User's homephone
	 * @param mobilephone User's mobile phone
	 * @param businessphone User's business phone
	 * @param birth User's birth date
	 * @param nationality User's nationality
	 * @param ssn User's social security
	 * @param civility User's civility
	 * @param id User's id
	 * @param u User's login/password
	 */
	public UserDetailsDTO(String fName, String lName, String img, String mail, String adress, String zip
			, String city, String homephone, String mobilephone, String businessphone, String birth, String nationality,
			String ssn, Integer civility,int id, UserDTO u) {
		this.firstname = fName;
		this.lastname = lName;
		this.image = img;
		this.email = mail;
		this.adress = adress;
		this.zip=zip;
		this.city=city;
		this.homephone=homephone;
		this.mobilephone=mobilephone;
		this.businessphone=businessphone; 
		this.birth=birth;
		this.nationality=nationality;
		this.ssn=ssn;
		this.civility=civility;
		this.id=id;
		this.user = u;
	}
	
	public UserDetailsDTO(UserDetails details){
		this.firstname = details.getFirstname();
		this.lastname = details.getName();
		this.image = details.getImage();
		this.email = details.getMail();
		this.adress = details.getAdress();
		this.zip= details.getZip();
		this.city=details.getCity();
		this.homephone=details.getHomephone();
		this.mobilephone=details.getMobilephone();
		this.businessphone=details.getBusinessphone();
		if(details.getBirth() != null){
			this.birth=details.getBirth().toString();
		}
		else{
			this.birth = "";
		}
		this.nationality=details.getNationality();
		this.ssn=details.getSsn();
		this.civility=details.getCivility();
		this.id=details.getId_user_detail();
	}

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
	public UserDTO user;
}
