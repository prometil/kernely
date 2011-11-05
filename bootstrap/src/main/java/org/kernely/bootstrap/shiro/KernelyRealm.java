/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.bootstrap.shiro;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.kernely.core.model.Permission;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.stream.model.StreamMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class KernelyRealm extends AuthorizingRealm {

	private static final Logger log = LoggerFactory.getLogger(KernelyRealm.class);

	@Inject
	private EntityManager em;

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		log.debug("HEREEEEE");
		try{
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		em.getTransaction().begin();
		String username = upToken.getUsername();
		if (username == null) {
			log.debug("HEREEEEE");
			throw new AccountException("Null usernames are not allowed by this realm.");
		}
		log.debug("HEREEEEE");
		Query query = em.createQuery("SELECT e FROM UserModel e where username='" + username + "'");
		
			
			log.debug("HEREEEEE");
			StreamMessage m = new StreamMessage();
			m.setMessage(UUID.randomUUID().toString());
			em.persist(m);
			log.debug("{}",m.getId());
			
			User userModel = (User) query.getResultList().get(0);
			String password = userModel.getPassword();
			em.getTransaction().commit();
					return new SimpleAuthenticationInfo(username, password, getName());
		}
		catch(Exception e){
			log.error("",e);
			return null;
		}
		
	}

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		if (principals == null) {
			throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
		}
		String username = (String) principals.fromRealm(getName()).iterator().next();
		Query query = em.createQuery("SELECT e FROM UserModel e where username='" + username + "'");
		User user = ((User) query.getResultList().get(0));
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