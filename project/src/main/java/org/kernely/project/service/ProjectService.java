package org.kernely.project.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for project pages
 * 
 */
@Singleton
public class ProjectService extends AbstractService {
	@Inject
	UserService userService;

	@Inject
	OrganizationService organizationService;

	private static final String ICON = "default.png";
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Gets the lists of all projects contained in the database.
	 * 
	 * @return the list of all projects contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ProjectDTO> getAllProjects() {
		Query query = em.get().createQuery("SELECT e FROM Project e");
		List<Project> collection = (List<Project>) query.getResultList();
		List<ProjectDTO> dtos = new ArrayList<ProjectDTO>();
		for (Project project : collection) {
			List<UserDTO> users = new ArrayList<UserDTO>();
			for (User u : project.getUsers()) {
				users.add(new UserDTO(u.getUsername(), u.isLocked(), u.getId()));
			}
			dtos.add(new ProjectDTO(project.getName(), project.getId(), project.getIcon(), users, new OrganizationDTO(project.getOrganization())));
		}
		return dtos;
	}

	/**
	 * Get the project with the specific name
	 * 
	 * @param name
	 *            the name of the project
	 * @return the project DTO
	 */
	public ProjectDTO getProject(String name) {
		Query query = em.get().createQuery("Select e FROM Project e WHERE name=:name");
		query.setParameter("name", name);
		Project proj = (Project) query.getSingleResult();
		ProjectDTO dto = new ProjectDTO(proj.getName(), proj.getId(), proj.getIcon(), new ArrayList(proj.getUsers()), new OrganizationDTO(proj.getOrganization()));
		return dto;
	}

	/**
	 * Create a new Project in database
	 * 
	 * @param request
	 *            The request, containing project name
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void createProject(ProjectCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name == null) {
			throw new IllegalArgumentException("Project name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Project name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT g FROM Project g WHERE name=:name");
		verifExist.setParameter("name", request.name);
		List<Project> list = (List<Project>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another project with this name already exists");
		}

		Project project = new Project();
		project.setName(request.name.trim());
		project.setIcon(ICON);
		project.setOrganization(organizationService.getOrganizationByName(request.organization));
		em.get().persist(project);
	}

	/**
	 * Update an existing project in database
	 * 
	 * @param request
	 *            The request, containing project name and id of the needed
	 *            project
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateProject(ProjectCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name == null) {
			throw new IllegalArgumentException("Project name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Project name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT g FROM Project g WHERE name=:name AND id !=:id");
		verifExist.setParameter("name", request.name);
		verifExist.setParameter("id", request.id);
		List<Project> list = (List<Project>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another project with this name already exists");
		}
		Set<User> users = null;
		if (!request.users.isEmpty() && (!(request.users.get(0).username == null))) {
			users = new HashSet<User>();
			for (UserDTO u : request.users) {
				users.add(em.get().find(User.class, u.id));
			}
		}
		Project project = em.get().find(Project.class, request.id);
		project.setName(request.name);
		project.setIcon(request.icon);
		project.setOrganization(organizationService.getOrganizationByName(request.organization));
		if (users == null) {
			project.getUsers().clear();
		} else {
			project.setUsers(users);
		}
	}

	/**
	 * Update the project icon
	 * 
	 * @param projectName
	 * @param projectIcon
	 */
	@Transactional
	public void updateProjectIcon(String projectName, String projectIcon) {
		ProjectDTO proj = this.getProject(projectName);
		Project project = em.get().find(Project.class, proj.id);
		project.setIcon(projectIcon);
	}

	/**
	 * Delete an existing Project in database
	 * 
	 * @param id
	 *            The id of the project to delete
	 */
	@Transactional
	public void deleteProject(int id) {
		Project project = em.get().find(Project.class, id);
		em.get().remove(project);
	}

	/**
	 * Get all users from a project.
	 * 
	 * @param id
	 *            The id of the project.
	 * @return the list of users which are in the project.
	 */
	@Transactional
	public List<UserDTO> getProjectUsers(int id) {
		Project g = em.get().find(Project.class, id);
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : g.getUsers()) {
			dtos.add(new UserDTO(user.getUsername(), user.isLocked(), user.getId()));
		}
		return dtos;
	}

	/**
	 * Returns the total of projects for the current user
	 * 
	 * @return the value of the total of projects for the current user
	 */
	public Long getCurrentNbProjects() {
		List<ProjectDTO> projDTO = this.getAllProjects();
		if (projDTO == null) {
			return Long.valueOf(0);
		}
		if (!projDTO.isEmpty()) {
			Query query = em.get().createQuery("SELECT count(p) FROM Project p");
			return ((Long) query.getSingleResult());
		}
		return Long.valueOf(0);
	}

}
