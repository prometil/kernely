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
package org.kernely.plugin;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.kernely.controller.AbstractController;
import org.kernely.core.dto.AdminPageDTO;
import org.kernely.menu.Menu;
import org.kernely.menu.MenuItem;
import org.kernely.migrator.Migration;
import org.kernely.persistence.AbstractModel;
import org.quartz.Job;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

/**
 * The abstract class for a plugin
 * 
 * @author b.grandperret
 * 
 */
public abstract class AbstractPlugin extends AbstractModule {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	protected Descriptor manifest;

	// the controller list
	private List<Class<? extends AbstractController>> controllers;

	// the model list
	private List<Class<? extends AbstractModel>> models;

	// the migration list
	private SortedSet<Migration> migrations;

	// the admin page
	private List<AdminPageDTO> adminPages;

	// the job map
	private Map<Class<? extends Job>, Trigger> jobs;

	// the name of the abstract plugin
	private List<String> menus;

	// the path of the plugin
	private List<String> path;

	// menu items
	private List<MenuItem> menuItems = new ArrayList<MenuItem>();

	/**
	 * the constructor
	 * 
	 * @param pName
	 *            the name of the plugin
	 */
	public AbstractPlugin() {
		menus = new ArrayList<String>();
		path = new ArrayList<String>();
		controllers = new ArrayList<Class<? extends AbstractController>>();
		models = new ArrayList<Class<? extends AbstractModel>>();
		adminPages = new ArrayList<AdminPageDTO>();
		jobs = new HashMap<Class<? extends Job>, Trigger>();
		migrations = new TreeSet<Migration>();

	}

	/**
	 * The plugin is injected just before this method.
	 */
	public void start() {

	}

	/**
	 * Register a new admin page. Don't forget to also register admin pages
	 * controllers.
	 * 
	 * @param name
	 *            The displayed name of the admin page.
	 * @param path
	 *            The name without special characters : will be in the url to
	 *            access to the admin page.
	 */
	protected void registerAdminPage(String name, String pathToAdmin) {
		log.trace("Register admin page {},{}", name, pathToAdmin);
		this.adminPages.add(new AdminPageDTO(name, pathToAdmin));
	}

	/**
	 * Register a new model.
	 * 
	 * @param model
	 */
	protected void registerModel(Class<? extends AbstractModel> model) {
		models.add(model);

	}

	/**
	 * register a new controller in the application, and look for menu.
	 * 
	 * @param controller
	 */
	protected void registerController(Class<? extends AbstractController> controller) {
		// introspect all the class
		Method[] methods = controller.getDeclaredMethods();
		Annotation[] annotations = controller.getAnnotations();
		Path prefixPath = null;
		for (Annotation anno : annotations) {
			if (Path.class.getName().equals(anno.annotationType().getName())) {
				prefixPath = (Path) anno;
			}
		}

		for (Method method : methods) {
			Annotation[] annos = method.getAnnotations();
			Menu menu = null;
			Path path = null;
			RequiresRoles roles = null;
			for (Annotation anno : annos) {
				if (Menu.class.getName().equals(anno.annotationType().getName())) {
					menu = (Menu) anno;
				}
				if (Path.class.getName().equals(anno.annotationType().getName())) {
					path = (Path) anno;
				}
				if (RequiresRoles.class.getName().equals(anno.annotationType().getName())) {
					roles = (RequiresRoles) anno;
				}
			}
			if ( menu != null && (path != null || prefixPath != null)) {
				String link = "";
				if(prefixPath != null){
					link = prefixPath.value();
				}
				if(path != null){
					link = link+"/"+path.value();
				}
			
				if (roles != null) {
					log.debug("Add menu {} -> {}", menu.value(), link);
					menuItems.add(new MenuItem(menu.value(), link, roles.value()));
				} else {
					log.debug("Add menu {} -> {}", menu.value(), link);
					menuItems.add(new MenuItem(menu.value(), link));
				}
			}

		}
		controllers.add(controller);
	}

	/**
	 * Register a menu
	 * 
	 * @param key
	 *            the composed key (i.e key1/key2/key3)
	 * @param path
	 */
	protected void registerMenu(String key, String path) {

	}

	/**
	 * Register a new path
	 * 
	 * @param String
	 *            path
	 */
	protected void registerPath(String path) {
		this.path.add(path);
	}

	/**
	 * Register a new name
	 * 
	 * @param String
	 *            name
	 */
	protected void registerName(String name) {
		this.menus.add(name);
	}

	/**
	 * return the module
	 * 
	 * @return
	 */
	public Module getModule() {
		return this;
	}

	/**
	 * Returns the name of the plugin
	 * 
	 * @return the name of the plugin
	 */
	public List<String> getMenus() {
		return menus;
	}

	/**
	 * Return the path for the plugin main page. If not null, the plugin name
	 * will appear in Kernely menu, and clicking on it display the page
	 * targetted by this path. If null, the plugin name will not appear in
	 * Kernely menu.
	 * 
	 * @return the path to the plugin main page, or null if the plugin does'nt
	 *         have a main page.
	 */
	public List<String> getPath() {
		return path;
	}

	/**
	 * Return admin pages, displayed in the administration panel.
	 * 
	 * @return admin pages.
	 */
	public List<AdminPageDTO> getAdminPages() {
		return this.adminPages;
	}

	/**
	 * Add a job to the list of jobs
	 * 
	 * @param job
	 *            the job to add
	 * @param trigger
	 *            the trigger
	 */
	protected void registerJob(Class<? extends Job> job, Trigger trigger) {
		jobs.put(job, trigger);
	}

	/**
	 * Returns the controller list
	 * 
	 * @return the resources list
	 */
	public List<Class<? extends AbstractController>> getControllers() {
		return controllers;
	}

	/**
	 * The methods returns the models
	 * 
	 * @return the method returns the model
	 */
	public List<Class<? extends AbstractModel>> getModels() {
		return models;
	}

	/**
	 * The method returns the list of job with there associated trigger
	 * 
	 * @return the map of job and trigger
	 */
	public Map<Class<? extends Job>, Trigger> getJobs() {
		return jobs;
	}

	protected void configure() {
		// do nothing
		bindListener(Matchers.any(), new TypeListener() {
			public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
				typeEncounter.register(new InjectionListener<I>() {
					public void afterInjection(Object i) {
						Object m = (Object) i;

						// test if method has a post construct annotation
						for (Method method : m.getClass().getMethods()) {
							if (method.isAnnotationPresent(PostConstruct.class)) {
								log.trace("Exectute post construct method on class {}-> method {}", m.getClass(), method.getName());
								try {
									method.invoke(m);
								} catch (IllegalArgumentException e) {
									log.debug("Cannot execute post construct method");
								} catch (IllegalAccessException e) {
									log.debug("Cannot access method {}", method);
								} catch (InvocationTargetException e) {
									log.debug("Cannot access method {}", method);
								}
							}
						}
					}
				});
			}
		});
		configurePlugin();
	}

	public abstract void configurePlugin();

	/**
	 * Register the migration script
	 * 
	 * @param migration
	 */
	protected void registerMigration(Migration migration) {
		migrations.add(migration);
	}

	/**
	 * Return all the migration script
	 * 
	 * @return a set of Migration
	 */
	public SortedSet<Migration> getMigrations() {
		return migrations;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return manifest.name;
	}

	/**
	 * Set the plugin manifest
	 * 
	 * @param m
	 *            the manifest to set
	 */
	public void setManifest(Descriptor m) {
		manifest = m;

	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

}
