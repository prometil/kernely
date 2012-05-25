/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.service;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.model.User;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * The abstract class   for service
 * @author b.grandperret
 *
 */
public abstract class AbstractService {
	@Inject
	protected Provider<EntityManager> em;

	/**
	 * 
	 * @return user
	 */
	protected User getAuthenticatedUserModel(){
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username = :username");
		query.setParameter("username", SecurityUtils.getSubject().getPrincipal());
		return (User)query.getSingleResult();
	}
}
