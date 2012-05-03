/**
 * 
 */
package org.kernely.plugin;

/**
 * A plugin manifest.
 */
public class Manifest {
	public String name = "";
	public String version = "";
	public String description = "";
	public String author = "";
	public String pluginClass = "";
	public String url = "";

	@Override
	public String toString() {
		return name + " (" + version + ") : " + description;
	}

}
