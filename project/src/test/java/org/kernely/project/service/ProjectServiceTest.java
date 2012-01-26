package org.kernely.project.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.omg.PortableServer.POA;

import com.google.inject.Inject;


public class ProjectServiceTest extends AbstractServiceTest {
	private static final String NAME="name";
	private static final String NAME_2="name2"; 
	
	@Inject
	private ProjectService projectService; 
	
	private ProjectDTO createProject(){
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name= NAME;
		projectService.createProject(proj);
		return projectService.getAllProjects().get(0);
	}
	
	@Test
	public void creationProjectTest(){
		ProjectDTO projDTO = this.createProject();
		assertEquals(NAME, projDTO.name);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creationProjectWithNullRequest(){
		projectService.createProject(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creationProjectWithNullName(){
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name= null;
		projectService.createProject(proj);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creationProjectWithVoidName(){
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name= "  ";
		projectService.createProject(proj);
	}
	
	@Test
	public void deleteProjectTest(){
		ProjectDTO projDTO = this.createProject();
		projectService.deleteProject(projDTO.id);
		assertEquals(0, projectService.getAllProjects().size());
	}
	
	@Test
	public void updateProjectTes(){
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO(NAME_2, projDTO.id);
		projectService.updateProject(proj);
		assertEquals(NAME_2, projectService.getAllProjects().get(0).name);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithNullRequest(){
		projectService.updateProject(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithNullName(){
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO(null, projDTO.id);
		projectService.updateProject(proj);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void updateProjectWithVoidName(){
		ProjectDTO projDTO = this.createProject();
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO("      ", projDTO.id);
		projectService.updateProject(proj);
	}
}
