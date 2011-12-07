/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core;

import groovy.text.SimpleTemplateEngine;

import org.kernely.core.migrations.Migration01;
import org.kernely.core.model.Group;
import org.kernely.core.model.Permission;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.core.model.UserDetails;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.resources.AdminController;
import org.kernely.core.resources.GroupAdminController;
import org.kernely.core.resources.GroupController;
import org.kernely.core.resources.MainController;
import org.kernely.core.resources.ManagerAdminController;
import org.kernely.core.resources.RoleController;
import org.kernely.core.resources.UserAdminController;
import org.kernely.core.resources.UserController;
import org.kernely.core.service.mail.MailService;
import org.kernely.core.service.mail.Mailer;
import org.kernely.core.template.TemplateRenderer;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * The core kernely plugin
 * 
 */
public class CorePlugin extends AbstractPlugin {
	
	public static final String NAME = "core";
	/**
	 * Default constructor
	 */
	public CorePlugin() {
		super("Core", null);
		registerController(MainController.class);
		registerConfigurationPath("core-config.xml");
		registerController(UserController.class);
		registerController(GroupController.class);
		registerController(RoleController.class);
		registerController(AdminController.class);
		registerController(UserAdminController.class);
		registerController(GroupAdminController.class);
		registerController(ManagerAdminController.class);
		registerAdminPage("User admin", "/admin/users");
		registerAdminPage("Group admin", "/admin/groups");
		registerAdminPage("Manager admin", "/admin/manager");
		registerModel(User.class);
		registerModel(Role.class);
		registerModel(Permission.class);
		registerModel(Group.class);
		registerModel(UserDetails.class);
		registerMigration(new Migration01());
	}

	@Override
	protected void configure() {
		bind(PluginsLoader.class);
		bind(TemplateRenderer.class);
		bind(Mailer.class).to(MailService.class);
		bind(SimpleTemplateEngine.class);
		bind(EventBus.class).in(Singleton.class);

	}

	@Provides
	@Singleton
	public Scheduler getScheduler() {
		SchedulerFactory schedFact = new StdSchedulerFactory();
		Scheduler sched = null;
		try {
			sched = schedFact.getScheduler();
			return sched;
		} catch (SchedulerException e) {
			return null;
		}

	}

}
