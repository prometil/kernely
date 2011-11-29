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
package org.kernely.core.common;

import javax.persistence.EntityManager;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.LifecycleUtils;
import org.apache.shiro.util.ThreadState;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.kernely.core.model.User;
import org.mockito.Mockito;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

/**
 * @author g.breton
 * 
 */
public abstract class AbstractServiceTest {

	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(TestModule.class);

	@Inject
	Initializer initializer;

	@Inject
	EntityManager em;

	@Inject
	PersistService service;

	private static ThreadState subjectThreadState;

	@Before
	public void openTransactionBefor() {
		// service.start();
		if (!em.getTransaction().isActive()) {
			em.getTransaction().begin();
		}

	}

	@After
	public void closeTransactionAfter() {
		// service.stop();
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		clearSubject();
	}

	private static void doClearSubject() {
		if (subjectThreadState != null) {
			subjectThreadState.clear();
			subjectThreadState = null;
		}
	}

	/**
	 * Allows subclasses to set the currently executing {@link Subject}
	 * instance.
	 * 
	 * @param subject
	 *            the Subject instance
	 */
	protected void setSubject(Subject subject) {
		clearSubject();
		subjectThreadState = createThreadState(subject);
		subjectThreadState.bind();
	}

	protected void clearSubject() {
		doClearSubject();
	}

	protected Subject getSubject() {
		return SecurityUtils.getSubject();
	}

	protected ThreadState createThreadState(Subject subject) {
		return new SubjectThreadState(subject);
	}

	protected static void setSecurityManager(SecurityManager securityManager) {
		SecurityUtils.setSecurityManager(securityManager);
	}

	protected static SecurityManager getSecurityManager() {
		return SecurityUtils.getSecurityManager();
	}

	@AfterClass
	public static void tearDownShiro() {
		doClearSubject();
		try {
			SecurityManager securityManager = getSecurityManager();
			LifecycleUtils.destroy(securityManager);
		} catch (UnavailableSecurityManagerException e) {
			// we don't care about this when cleaning up the test environment
			// (for example, maybe the subclass is a unit test and it didn't
			// need a SecurityManager instance because it was using only
			// mock Subject instances)
		}
		setSecurityManager(null);
	}

	public void authenticateAs(String username) {
		doClearSubject();
		Subject s = Mockito.mock(Subject.class);
		Mockito.when(s.isAuthenticated()).thenReturn(true);
		Mockito.when(s.getPrincipal()).thenReturn(username);
		setSubject(s);
	}
}
