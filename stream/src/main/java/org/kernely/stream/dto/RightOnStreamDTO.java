package org.kernely.stream.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RightOnStreamDTO {
	/**
	 * Default constructor
	 */
	public RightOnStreamDTO() {

	}

	/**
	 * Creates a RightOnStreamDTO
	 * 
	 * @param userid
	 *            Id of the user
	 * @param permission
	 *            Permission granted to the user
	 */
	public RightOnStreamDTO(int userid, String permission) {
		this.userid = userid;
		this.permission = permission;
	}

	public int userid;
	public String permission;
}
