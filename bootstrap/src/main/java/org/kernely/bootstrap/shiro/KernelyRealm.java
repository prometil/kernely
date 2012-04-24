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

package org.kernely.bootstrap.shiro;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.PasswordMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.kernely.core.model.Permission;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Definition of the realm for shiro
 */
public class KernelyRealm extends AuthorizingRealm {

	private static Logger log = LoggerFactory.getLogger(KernelyRealm.class);

	@Inject
	private Provider<EntityManager> em;
	
	@Inject
	public KernelyRealm(PasswordMatcher passwordMatcher){
		super(passwordMatcher);
	}

	/**
	 * Handle the authentication of kernely
	 */
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
		
		try {

			UsernamePasswordToken upToken = (UsernamePasswordToken) token;
			em.get().getTransaction().begin();
			String username = upToken.getUsername();
			if (username == null) {
				throw new AccountException("Null usernames are not allowed by this realm.");
			}
			final Query query = em.get().createQuery("SELECT e FROM User e where username = :username AND locked = false");
			query.setParameter("username", username);
			final User userModel = (User) query.getResultList().get(0);
			em.get().getTransaction().commit();
			return new SimpleAuthenticationInfo(upToken.getPrincipal(), userModel.getPassword(), getName());
			
		} catch (Exception e) {
			log.error("Autentication error", e);
			em.get().getTransaction().commit();
			return null;
		}
	}

	/**
	 * Handle the authorization of kernely
	 */
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		log.debug("Get autorization from database");
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		String username = (String) principals.fromRealm(getName()).iterator().next();
		Query query = em.get().createQuery("SELECT e FROM User e where username= :username");
		query.setParameter("username", username);
		User user = ((User) query.getSingleResult());
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		if (user != null) {
			for (Role role : user.getAllRoles()) {
				info.addRole(role.getName());
			}
			for (Permission perm : user.getAllPermissions()) {
				info.addStringPermission(perm.getName());
			}
		}
		return info;
	}

}