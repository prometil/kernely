package org.kernely.core.template.helpers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.kernely.core.i18n.Messages;

public class I18n {

	private static Messages messages;
	private Locale locale;

	public I18n(Locale pLocale) {
		List<String> names = new ArrayList<String>();
		names.add("core");
		names.add("stream");
		messages = new Messages(new Locale("fr", "FR"), names);
		locale = pLocale;
	}

	/**
	 * The method
	 * 
	 * @param key
	 *            the key of the message to get
	 * @return
	 */
	public String t(String key) {
		return messages.getKey(key);
	}

	/**
	 * This method returns a key with parameters
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
