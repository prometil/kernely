package org.kernely.project.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.ClientCreationRequestDTO;
import org.kernely.project.dto.ClientDTO;

import com.google.inject.Inject;

public class ClientServiceTest extends AbstractServiceTest {
	private static final String NAME = "name";
	private static final String NAME_2 = "name2";

	@Inject
	private ClientService  clientService;

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
	
	private ClientDTO createClient(){
		ClientCreationRequestDTO client = new ClientCreationRequestDTO();
		client.address=NAME;
		client.city=NAME;
		client.email=NAME;
		client.fax=NAME;
		client.name=NAME;
		client.phone=NAME;
		client.zip=NAME;
		clientService.createClient(client);
		return clientService.getAllClients().get(0);		
	}
	
	@Test
	public void creationClientTest() {
		ClientDTO clientDTO = this.createClient();
		assertEquals(NAME, clientDTO.name);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creationClientWithNullRequest() {
		clientService.createClient(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationClientWithNullName() {
		ClientCreationRequestDTO proj = new ClientCreationRequestDTO();
		proj.name = null;
		clientService.createClient(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationClientWithVoidName() {
		ClientCreationRequestDTO proj = new ClientCreationRequestDTO();
		proj.name = "  ";
		clientService.createClient(proj);
	}
	
	@Test
	public void deleteClientTest() {
		ClientDTO clientDTO = this.createClient();
		clientService.deleteClient(clientDTO.id);
		assertEquals(0, clientService.getAllClients().size());
	}
	
	@Test
	public void updateClientTest() {
		ClientDTO clientDTO = this.createClient();
		ClientCreationRequestDTO proj = new ClientCreationRequestDTO(clientDTO.id, NAME_2, clientDTO.address, clientDTO.email, clientDTO.zip, clientDTO.city, clientDTO.phone, clientDTO.fax);
		clientService.updateClient(proj);
		assertEquals(NAME_2, clientService.getAllClients().get(0).name);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateClientWithNullRequest() {
		clientService.updateClient(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateClientWithNullName() {
		ClientDTO clientDTO = this.createClient();
		ClientCreationRequestDTO proj = new ClientCreationRequestDTO(clientDTO.id, null, clientDTO.address, clientDTO.email, clientDTO.zip, clientDTO.city, clientDTO.phone, clientDTO.fax);
		clientService.updateClient(proj);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateClientWithVoidName() {
		ClientDTO clientDTO = this.createClient();
		ClientCreationRequestDTO proj = new ClientCreationRequestDTO(clientDTO.id, "      ", clientDTO.address, clientDTO.email, clientDTO.zip, clientDTO.city, clientDTO.phone, clientDTO.fax);
		clientService.updateClient(proj);
	}
}
