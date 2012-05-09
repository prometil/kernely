/**
 * 
 */
package org.kernely.menu;

import java.util.HashSet;
import java.util.Set;

import scala.actors.threadpool.Arrays;

public class MenuItem {
	
	
	public String nameKey;
	public String link;
	public Set<String> rights = new HashSet<String>();

	/**
	 * Create a menu item with no roles
	 * @param nameKey the name key for i18n
	 * @param link 
	 */
	public MenuItem(String nameKey, String link) {
		this.nameKey = nameKey;
		this.link = link;
	}

	@SuppressWarnings("unchecked")
	public MenuItem(String nameKey, String link, String[] rights) {
		this.nameKey = nameKey;
		this.link = link;
		this.rights.addAll(Arrays.asList(rights));
	}
}