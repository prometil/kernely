/**
 * 
 */
package org.kernely.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.kernely.core.hibernate.HibernateUtil;
import org.kernely.user.dto.UserCreationRequestDTO;
import org.kernely.user.dto.UserDTO;
import org.kernely.user.model.UserModel;

import com.google.inject.Inject;

/**
 * @author g.breton
 * 
 */
public class UserService {

	@Inject
	private HibernateUtil hibernateUtil;

	public void createUser(UserCreationRequestDTO request) {
		EntityManager em = hibernateUtil.getEM();
		em.getTransaction().begin();
		UserModel user = new UserModel();
		user.setPassword(request.password);
		user.setUsername(request.username);
		em.persist(user);
		em.getTransaction().commit();
		em.close();

	}

	@SuppressWarnings("unchecked")
	public List<UserDTO> getAllUsers() {
		EntityManager em = hibernateUtil.getEM();
		em.getTransaction().begin();
		Query query = em.createQuery("SELECT e FROM UserModel e");
		List<UserModel> collection = (List<UserModel>) query.getResultList();
		List<UserDTO> dtos = new ArrayList<UserDTO>();
		for (UserModel user : collection) {
			dtos.add(new UserDTO(user.getUsername()));
		}
		em.getTransaction().commit();
		em.close();

		return dtos;

	}
}
