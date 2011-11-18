package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDetailsDTO {
	public UserDetailsDTO(){
		
	}
	
	public UserDetailsDTO(int id, String fName, String lName, String img, String mail, UserDTO u) {
		this.id = id;
		firstname = fName;
		lastname = lName;
		image = img;
		email = mail;
		user = u;
	}

	public int id;
	public String firstname;
	public String lastname;
	public String image;
	public String email;
	public UserDTO user;
}
