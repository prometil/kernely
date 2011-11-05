package org.kernely.core.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.dto.GroupDTO;
import org.kernely.core.model.GroupModel;

import com.google.inject.Inject;

public class GroupService {

	@Inject
	private EntityManager em;
	
	/**
	 * Gets the lists of all groups contained in the database.
	 * @return the list of all groups contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<GroupDTO> getAllGroups() {
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM GroupModel e");
		List<GroupModel> collection = (List<GroupModel>) query.getResultList();
		List<GroupDTO> dtos = new ArrayList<GroupDTO>();
		for (GroupModel group : collection) {
			dtos.add(new GroupDTO(group.getName()));
		}
		em.getTransaction().commit();
		return dtos;

	}
}
