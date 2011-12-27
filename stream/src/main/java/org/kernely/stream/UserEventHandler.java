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

import org.kernely.core.event.UserCreationEvent;
import org.kernely.stream.model.Stream;
import org.kernely.stream.service.StreamService;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

/**
 * @author g.breton
 */
public class UserEventHandler {

	@Inject
	private StreamService streamService;

	/**
	 * Detect the creation of an user and create his stream.
	 * 
	 * @param e
	 *            The event, containing user data : id and username...
	 */
	@Subscribe
	public void onUserCreation(UserCreationEvent event) {
		streamService.createStream("Stream of " + event.getUsername(), Stream.CATEGORY_USERS);
	}
}
