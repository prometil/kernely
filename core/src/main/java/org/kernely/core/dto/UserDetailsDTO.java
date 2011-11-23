package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDetailsDTO {
	public UserDetailsDTO(){
		
	}
	
	public UserDetailsDTO(String fName, String lName, String img, String mail, String adress, String zip
			, String city, String homephone, String mobilephone, String businessphone, String birth, String nationality,
			String ssn, Integer civility,int id, UserDTO u) {
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
		user = u;
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
	public UserDTO user;
}
