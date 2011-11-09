package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

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
		Query query = em.get().createQuery("SELECT e FROM GroupModel e");
		List<Group> collection = (List<Group>) query.getResultList();
		List<GroupDTO> dtos = new ArrayList<GroupDTO>();
		for (Group group : collection) {
			dtos.add(new GroupDTO(group.getName()));
		}
		return dtos;

	}
}
