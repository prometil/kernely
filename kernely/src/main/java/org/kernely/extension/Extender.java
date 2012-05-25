/**
 * 
 */
package org.kernely.extension;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.kernely.plugin.PluginManager;


/**
 * Defines a simple extension point
 */
public abstract class Extender {
	


	/**
	 * Calls the extension point
	 * @param parameters the list of names parameters used for the call
	 * @return
	 */
	public abstract HashMap<String, Object> call( HashMap<String, Object> parameters);
	
	
	/**
	 * Returns the name of the extended extension point.
	 * @return the name of the extended extension point.
	 */
	public abstract String getExtensionPointName();
	
	/**
	 * Register the current extender instance in the plugin manager
	 * it shoule be done automatically by when inject through the plugin
	 */
	@PostConstruct
	public void register(){
		System.out.println("post consturx");
		PluginManager.registerExtender(getExtensionPointName(), this);
	}
}
