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

@XmlRootElement
public class UserDetailsUpdateRequestDTO {
	
	public UserDetailsUpdateRequestDTO(String fName, String lName, String img, String mail, String adress, String zip
			, String city, String homephone, String mobilephone, String businessphone, String birth, String nationality,
			String ssn, int id, Integer civility) {
		this.firstname = fName;
		this.name = lName;
		this.image = img;
		this.mail = mail;
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
	}
	
	public UserDetailsUpdateRequestDTO(){

	}
	
	public int id;
	public String firstname;
	public String name;
	public String image;
	public String mail;
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
}
