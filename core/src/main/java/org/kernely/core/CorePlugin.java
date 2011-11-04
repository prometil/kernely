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

import org.kernely.core.hibernate.EntityManagerProvider;
import org.kernely.core.hibernate.HibernateUtil;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.core.plugin.PluginsLoader;
import org.kernely.core.resources.MainController;
import org.kernely.core.service.mail.MailService;
import org.kernely.core.service.mail.Mailer;
import org.kernely.core.template.TemplateRenderer;
import org.kernely.user.model.GroupModel;
import org.kernely.user.model.PermissionModel;
import org.kernely.user.model.RoleModel;
import org.kernely.user.model.UserModel;
import org.kernely.user.resources.GroupController;
import org.kernely.user.resources.UserController;

import com.google.common.eventbus.EventBus;
import com.google.inject.Singleton;
import com.google.inject.servlet.RequestScoped;

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
		registerModel(UserModel.class);
		registerModel(RoleModel.class);
		registerModel(PermissionModel.class);
		registerModel(GroupModel.class);
	}

	@Override
	protected void configure() {
		bind(PluginsLoader.class);
		bind(TemplateRenderer.class);
		bind(Mailer.class).to(MailService.class);
		bind(EntityManagerProvider.class).to(HibernateUtil.class);
		bind(SimpleTemplateEngine.class);
		bind(EventBus.class).in(Singleton.class);
	}
	
}
