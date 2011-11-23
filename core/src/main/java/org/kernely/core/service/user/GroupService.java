package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.GroupCreationRequestDTO;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.model.Group;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class GroupService {

	@Inject
	private Provider<EntityManager> em;
	
	/**
	 * Gets the lists of all groups contained in the database.
	 * @return the list of all groups contained in the database.
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<GroupDTO> getAllGroups() {
		Query query = em.get().createQuery("SELECT e FROM Group e");
		List<Group> collection = (List<Group>) query.getResultList();
		List<GroupDTO> dtos = new ArrayList<GroupDTO>();
		for (Group group : collection) {
			dtos.add(new GroupDTO(group.getName(), group.getId()));
		}
		return dtos;

	}
	
	/**
	 * Create a new Group in database
	 * @param request
	 * 			The request, containing group name
	 */
	@Transactional
	public void createGroup(GroupCreationRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if("".equals(request.name)){
			throw new IllegalArgumentException("Group name cannot be null ");
		}
		
		if("".equals(request.name.trim())){
			throw new IllegalArgumentException("Group name cannot be space character only ");
		}
		
		Group group = new Group();
		group.setName(request.name.trim());
		em.get().persist(group);
	}
	
	/**
	 * Update an existing group in database
	 * @param request
	 * 			The request, containing group name and id of the needed group
	 */
	@Transactional
	public void updateGroup(GroupCreationRequestDTO request) {
		if(request==null){
			throw new IllegalArgumentException("Request cannot be null ");
		}
		
		if("".equals(request.name)){
			throw new IllegalArgumentException("Group name cannot be null ");
		}
		
		if("".equals(request.name.trim())){
			throw new IllegalArgumentException("Group name cannot be space character only ");
		}
		
		Group group = em.get().find(Group.class, request.id);
		group.setName(request.name);
	}
	
	/**
	 * Delete an existing Group in database
	 * @param id
	 * 			The id of the group to delete
	 */
	@Transactional
	public void deleteGroup(int id) {
		Group group = em.get().find(Group.class, id);
		em.get().remove(group);
	}
}
