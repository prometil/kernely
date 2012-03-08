package org.kernely.timesheet.model;

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
public class TimeSheetDetail extends AbstractModel {

	private String name;

	private String icon;

	/**
	 * Users in the project
	 */
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.LAZY)
	@JoinTable(name = "kernely_user_project", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> users;

	@OneToOne
	@JoinColumn(name = "organization_id")
	private TimeSheet organization;

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
	public TimeSheetDetail() {
		this.id = 0;
		this.name = "";
		this.icon = "";
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
	public TimeSheet getOrganization() {
		return organization;
	}

	/**
	 * @param organization
	 *            the organization to set
	 */
	public void setOrganization(TimeSheet organization) {
		this.organization = organization;
	}

}
