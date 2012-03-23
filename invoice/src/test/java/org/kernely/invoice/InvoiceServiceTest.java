package org.kernely.invoice;

import static junit.framework.Assert.*;

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.invoice.dto.InvoiceCreationRequestDTO;
import org.kernely.invoice.dto.InvoiceDTO;
import org.kernely.invoice.model.Invoice;
import org.kernely.invoice.service.InvoiceService;
import org.kernely.project.dto.OrganizationCreationRequestDTO;
import org.kernely.project.dto.OrganizationDTO;
import org.kernely.project.dto.ProjectCreationRequestDTO;
import org.kernely.project.dto.ProjectDTO;
import org.kernely.project.service.OrganizationService;
import org.kernely.project.service.ProjectService;

import com.google.inject.Inject;

public class InvoiceServiceTest extends AbstractServiceTest{

	private static final String NAME_ORGANIZATION = "Kernely CORP";
	private static final String NAME_PROJECT1 = "Secret Project 1";
	private static final String NAME_PROJECT2 = "Secret Project 2";
	private static final Date DATE_PUBLICATION = new DateTime().plusMonths(1).withDayOfMonth(3).toDateMidnight().toDate();
	private static final Date DATE_TERM = new DateTime().plusMonths(2).withDayOfMonth(3).toDateMidnight().toDate();
	private static final String DATE_PUBLICATION_STRING = new DateTime().plusMonths(1).withDayOfMonth(3).toDateMidnight().toString("MM/dd/yyyy");
	private static final String DATE_TERM_STRING = new DateTime().plusMonths(2).withDayOfMonth(3).toDateMidnight().toString("MM/dd/yyyy");
	private static final String OBJECT = "INVOICE OBJECT";
	
	
	@Inject
	private InvoiceService invoiceService;
	
	@Inject
	private ProjectService projectService;

	@Inject
	private OrganizationService organizationService;

	@Inject
	private RoleService roleService;

	@Inject
	private UserService userService;
	
	
	private UserDTO createUserForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = "User";
		request.password = "Password";
		return userService.createUser(request);
	}
	
	private OrganizationDTO createOrganizationForTest(){
		OrganizationCreationRequestDTO organizationRequest = new OrganizationCreationRequestDTO(1, NAME_ORGANIZATION, null, null, null, null, null, null);
		return organizationService.createOrganization(organizationRequest);
	}
	
	private ProjectDTO createProject1ForTest(){
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME_PROJECT1, 1, NAME_PROJECT1, NAME_ORGANIZATION);
		return projectService.createProject(projectRequest);
	}
	
	private ProjectDTO createProject2ForTest(){
		ProjectCreationRequestDTO projectRequest = new ProjectCreationRequestDTO(NAME_PROJECT2, 1, NAME_PROJECT2, NAME_ORGANIZATION);
		return projectService.createProject(projectRequest);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void createInvoiceWithNullRequestTest(){
		invoiceService.createOrUpdateInvoice(null);
	}
			
	@Test
	public void createInvoiceTest(){
		OrganizationDTO organization = createOrganizationForTest();
		ProjectDTO project1 = createProject1ForTest();
		InvoiceCreationRequestDTO request = new InvoiceCreationRequestDTO();
		request.id = 0;
		request.datePublication = DATE_PUBLICATION_STRING;
		request.dateTerm = DATE_TERM_STRING;
		request.projectId = project1.id;
		request.object= OBJECT;
		
		InvoiceDTO invoiceDTO = invoiceService.createOrUpdateInvoice(request);
		
		assertEquals(DATE_PUBLICATION, invoiceDTO.datePublication);
		assertEquals(DATE_TERM, invoiceDTO.dateTerm);
		assertEquals(DateTime.now().getMonthOfYear(), new DateTime(invoiceDTO.dateCreation).getMonthOfYear());
		assertEquals(DateTime.now().getDayOfMonth(), new DateTime(invoiceDTO.dateCreation).getDayOfMonth());
		assertEquals(DateTime.now().getHourOfDay(), new DateTime(invoiceDTO.dateCreation).getHourOfDay());
		assertEquals(DateTime.now().getMinuteOfHour(), new DateTime(invoiceDTO.dateCreation).getMinuteOfHour());
		
		assertEquals(OBJECT, invoiceDTO.object);
		assertEquals(organization.name, invoiceDTO.organizationName);
		assertEquals(project1.name, invoiceDTO.projectName);
		assertEquals(Invoice.INVOICE_UNDEFINED, invoiceDTO.status);
	}
	
	
}
