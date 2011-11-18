package org.kernely.core.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PluginDTO {
	public PluginDTO(){
		
	}
	
	public PluginDTO(String name, String path, String img, String admin, String adminpath) {
		this.name = name ;
		this.path = path;
		this.img = img;
		this.admin = admin;
		this.adminpath = adminpath;
	}

	public String adminpath;
	public String admin;
	public String name;
	public String path;
	public String img;
	public String email;
	
}
