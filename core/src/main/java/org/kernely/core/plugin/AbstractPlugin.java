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
package org.kernely.core.plugin;

import java.util.ArrayList;
import java.util.List;

import org.kernely.core.hibernate.AbstractEntity;
import org.kernely.core.resources.AbstractController;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public abstract class AbstractPlugin extends AbstractModule {
	
	private List<Class<? extends AbstractController>> controllers;
	
	private List<Class<? extends AbstractEntity>> models;
	
	private String name;
	
	private String path;
	
	private String configurationFilepath;
	
	public AbstractPlugin(String pName, String pPath){
		name = pName;
		path = pPath;
		controllers = new  ArrayList<Class<? extends AbstractController>>();
		models = new ArrayList<Class<? extends AbstractEntity>>();
	}
	
	protected void registerModel(Class<? extends AbstractEntity> model){
		models.add(model);
		
	}
	
	protected void registerController(Class<? extends AbstractController> controller){
		controllers.add(controller);
	}
	
	protected void registerConfigurationPath(String pFilepath){
		configurationFilepath = pFilepath;
	}
	public Module getModule(){
		return this;
	}
	
	
	/**
	 * Returns the name of the plugin
	 * @return the name of the plugin
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Return the plugin path
	 * @return the plugin path
	 */
	public String getPath(){
		return path;
	}
	
	/**
	 * Return the configuration filepath
	 * @return the configuration file path
	 */
	public String getConfigurationFilepath(){
		return configurationFilepath;
	}
	
	/**
	 * Returns the controller list
	 * @return the resources list 
	 */
	public List<Class<? extends AbstractController>> getControllers(){
		return controllers;
	}
	
	/**
	 * The methods returns the models
	 * @return the method returns the model
	 */
	public List<Class<? extends AbstractEntity>> getModels(){
		return models;
	}

	@Override
	protected void configure() {
		//do nothing
	}
	
	
	
	
}
