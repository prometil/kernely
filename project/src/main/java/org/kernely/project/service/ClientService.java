package org.kernely.project.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.project.dto.ClientCreationRequestDTO;
import org.kernely.project.dto.ClientDTO;
import org.kernely.project.model.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;


/**
 * The service for client pages
 * 
 */
@Singleton
public class ClientService extends AbstractService{
	@Inject
	UserService userService;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Gets the lists of all clients contained in the database.
	 * 
	 * @return the list of all clients contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<ClientDTO> getAllClients() {
		Query query = em.get().createQuery("SELECT e FROM Client e");
		List<Client> collection = (List<Client>) query.getResultList();
		List<ClientDTO> dtos = new ArrayList<ClientDTO>();
		for (Client client : collection) {
			dtos.add(new ClientDTO(client));
		}
		return dtos;
	}

	/**
	 * Create a new Client in database
	 * 
	 * @param request
	 *            The request, containing client name
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void createClient(ClientCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name==null) {
			throw new IllegalArgumentException("Client name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Client name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT g FROM Client g WHERE name=:name");
		verifExist.setParameter("name", request.name);
		List<Client> list = (List<Client>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another client with this name already exists");
		}

		Client client = new Client();
		client.setName(request.name);
		client.setAddress(request.address);
		client.setZip(request.zip);
		client.setCity(request.city);
		client.setEmail(request.email);
		client.setFax(request.fax);
		client.setPhone(request.phone);
		em.get().persist(client);
	}
	
	
	/**
	 * Update an existing  client in database
	 * 
	 * @param request
	 *            The request, containing  client name and id of the needed  client
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateClient(ClientCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		if (request.name==null) {
			throw new IllegalArgumentException("Client name cannot be null ");
		}

		if ("".equals(request.name.trim())) {
			throw new IllegalArgumentException("Client name cannot be space character only ");
		}

		Query verifExist = em.get().createQuery("SELECT g FROM Client g WHERE name=:name AND id !=:id");
		verifExist.setParameter("name", request.name);
		verifExist.setParameter("id", request.id);
		List<Client> list = (List<Client>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Another  client with this name already exists");
		}
		
		Client client = em.get().find(Client.class, request.id);
		client.setName(request.name);
		client.setAddress(request.address);
		client.setCity(request.city);
		client.setEmail(request.email);
		client.setZip(request.zip);
		client.setFax(request.fax);
		client.setPhone(request.phone);
	}
	
	/**
	 * Delete an existing Client in database
	 * 
	 * @param id
	 *            The id of the client to delete
	 */
	@Transactional
	public void deleteClient(int id) {
		Client client = em.get().find(Client.class, id);
		em.get().remove(client);
	}
	
}
