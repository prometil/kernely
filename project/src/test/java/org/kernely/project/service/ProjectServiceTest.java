package org.kernely.project.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.PermissionService;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.model.Project;

import com.google.inject.Inject;

public class ProjectServiceTest extends AbstractServiceTest {
	private static final String NAME = "name";
	private static final String NAME_2 = "name2";

	@Inject
	private ProjectService projectService;

	@Inject
	private UserService userService;

	@Inject
	private OrganizationService  organizationService;
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private PermissionService permissionService; 

	private final String TEST_STRING = "test_string";

	private long creationOfTestUser() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = TEST_STRING;
		request.password = TEST_STRING;
		request.firstname = TEST_STRING;
		request.lastname = TEST_STRING;
		UserDTO userDTO = userService.createUser(request);
		return userDTO.id;
	}
	
	private OrganizationDTO createOrganization(){
		OrganizationCreationRequestDTO organization = new OrganizationCreationRequestDTO();
		organization.address=NAME;
		organization.city=NAME;
		organization.fax=NAME;
		organization.name=NAME;
		organization.phone=NAME;
		organization.zip=NAME;
		organizationService.createOrganization(organization);
		return organizationService.getAllOrganizations().get(0);		
	}

	private ProjectDTO createProject(){
		createOrganization();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name = NAME;
		proj.organization=NAME;
		projectService.createProject(proj);
		return projectService.getAllProjects().get(0);
	}

	@Test
	public void creationProjectTest() {
		ProjectDTO projDTO = this.createProject();
		assertEquals(NAME, projDTO.name);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationProjectWithNullRequest() {
		projectService.createProject(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationProjectWithNullName() {
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name = null;
		projectService.createProject(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationProjectWithVoidName() {
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name = "  ";
		projectService.createProject(proj);
	}

	@Test
	public void deleteProjectTest() {
		ProjectDTO projDTO = this.createProject();
		projectService.deleteProject(projDTO.id);
		assertEquals(0, projectService.getAllProjects().size());
	}

	@Test
	public void updateProjectTest() {
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO(NAME_2, projDTO.id, projDTO.icon, projDTO.organization.name);
		projectService.updateProject(proj);
		assertEquals(NAME_2, projectService.getAllProjects().get(0).name);
	}
	
	@Test
	public void updateProjectIconTest() {
		ProjectDTO projDTO = this.createProject();
		projectService.updateProjectIcon(projDTO.name, NAME_2);
		assertEquals(NAME_2, projectService.getAllProjects().get(0).icon);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithNullRequest() {
		projectService.updateProject(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithNullName() {
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO(null, projDTO.id, projDTO.icon,  projDTO.organization.name);
		projectService.updateProject(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithVoidName() {
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO("      ", projDTO.id, projDTO.icon, projDTO.organization.name);
		projectService.updateProject(proj);
	}

	@Test
	public void testUserHasNoRight() {
		this.creationOfTestUser();
		ProjectDTO proj = this.createProject();
		authenticateAs(TEST_STRING);

		assertEquals(false, projectService.currentUserHasRightsOnProject(Project.RIGHT_CLIENT, proj.id));
		assertEquals(false, projectService.currentUserHasRightsOnProject(Project.RIGHT_CONTRIBUTOR, proj.id));
		assertEquals(false, projectService.currentUserHasRightsOnProject(Project.RIGHT_PROJECTMANAGER, proj.id));
	}
	
	@Test
	public void testUserHasRight(){
		long id = this.creationOfTestUser();
		ProjectDTO project = this.createProject();
		authenticateAs(TEST_STRING);
		
		permissionService.grantPermission(id, Project.RIGHT_CLIENT, Project.PROJECT_RESOURCE, project.id);
		assertEquals(true, projectService.currentUserHasRightsOnProject(Project.RIGHT_CLIENT, project.id));	
	}
	
	@Test
	public void getNoProjectForSpecificUser(){
		long userId = this.creationOfTestUser();
		List<ProjectDTO> projects = projectService.getAllProjectsForUser(userId);
		assertEquals(0,projects.size());
	}
	
	@Test
	public void getProjectForContributorUser(){
		long userId = this.creationOfTestUser();
		ProjectDTO project = this.createProject();
		
		// Associate user to project by setting permission
		permissionService.grantPermission(userId, Project.RIGHT_CONTRIBUTOR, Project.PROJECT_RESOURCE, project.id);
		
		List<ProjectDTO> projects = projectService.getAllProjectsForUser(userId);
		assertEquals(1,projects.size());
		assertEquals(project,projects.get(0));
	}

	@Test
	public void getProjectForManagerUser(){
		long userId = this.creationOfTestUser();
		ProjectDTO project = this.createProject();
		
		// Associate user to project by setting permission
		permissionService.grantPermission(userId, Project.RIGHT_PROJECTMANAGER, Project.PROJECT_RESOURCE, project.id);
		
		List<ProjectDTO> projects = projectService.getAllProjectsForUser(userId);
		assertEquals(1,projects.size());
		assertEquals(project,projects.get(0));
	}

	@Test
	public void getProjectForManagerAndContributorUser(){
		long userId = this.creationOfTestUser();
		ProjectDTO project = this.createProject();
		
		// Associate user to project by setting permissions
		permissionService.grantPermission(userId, Project.RIGHT_CONTRIBUTOR, Project.PROJECT_RESOURCE, project.id);
		permissionService.grantPermission(userId, Project.RIGHT_PROJECTMANAGER, Project.PROJECT_RESOURCE, project.id);
		
		List<ProjectDTO> projects = projectService.getAllProjectsForUser(userId);
		assertEquals(1,projects.size());
		assertEquals(project,projects.get(0));
	}
	
}
