package org.kernely.core.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
