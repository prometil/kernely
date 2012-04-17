/**
 * 
 */
package org.kernely.core.template.helpers;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.AbstractConfiguration;
import org.kernely.core.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import soba.SobaEngine;
import soba.context.Context;
import soba.extension.Extension;
import soba.node.ExtensionNode;

/**
 * @author g.breton
 * 
 */
public class SobaI18n extends Extension {

	private static final Logger log = LoggerFactory.getLogger(SobaI18n.class);

	@Inject
	private AbstractConfiguration configuration;

	private Messages messages;

	/**
	 * Creates the soba extension
	 */
	public SobaI18n() {
		super("i18n");

	}

	@PostConstruct
	public void construct() {
	
		String lang = configuration.getString("locale.lang");
		String country = configuration.getString("locale.country");
		List<String> names = new ArrayList<String>();
		names.add("core");
		names.add("stream");
		names.add("holiday");
		names.add("project");
		names.add("timesheet");
		names.add("invoice");
		Locale locale = new Locale(lang, country);
		messages = new Messages(locale, names);
	}

	@Override
	public void execute(ExtensionNode node, SobaEngine engine, Writer writer,
			Context context) {
		if (node.parameters().size() > 0) {
			String key = (String) node.parameters().head();
			try {
				log.debug("i18n value for {} is {}", key,messages.getKey(key));
				writer.write(messages.getKey(key));
			} catch (IOException e) {
				log.error("Cannot write on writer", e);
			}
		}

	}

}
