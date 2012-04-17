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
package org.kernely.stream;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.stream.migrations.Migration01;
import org.kernely.stream.controller.StreamAdminController;
import org.kernely.stream.controller.StreamController;
import org.kernely.stream.model.Message;
import org.kernely.stream.model.Stream;
import org.kernely.stream.service.StreamService;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * The user plugin
 * 
 */
public class StreamPlugin extends AbstractPlugin {

	public static final String NAME = "stream";
	
	@Inject
	private EventBus eventBus;
	
	@Inject
	private UserEventHandler userEventHandler;

	/**
	 * Default constructor
	 */
	public StreamPlugin() {
		super();
		registerPath("/streams");
		registerName(NAME);
		registerController(StreamController.class);
		registerController(StreamAdminController.class);
		registerModel(Message.class);
		registerModel(Stream.class);
		registerAdminPage("Stream admin", "/admin/streams/main");
		registerMigration(new Migration01());

	}

	@Override
	public void start() {
		
		eventBus.register(userEventHandler);
	}

	/**
	 * Configure the plugin
	 */
	@Override
	public void configurePlugin() {
		bind(StreamService.class);
		bind(UserEventHandler.class);
	}
}
