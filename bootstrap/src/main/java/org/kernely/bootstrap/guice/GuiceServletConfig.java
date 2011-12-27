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

package org.kernely.bootstrap.guice;

import static org.quartz.JobBuilder.newJob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.CombinedConfiguration;
import org.kernely.core.job.GuiceSchedulerFactory;
import org.kernely.core.plugin.AbstractPlugin;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

/**
 * The Guice servlet container
 * 
 * @author g.breton
 * 
 */
public class GuiceServletConfig extends GuiceServletContextListener {

	public static Logger log = LoggerFactory.getLogger(GuiceServletConfig.class);
	private List<? extends AbstractPlugin> plugins;
	private final CombinedConfiguration combinedConfiguration;

	/**
	 * Constructor.
	 * 
	 * @param plugins
	 *            The list of plugins to configure.
	 */
	public GuiceServletConfig(List<? extends AbstractPlugin> plugins, CombinedConfiguration combinedConfiguration) {
		this.plugins = plugins;
		this.combinedConfiguration = combinedConfiguration;
	}

	/**
	 * Creates a Guice injector with all the plugins modules, binding all jersey
	 * resource detected in plugins.
	 */
	@Override
	protected Injector getInjector() {
		List<Module> list = new ArrayList<Module>();
		for (AbstractPlugin plugin : plugins) {
			Module module = plugin.getModule();
			if (module != null) {
				list.add(module);
			}
		}
		list.add(new KernelyServletModule(plugins, combinedConfiguration));
		list.add(new ServletModule());
		
		//create injector
		Injector injector = Guice.createInjector(list);

		//get all jobs
		Scheduler scheduler = injector.getInstance(Scheduler.class);
		GuiceSchedulerFactory guiceSchedulerFactory = injector.getInstance(GuiceSchedulerFactory.class);
		try {
			scheduler.setJobFactory(guiceSchedulerFactory);

			for (AbstractPlugin plugin : plugins) {
				for (Map.Entry<Class<? extends Job>, Trigger> entry : plugin.getJobs().entrySet()) {
					JobDetail job = newJob(entry.getKey()).build();
					scheduler.scheduleJob(job, entry.getValue());
				}
			}
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("Scheduler exception {}", e);
		}
		return injector;
	}
}
