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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

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

	@Before
	public void openTransactionBefor() {
		// service.start();
		if(!em.getTransaction().isActive()){
			em.getTransaction().begin();
		}
		 

	}

	@After
	public void closeTransactionAfter() {
		// service.stop();
		if(em.getTransaction().isActive()){
			em.getTransaction().rollback();
		}
	}
}
