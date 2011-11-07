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

import org.kernely.core.model.Group;
import org.kernely.core.model.Permission;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.resources.GroupController;
import org.kernely.core.resources.MainController;
import org.kernely.core.resources.UserController;
import org.kernely.core.service.mail.MailService;
import org.kernely.core.service.mail.Mailer;
import org.kernely.core.template.TemplateRenderer;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;

/**
 * The core kernely plugin
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
		registerController(UserController.class);
		registerController(GroupController.class);
		registerModel(User.class);
		registerModel(Role.class);
		registerModel(Permission.class);
		registerModel(Group.class);
	}

	@Override
	protected void configure() {
		bind(PluginsLoader.class);
		bind(TemplateRenderer.class);
		bind(Mailer.class).to(MailService.class);
		bind(SimpleTemplateEngine.class);
		bind(EventBus.class).in(Singleton.class);
		
	}
	
}
