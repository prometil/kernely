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
package org.kernely.stream;

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.stream.model.StreamMessage;
import org.kernely.stream.resources.StreamResource;
import org.kernely.stream.service.StreamService;
/**
 * The user plugin
 *
 */
public class StreamPlugin  extends AbstractPlugin {

	/**
	 * Default constructor
	 */
	public StreamPlugin() {
		super("Stream", "/streams");
		registerController(StreamResource.class);
		registerModel(StreamMessage.class);

	}
	
	@Override
	protected void configure() {
		bind(StreamService.class);
		bind(UserEvenHandler.class);
	}
}
