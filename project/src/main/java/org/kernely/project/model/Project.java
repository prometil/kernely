package org.kernely.project.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.model.User;

/**
 * project model
 * 
 */
@Entity
@Table(name = "kernely_project")
public class Project extends AbstractModel {

	private String name;
	
	private String description;
	
	private String status;

	private String icon;

	/**
	 * Users in the project
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "kernely_user_project", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "organization_id")
	private Organization organization;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inter_organization_id")
	private Organization interOrganization;

	/**
	 * The right for an user to be a contributor on the project
	 */
	public static final String RIGHT_CONTRIBUTOR = "contributor";

	/**
	 * The right for an user to be a project manager on the project
	 */
	public static final String RIGHT_PROJECTMANAGER = "project_manager";

	/**
	 * The right for an user to be a client on the project
	 */
	public static final String RIGHT_CLIENT = "client";

	/**
	 * The resource for project to give rights on the project.
	 */
	public static final String PROJECT_RESOURCE = "projects";

	/**
	 * initialize a project with default value
	 */
	public Project() {
		this.id = 0;
		this.name = "";
		this.icon = "";
		this.description = "";
		this.status ="";
		this.users = new HashSet<User>();
	}

	/**
	 * @return the type
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon
	 *            the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the interOrganization
	 */
	public Organization getInterOrganization() {
		return interOrganization;
	}

	/**
	 * @param interOrganization the interOrganization to set
	 */
	public void setInterOrganization(Organization interOrganization) {
		this.interOrganization = interOrganization;
	}

	
}
