package org.kernely.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.PermissionService;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.model.Organization;
import org.kernely.project.model.Project;
import org.kernely.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


/**
 * The service for organization pages
 * 
 */
@Singleton
public class OrganizationService extends AbstractService{
	
	@Inject
	private PermissionService permissionService;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Gets the lists of all organizations contained in the database.
	 * 
	 * @return the list of all organizations contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<OrganizationDTO> getAllOrganizations() {
		Query query = em.get().createQuery("SELECT e FROM Organization e");
		List<Organization> collection = (List<Organization>) query.getResultList();
		List<OrganizationDTO> dtos = new ArrayList<OrganizationDTO>();
		for (Organization organization : collection) {
			dtos.add(new OrganizationDTO(organization));
		}
		return dtos;
	}
	
	/**
	 * Gets the lists of all organizations contained in the database which contains at least one project.
	 * 
	 * @return the list of all organizations contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<OrganizationDTO> getAllOrganizationsWithProjects() {
		Query query = em.get().createQuery("SELECT DISTINCT e FROM Organization e, Project p WHERE p.organization = e");
		List<Organization> collection = (List<Organization>) query.getResultList();
		List<OrganizationDTO> dtos = new ArrayList<OrganizationDTO>();
		for (Organization organization : collection) {
			dtos.add(new OrganizationDTO(organization));
		}
		return dtos;
	}

	/**
	 * Create a new Organization in database
	 * 
	 * @param request
	 *            The request, containing organization name
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public OrganizationDTO createOrganization(OrganizationCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name==null) {
			throw new IllegalArgumentException("Organization name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Organization name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT g FROM Organization g WHERE name=:name");
		verifExist.setParameter("name", request.name);
		List<Organization> list = (List<Organization>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another organization with this name already exists");
		}

		Organization organization = new Organization();
		organization.setName(request.name);
		organization.setAddress(request.address);
		organization.setZip(request.zip);
		organization.setCity(request.city);
		organization.setFax(request.fax);
		organization.setPhone(request.phone);
		em.get().persist(organization);
		
		return new OrganizationDTO(organization);
	}
	
	
	/**
	 * Update an existing  organization in database
	 * 
	 * @param request
	 *            The request, containing  organization name and id of the needed  organization
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateOrganization(OrganizationCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name==null) {
			throw new IllegalArgumentException("Organization name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Organization name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT g FROM Organization g WHERE name=:name AND id !=:id");
		verifExist.setParameter("name", request.name);
		verifExist.setParameter("id", request.id);
		List<Organization> list = (List<Organization>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another  organization with this name already exists");
		}
		
		Set<User> users = null;
		if (!request.users.isEmpty() && (!(request.users.get(0).username == null))) {
			users = new HashSet<User>();
			for (UserDTO u : request.users) {
				users.add(em.get().find(User.class, u.id));
			}
		}
		
		Organization organization = em.get().find(Organization.class, request.id);
		organization.setName(request.name);
		organization.setAddress(request.address);
		organization.setCity(request.city);
		organization.setZip(request.zip);
		organization.setFax(request.fax);
		organization.setPhone(request.phone);
		
		if (users == null) {
			organization.getUsers().clear();
		} else {
			organization.setUsers(users);
		}
	}
	
	/**
	 * Delete an existing Organization in database
	 * 
	 * @param id
	 *            The id of the organization to delete
	 */
	@Transactional
	public void deleteOrganization(long id) {
		Organization organization = em.get().find(Organization.class, id);
		em.get().remove(organization);
	}
	
	/**
	 * Get all users from a organization.
	 * 
	 * @param id
	 *            The id of the organization.
	 * @return the list of users which are in the organization.
	 */
	@Transactional
	public List<UserDTO> getOrganizationUsers(long id) {
		Organization g = em.get().find(Organization.class, id);
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : g.getUsers()) {
			dtos.add(new UserDTO(user.getUsername(), user.isLocked(), user.getId()));
		}
		return dtos;
	}
	
	/**
	 * Get an organisation by her name
	 * @param the name of the organization
	 * @return the organization
	 */
	@Transactional
	public Organization getOrganizationByName(String name){
		Query query = em.get().createQuery("SELECT o FROM Organization o WHERE name=:name");
		query.setParameter("name", name);
		return (Organization) query.getSingleResult();
	}
	
	/**
	 * Gets all the organization where the current user has the Project manager role
	 * @return A list of OrganizationDTO representing the linked organizations
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<OrganizationDTO> getOrganizationForProjectManager(){
		List<Project> allProjects = (List<Project>)em.get().createQuery("SELECT p FROM Project p").getResultList();
		List<OrganizationDTO> userOrganizations = new ArrayList<OrganizationDTO>();
		for (Project project : allProjects) {
			if (permissionService.userHasPermission(this.getAuthenticatedUserModel().getId(), false, Project.RIGHT_PROJECTMANAGER, Project.PROJECT_RESOURCE, project.getId())){
				userOrganizations.add(new OrganizationDTO(project.getOrganization()));
			}
		}
		return userOrganizations;
	}
}
