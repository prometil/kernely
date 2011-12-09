package org.kernely.stream.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RightOnStreamDTO {

	public int userid;
	public String permission;

	public RightOnStreamDTO() {

	}

	public RightOnStreamDTO(int userid, String permission) {
		this.userid = userid;
		this.permission = permission;
	}

}
