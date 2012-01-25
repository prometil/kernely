package org.kernely.project.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
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
			dtos.add(new ProjectDTO(project.getName(), project.getId()));
		}
		return dtos;
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

		if (request.name==null) {
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
		em.get().persist(project);
	}


}
