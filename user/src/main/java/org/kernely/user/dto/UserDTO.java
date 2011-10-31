/**
 * 
 */
package org.kernely.user.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author yak
 *
 */
@XmlRootElement
public class UserDTO {
	public UserDTO(String pUsername) {
		username = pUsername;
	}

	public String username;
}
