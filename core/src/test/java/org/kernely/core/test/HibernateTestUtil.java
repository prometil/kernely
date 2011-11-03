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
package org.kernely.core.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.hibernate.ejb.Ejb3Configuration;
import org.kernely.core.hibernate.AbstractEntity;
import org.kernely.core.hibernate.EntityManagerProvider;
import org.kernely.core.hibernate.HibernateUtil;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * @author g.breton
 *
 */
public class HibernateTestUtil implements EntityManagerProvider {

	private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

	public Set<Class<? extends AbstractEntity>> classes;

	private PluginsLoader pluginLoader;

	/**
	 * 
	 */
	private EntityManagerFactory factory;


	/**
	 * Construct the hibernate util
	 */
	@Inject
	public HibernateTestUtil(PluginsLoader pPluginLoader) {

		classes = new HashSet<Class<? extends AbstractEntity>>();
		pluginLoader = pPluginLoader;
		addModels();

	}

	public void addModels() {
		List<? extends AbstractPlugin> plugins = pluginLoader.getPlugins();
		for (AbstractPlugin plugin : plugins) {
			if (plugin != null) {
				List<Class<? extends AbstractEntity>> models = plugin.getModels();
				if (models != null) {
					for (Class<? extends AbstractEntity> entity : models) {
						classes.add(entity);
					}
				} else {
					log.info("Model for plugin {} is null", plugin.getName());
				}

			}

		}
		refresh(PluginsLoader.class.getClassLoader());
	}

	public void refresh(ClassLoader loader) {
		log.debug("Configure");

		Ejb3Configuration cfg = getConfiguration();

		for (Class<? extends AbstractEntity> clazz : classes) {
			log.debug("Add annotation {} ", clazz);
			cfg.addAnnotatedClass(clazz);
		}
		factory = cfg.buildEntityManagerFactory();
	}

	public EntityManager getEM() {
		return factory.createEntityManager();

	}

	@Override
	public Ejb3Configuration getConfiguration() {
		Ejb3Configuration cfg = new Ejb3Configuration();
		cfg.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		cfg.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:aname");
		cfg.setProperty("hibernate.connection.username", "sa");
		cfg.setProperty("hibernate.connection.password",  "");
		cfg.setProperty("hibernate.connection.pool_size", "10");
		cfg.setProperty("show_sql", "true");
		cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		cfg.setProperty("hibernate.hbm2ddl.auto",  "update");
		return cfg;
	}
}
