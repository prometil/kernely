package org.kernely.project.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;

import com.google.inject.Inject;

public class ProjectServiceTest extends AbstractServiceTest {
	private static final String NAME = "name";
	private static final String NAME_2 = "name2";

	@Inject
	private ProjectService projectService;

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

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

	private ProjectDTO createProject() {
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name = NAME;
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
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO(NAME_2, projDTO.id, new ArrayList<UserDTO>());
		projectService.updateProject(proj);
		assertEquals(NAME_2, projectService.getAllProjects().get(0).name);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithNullRequest() {
		projectService.updateProject(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithNullName() {
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO(null, projDTO.id, new ArrayList<UserDTO>());
		projectService.updateProject(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithVoidName() {
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO("      ", projDTO.id, projDTO.users);
		projectService.updateProject(proj);
	}

	@Test
	public void getProjectUser() {
		this.createProject();
		ProjectDTO projectdto = new ProjectDTO();
		projectdto = projectService.getAllProjects().get(0);

		this.creationOfTestUser();

		UserDTO userdto = new UserDTO();
		userdto = userService.getAllUsers().get(0);

		List<UserDTO> users = new ArrayList<UserDTO>();
		users.add(userdto);
		ProjectCreationRequestDTO gcr = new ProjectCreationRequestDTO(projectdto.name, projectdto.id, users);
		projectService.updateProject(gcr);

		assertEquals(1, projectService.getProjectUsers(projectdto.id).size());
	}

	@Test
	public void addProjectUser() {
		ProjectCreationRequestDTO request = new ProjectCreationRequestDTO();
		request.name = "Test Project";
		projectService.createProject(request);
		ProjectDTO projectdto = new ProjectDTO();
		projectdto = projectService.getAllProjects().get(0);

		this.creationOfTestUser();

		UserDTO userdto = new UserDTO();
		userdto = userService.getAllUsers().get(0);

		List<UserDTO> users = new ArrayList<UserDTO>();
		users.add(userdto);
		ProjectCreationRequestDTO gcr = new ProjectCreationRequestDTO(projectdto.name, projectdto.id, users);
		projectService.updateProject(gcr);

		projectdto = projectService.getAllProjects().get(0);
		assertEquals(1, projectdto.users.size());
		assertEquals(TEST_STRING, projectdto.users.get(0).username);

		gcr = new ProjectCreationRequestDTO(projectdto.name, projectdto.id, new ArrayList<UserDTO>());
		projectService.updateProject(gcr);

		projectdto = projectService.getAllProjects().get(0);
		assertEquals(0, projectdto.users.size());
		assertEquals(1, userService.getAllUsers().size());
	}
}
