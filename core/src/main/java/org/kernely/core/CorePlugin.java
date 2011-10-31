package org.kernely.core;

import groovy.text.SimpleTemplateEngine;

import org.kernely.core.hibernate.HibernateUtil;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.resources.MainController;
import org.kernely.core.service.mail.MailService;
import org.kernely.core.template.TemplateRenderer;

/**
 * The core kernely plugin
 * @author yak
 *
 */
public class CorePlugin extends AbstractPlugin {
	/**
	 * Default constructor
	 */
	public CorePlugin() {
		super("Core", "/");
		registerController(MainController.class);
		registerConfigurationPath("core-config.xml");
	}

	@Override
	protected void configure() {
		bind(PluginsLoader.class);
		bind(TemplateRenderer.class);
		bind(MailService.class);
		bind(HibernateUtil.class);
		bind(SimpleTemplateEngine.class);
	}
	
}
