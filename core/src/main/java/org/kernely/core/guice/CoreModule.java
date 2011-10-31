/**
 * 
 */
package org.kernely.core.guice;

import groovy.text.SimpleTemplateEngine;

import org.kernely.core.hibernate.HibernateUtil;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.service.mail.MailService;
import org.kernely.core.template.TemplateRenderer;

import com.google.inject.AbstractModule;


/**
 * The core module
 * @author g.breton
 *
 */
public class CoreModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(PluginsLoader.class);
		bind(TemplateRenderer.class);
		bind(MailService.class);
		bind(HibernateUtil.class);
		bind(SimpleTemplateEngine.class);
	}
}
