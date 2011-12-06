package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.kernely.core.dto.GroupCreationRequestDTO;
import org.kernely.core.dto.GroupDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Group;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;

import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

@Singleton
public class GroupService extends AbstractService {
	
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
			List<UserDTO> users = new ArrayList<UserDTO>();
			for(User u : group.getUsers()){
				users.add(new UserDTO(u.getUsername(), u.isLocked(), u.getId()));
			}
			dtos.add(new GroupDTO(group.getName(), group.getId(), users));
		}
		return dtos;

	}
	
	/**
	 * Create a new Group in database
	 * @param request
	 * 			The request, containing group name
	 */
	@SuppressWarnings("unchecked")
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
		
		Query verifExist = em.get().createQuery("SELECT g FROM Group g WHERE name='"+ request.name +"'");
		List<Group> list = (List<Group>)verifExist.getResultList();
		if(!list.isEmpty()){
			throw new IllegalArgumentException("Another group with this name already exists");
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
	@SuppressWarnings("unchecked")
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
		
		Query verifExist = em.get().createQuery("SELECT g FROM Group g WHERE name='"+ request.name +"'AND group_id != "+ request.id);
		List<Group> list = (List<Group>)verifExist.getResultList();
		if(!list.isEmpty()){
			throw new IllegalArgumentException("Another group with this name already exists");
		}
		
		Set<User> users = null;
		if(!request.users.isEmpty()){
			if(!(request.users.get(0).username == null)){
				users = new HashSet<User>();
				for(UserDTO u: request.users){
					users.add(em.get().find(User.class, u.id));
				}
			}
		}
		Group group = em.get().find(Group.class, request.id);
		group.setName(request.name);
		
		
		if(users == null){
			group.getUsers().clear();
		}
		else{
			group.setUsers(users);
		}
			
		
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
	
	@Transactional
	public List<UserDTO> getGroupUsers(int id){
		Group g = em.get().find(Group.class, id);
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (User user : g.getUsers()) {
			dtos.add(new UserDTO(user.getUsername(), user.isLocked(), user.getId()));
		}
		return dtos;
	}
}
