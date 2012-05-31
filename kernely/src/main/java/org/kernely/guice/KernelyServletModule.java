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
package org.kernely.guice;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.kernely.controller.AbstractController;
import org.kernely.menu.MenuItem;
import org.kernely.menu.MenuManager;
import org.kernely.plugin.AbstractPlugin;
import org.kernely.plugin.PluginManager;
import org.kernely.resource.ResourceLocator;
import org.kernely.servlet.MediaServlet;
import org.kernely.template.SobaTemplateRenderer;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * @author g.breton
 * 
 */
public class KernelyServletModule extends JerseyServletModule {

	private static Logger log = LoggerFactory.getLogger(KernelyServletModule.class);

	/**
	 * Bind the servlet of the application
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configureServlets() {

		MenuManager menuManager = new MenuManager();

		// Bind all Jersey resources detected in plugins
		for (AbstractPlugin plugin : PluginManager.getPlugins()) {
			for (Class<? extends AbstractController> controllerClass : plugin.getControllers()) {
				log.debug("Register controller {}", controllerClass);
				bind(controllerClass);

			}
			for (MenuItem item : plugin.getMenuItems()) {
				menuManager.add(plugin.getName(), item);
			}
		}
		CombinedConfiguration configuration = PluginManager.getConfiguration();
		bind(AbstractConfiguration.class).toInstance(configuration);
		bind(ResourceLocator.class);
		bind(PluginManager.class).toInstance(PluginManager.getInstance());
		bind(MenuManager.class).toInstance(menuManager);
		bind(SobaTemplateRenderer.class);

		// persistence
		Iterator<String> keys = configuration.getKeys("hibernate");
		Properties properties = new Properties();
		while (keys.hasNext()) {
			String key = keys.next();
			properties.put(key, configuration.getProperty(key));
		}

		// the jpa persiste module
		JpaPersistModule module = new JpaPersistModule("kernelyUnit").properties(properties);
		install(module);
		filter("/*").through(PersistFilter.class);

		// bind the scheduler factory
		bind(SchedulerFactory.class).to(StdSchedulerFactory.class);

		// Allows to retrieve resources .js, .css, .png
		bind(DefaultServlet.class).in(Singleton.class);
		bind(MediaServlet.class).in(Singleton.class);

		// serve resource type
		serve("*.ico").with(MediaServlet.class);
		serve("*.js").with(MediaServlet.class);
		serve("*.css").with(MediaServlet.class);
		serve("*.png").with(MediaServlet.class);
		serve("*.jpg").with(MediaServlet.class);
		serve("*.gif").with(MediaServlet.class);
		serve("/*").with(GuiceContainer.class);

		bindListener(Matchers.any(), new TypeListener() {
			public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
				Class<? super I> type = typeLiteral.getRawType();

				for (Method method : type.getMethods()) {
					if (method.isAnnotationPresent(PostConstruct.class)) {

						typeEncounter.register(new InjectionListener<I>() {
							public void afterInjection(Object i) {
								Object m = (Object) i;
								// test if method has a post construct
								// annotation
								for (Method method : m.getClass().getMethods()) {

									if (method.isAnnotationPresent(PostConstruct.class)) {

										log.trace("Exectute post construct method on class {}-> method {}", m.getClass(), method.getName());
										try {
											method.invoke(m);
										} catch (IllegalArgumentException e) {
											log.debug("Cannot execute post construct method");
										} catch (IllegalAccessException e) {
											log.debug("Cannot access method {}", method);
										} catch (InvocationTargetException e) {
											log.debug("Cannot access method {}", method);
										}
									}
								}
							}
						});

					}
				}

			}
		});

	}
}
