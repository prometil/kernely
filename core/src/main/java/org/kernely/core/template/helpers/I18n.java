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
package org.kernely.core.template.helpers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.kernely.core.i18n.Messages;

/**
 * 
 * @author g.breton
 * 
 */
public class I18n {

	private Messages messages;

	// the choosen locale
	private Locale locale;

	/**
	 * Create the I18N with a given locale
	 * 
	 * @param pLocale
	 *            the i18n locale
	 */
	public I18n(Locale pLocale) {
		List<String> names = new ArrayList<String>();
		names.add("core");
		names.add("stream");
		messages = new Messages(pLocale, names);
		locale = pLocale;
	}

	/**
	 * Translates a key to a string
	 * 
	 * @param key
	 *            the key of the message to get
	 * @return
	 */
	public String t(String key) {
		return messages.getKey(key);
	}

	/**
	 * Translate a key to a string with some parameters
	 * 
	 * eg. template = At {2,time,short} on {2,date,long}, we detected
	 * {1,number,integer} spaceships on the planet {0}.
	 * 
	 * @param key
	 *            the keys
	 * @param elements
	 *            the parameters
	 * @return
	 */
	public String t(String key, Object... elements) {
		String value = t(key);

		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(locale);
		formatter.applyPattern(value);
		return formatter.format(elements);
	}
}
