package org.kernely.project.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;

import com.google.inject.Inject;


public class ProjectServiceTest extends AbstractServiceTest {
	private static final String NAME="name"; 
	
	@Inject
	private ProjectService projectService; 
		
	@Test
	public void creationProjectTest(){
		ProjectCreationRequestDTO proj = new ProjectCreationRequestDTO();
		proj.name= NAME;
		projectService.createProject(proj);
		ProjectDTO projDTO = projectService.getAllProjects().get(0);
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
}
