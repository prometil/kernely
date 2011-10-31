package org.kernely.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractEntity;



//l'entité (LA table) dans la base de donnée en version java
@Entity
@Table(name = "user")
public class UserModel extends AbstractEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	private String username;
	private String password;
	
	@Column(name = "Username")
	public String getUsername() {
		return username;
	}
	
	@Column(name = "password")
	public String getPassword() {
		return password;
	}
	
	public void setUsername(String newUsername) {
		this.username = newUsername;
	}
	public void setPassword(String newPassword) {
		this.password = newPassword;
	}
}
