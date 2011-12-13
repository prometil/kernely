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
package org.kernely.core.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class handles all the message from all the plugin
 * 
 * @author g.breton
 * 
 */
public class Messages {

	List<ResourceBundle> bundles = new ArrayList<ResourceBundle>();

	public Messages(Locale locale, List<String> names) {
		for(String name: names){
			ResourceBundle bundle = ResourceBundle.getBundle("messages/"+name, locale);
			addBundle(bundle);
		}
	}

	private void addBundle(ResourceBundle bundle) {
		bundles.add(bundle);
	}

	public String getKey(String key) {
		for (ResourceBundle bundle : bundles) {
			if (bundle.containsKey(key)) {
				return bundle.getString(key);
			}
		}
		return "<key not found>";
	}
	/**
	 * 
	 * @param key
	 * @param parameters
	 * @return
	 */
	public String getKey (String key, Object ... parameters){
		return getKey(key);
	}
	
}
