package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDetailsDTO {
	public UserDetailsDTO(){
		
	}
	
	public UserDetailsDTO(String fName, String lName, String img, String mail) {
		firstname = fName;
		lastname = lName;
		image = img;
		email = mail;
	}

	public String firstname;
	public String lastname;
	public String image;
	public String email;
	
}
