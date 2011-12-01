package org.kernely.core.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.model.User;

import com.google.inject.Inject;
import com.google.inject.Provider;

public abstract class AbstractService {
	@Inject
	protected Provider<EntityManager> em;

	protected User getAuthenticatedUserModel(){
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username ='"+ SecurityUtils.getSubject().getPrincipal() +"'");
		return (User)query.getSingleResult();
		
	}
}
