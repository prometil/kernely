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
package org.kernely.core.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.configuration.AbstractConfiguration;
import org.hibernate.ejb.Ejb3Configuration;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.servlet.RequestScoped;
public class HibernateUtil implements EntityManagerProvider {

	private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

	public Set<Class<? extends AbstractEntity>> classes;

	private PluginsLoader pluginLoader;

	private EntityManagerFactory factory;

	private AbstractConfiguration configuration;

	/**
	 * Construct the hibernate util
	 */
	@Inject
	public HibernateUtil(PluginsLoader pPluginLoader, AbstractConfiguration pConfiguration) {

		classes = new HashSet<Class<? extends AbstractEntity>>();
		pluginLoader = pPluginLoader;
		configuration = pConfiguration;
		addModels();

	}

	/**
	 * Search models in all detected plugins and persists all entities.
	 */
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

	/**
	 * Refresh HibernateUtil by searching new annotated classes.
	 * @param loader
	 */
	public void refresh(ClassLoader loader) {
		log.debug("Configure");

		Ejb3Configuration cfg = getConfiguration();
		for (Class<? extends AbstractEntity> clazz : classes) {
			log.debug("Add annotation {} ", clazz);
			cfg.addAnnotatedClass(clazz);
		}
		factory = cfg.buildEntityManagerFactory();
	}

	/**
	 * Get the Entity Manager.
	 * @return the entity manageR.
	 */
	public EntityManager getEM() {
		return factory.createEntityManager();

	}

	/**
	 * Get the EJB configuration of HibernateUtil
	 */
	@Override
	public Ejb3Configuration getConfiguration() {
		Ejb3Configuration cfg = new Ejb3Configuration();
		cfg.setProperty("hibernate.connection.driver_class", configuration.getString("hibernate.driver_class","org.postgresql.Driver"));
		cfg.setProperty("hibernate.connection.url", configuration.getString("hibernate.url","jdbc:postgresql://localhost:5432/kernely_db"));
		cfg.setProperty("hibernate.connection.username", configuration.getString("hibernate.username","postgres"));
		cfg.setProperty("hibernate.connection.password", configuration.getString("hibernate.password","kernely"));
		cfg.setProperty("hibernate.connection.pool_size", configuration.getString("hibernate.pool_size","10"));
		cfg.setProperty("show_sql", configuration.getString("hibernate.show_sql","true"));
		cfg.setProperty("hibernate.dialect", configuration.getString("hibernate.dialect","org.hibernate.dialect.PostgreSQLDialect"));
		cfg.setProperty("hibernate.hbm2ddl.auto", configuration.getString("hibernate.hbm2ddl.auto","update"));
		return cfg;
	}
}
