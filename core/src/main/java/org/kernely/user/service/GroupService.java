package org.kernely.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.hibernate.EntityManagerProvider;
import org.kernely.user.dto.GroupDTO;
import org.kernely.user.model.GroupModel;

import com.google.inject.Inject;

public class GroupService {

	@Inject
	private EntityManagerProvider entityManagerProvider;
	
	/**
	 * Gets the lists of all groups contained in the database.
	 * @return the list of all groups contained in the database.
	 */
	@SuppressWarnings("unchecked")
	public List<GroupDTO> getAllGroups() {
		EntityManager em = entityManagerProvider.getEM();
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM GroupModel e");
		List<GroupModel> collection = (List<GroupModel>) query.getResultList();
		List<GroupDTO> dtos = new ArrayList<GroupDTO>();
		for (GroupModel group : collection) {
			dtos.add(new GroupDTO(group.getName()));
		}
		em.getTransaction().commit();
		em.close();

		return dtos;

	}
}
