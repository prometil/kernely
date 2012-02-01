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
package org.kernely.core.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kernely.core.controller.AbstractController;
import org.kernely.core.dto.AdminPageDTO;
import org.kernely.core.hibernate.AbstractModel;
import org.kernely.core.migrations.migrator.Migration;
import org.quartz.Job;
import org.quartz.Trigger;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * The abstract class for a plugin
 * 
 * @author b.grandperret
 * 
 */
public abstract class AbstractPlugin extends AbstractModule {

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
	private List<String> name;

	// the path of the plugin
	private List<String> path;

	/**
	 * the constructor
	 * 
	 * @param pName
	 * @param pPath
	 */
	public AbstractPlugin() {
		name = new ArrayList<String>();
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
		this.adminPages.add(new AdminPageDTO(name, pathToAdmin));
	}

	/**
	 * Register a new model.
	 * @param model
	 */
	protected void registerModel(Class<? extends AbstractModel> model) {
		models.add(model);

	}

	/**
	 * register a new controller
	 * @param controller
	 */
	protected void registerController(Class<? extends AbstractController> controller) {
		controllers.add(controller);
	}
	
	/**
	 * Register a new path
	 * @param String path
	 */
	protected void registerPath(String path){
		this.path.add(path);
	}
	
	/**
	 * Register a new name
	 * @param String name
	 */
	protected void registerName(String name){
		this.name.add(name);
	}
	

	/**
	 * return the module
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
	public List<String> getName() {
		return name;
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

	@Override
	protected void configure() {
		// do nothing
	}

	/**
	 * Register the migration script
	 * @param migration
	 */
	protected void registerMigration(Migration migration) {
		migrations.add(migration);
	}

	/**
	 * Return all the migration script
	 * @return a set of Migration
	 */
	public SortedSet<Migration> getMigrations() {
		return migrations;
	}

}
