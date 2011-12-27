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
package org.kernely.core.hibernate;

import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernatePersistence;
import org.kernely.core.plugin.AbstractPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KernelyHibernatePersistence extends HibernatePersistence {

	public static Logger log = LoggerFactory.getLogger(KernelyHibernatePersistence.class);

	@SuppressWarnings("unchecked")
	@Override
	public EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map overridenProperties) {

		Ejb3Configuration cfg = new Ejb3Configuration();
		for (Object entry : overridenProperties.entrySet()) {

			Map.Entry<String, String> mapEntry = (Map.Entry<String, String>) entry;
			log.trace("Hibernate persistence property : {} - {}", mapEntry.getKey(), mapEntry.getValue());
			cfg.setProperty(mapEntry.getKey(), mapEntry.getValue());
		}

		ServiceLoader<AbstractPlugin> commandLoader = ServiceLoader.load(AbstractPlugin.class);
		Iterator<AbstractPlugin> it = commandLoader.iterator();
		while (it.hasNext()) {
			AbstractPlugin plugin = it.next();

			for (Class<? extends AbstractModel> entityClass : plugin.getModels()) {
				log.debug("Add annotated class : {}", entityClass);
				cfg.addAnnotatedClass(entityClass);
			}
		}
		return cfg.buildEntityManagerFactory();
	}

}
