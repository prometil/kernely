package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AdminPageDTO {
	public AdminPageDTO(){
		
	}
	
	public AdminPageDTO(String name, String path) {
		this.name = name ;
		this.path = path;
	}

	public String name;
	public String path;

}
