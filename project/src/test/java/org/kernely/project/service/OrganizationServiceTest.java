package org.kernely.project.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.OrganizationDTO;

import com.google.inject.Inject;

public class OrganizationServiceTest extends AbstractServiceTest {
	private static final String NAME = "name";
	private static final String NAME_2 = "name2";

	@Inject
	private OrganizationService  organizationService;

	
	private OrganizationDTO createOrganization(){
		OrganizationCreationRequestDTO organization = new OrganizationCreationRequestDTO();
		organization.address=NAME;
		organization.city=NAME;
		organization.fax=NAME;
		organization.name=NAME;
		organization.phone=NAME;
		organization.zip=NAME;
		return organizationService.createOrganization(organization);
		
	}
	
	@Test
	public void getOrganizationByNameTest(){
		createOrganization();
		assertEquals(NAME, organizationService.getOrganizationByName(NAME).getName());
	}
	
	@Test
	public void getOrganizationTest(){
		OrganizationDTO created = createOrganization();
		assertEquals(NAME, organizationService.getOrganization(created.id).name);
	}
	
	@Test
	public void creationOrganizationTest() {
		OrganizationDTO organizationDTO = this.createOrganization();
		assertEquals(NAME, organizationDTO.name);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creationOrganizationWithNullRequest() {
		organizationService.createOrganization(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationOrganizationWithNullName() {
		OrganizationCreationRequestDTO proj = new OrganizationCreationRequestDTO();
		proj.name = null;
		organizationService.createOrganization(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationOrganizationWithVoidName() {
		OrganizationCreationRequestDTO proj = new OrganizationCreationRequestDTO();
		proj.name = "  ";
		organizationService.createOrganization(proj);
	}
	
	@Test
	public void deleteOrganizationTest() {
		OrganizationDTO organizationDTO = this.createOrganization();
		organizationService.deleteOrganization(organizationDTO.id);
		assertEquals(0, organizationService.getAllOrganizations().size());
	}
	
	@Test
	public void updateOrganizationTest() {
		OrganizationDTO organizationDTO = this.createOrganization();
		OrganizationCreationRequestDTO proj = new OrganizationCreationRequestDTO(organizationDTO.id, NAME_2, organizationDTO.address, organizationDTO.zip, organizationDTO.city, organizationDTO.phone, organizationDTO.fax, organizationDTO.users);
		organizationService.updateOrganization(proj);
		assertEquals(NAME_2, organizationService.getAllOrganizations().get(0).name);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateOrganizationWithNullRequest() {
		organizationService.updateOrganization(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateOrganizationWithNullName() {
		OrganizationDTO organizationDTO = this.createOrganization();
		OrganizationCreationRequestDTO proj = new OrganizationCreationRequestDTO(organizationDTO.id, null, organizationDTO.address, organizationDTO.zip, organizationDTO.city, organizationDTO.phone, organizationDTO.fax, organizationDTO.users);
		organizationService.updateOrganization(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateOrganizationWithVoidName() {
		OrganizationDTO organizationDTO = this.createOrganization();
		OrganizationCreationRequestDTO proj = new OrganizationCreationRequestDTO(organizationDTO.id, "      ", organizationDTO.address, organizationDTO.zip, organizationDTO.city, organizationDTO.phone, organizationDTO.fax, organizationDTO.users);
		organizationService.updateOrganization(proj);
	}
}
