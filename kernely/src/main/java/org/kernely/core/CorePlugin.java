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
package org.kernely.core;

import groovy.text.SimpleTemplateEngine;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.kernely.core.controller.AdminController;
import org.kernely.core.controller.GroupAdminController;
import org.kernely.core.controller.GroupController;
import org.kernely.core.controller.MainController;
import org.kernely.core.controller.ManagerAdminController;
import org.kernely.core.controller.RoleController;
import org.kernely.core.controller.UserAdminController;
import org.kernely.core.controller.UserController;
import org.kernely.core.migrations.Migration01;
import org.kernely.core.model.Group;
import org.kernely.core.model.Mail;
import org.kernely.core.model.Permission;
import org.kernely.core.model.Role;
import org.kernely.core.model.User;
import org.kernely.core.model.UserDetails;
import org.kernely.plugin.AbstractPlugin;
import org.kernely.plugin.Descriptor;
import org.kernely.service.mail.MailJob;
import org.kernely.service.mail.MailService;
import org.kernely.service.mail.Mailer;
import org.kernely.template.TemplateRenderer;
import org.kernely.template.helpers.SobaI18n;
import org.quartz.DateBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.impl.StdSchedulerFactory;

import soba.SobaEngine;

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CorePlugin() {
	
		super();
		Descriptor coreManifest = new Descriptor();
		coreManifest.name = "core";
		coreManifest.version = "0.1";
		this.setManifest(coreManifest);
		
		registerPath(null);
		registerName(NAME);
		registerController(MainController.class);
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
		registerModel(Mail.class);
		registerMigration(new Migration01());
		
		 // create the Mail schedule, run every 5 minutes
        ScheduleBuilder mailScheduleBuilder = SimpleScheduleBuilder.
                simpleSchedule().
                withIntervalInMinutes(5).
                repeatForever();
 
        // Create the Mail trigger
        Trigger mailTrigger = TriggerBuilder.
                newTrigger().
                withSchedule(mailScheduleBuilder).
                startAt(DateBuilder.futureDate(1, IntervalUnit.MINUTE)).build();
        
        registerJob(MailJob.class, mailTrigger);
	}
	/**
	 * Return the scheduler of the plugin 
	 * @return Scheduler
	 */
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
	/**
	 * Configure the plugin
	 */
	@Override
	public void configurePlugin() {
		bind(TemplateRenderer.class);
		bind(Mailer.class).to(MailService.class);
		bind(SimpleTemplateEngine.class);
		bind(EventBus.class).in(Singleton.class);
		bind(SobaEngine.class).in(Singleton.class);
		bind(SobaI18n.class).in(Singleton.class);
		bind(PasswordService.class).to(DefaultPasswordService.class).in(Singleton.class);
	}
	public Object getVersion() {
		return manifest.version;
	}
	
	

}
