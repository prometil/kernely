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
package org.kernely.holiday;

import java.util.List;

import org.joda.time.DateTime;
import org.kernely.core.event.UserLockedEvent;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.service.HolidayRequestService;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

/**
 * This class is used to listen event bus and add balances when a new user is added.
 */
public class HolidayUserEventHandler {
	

	@Inject
	private HolidayRequestService requestService;
	
	/**
	 * Detect the lock event of an user and deny all request of this user
	 * @param event
	 * 			  The event, containing user data : id, username, lock or unlock...
	 */
	@Subscribe
	public void onUserLocked(UserLockedEvent event){
		List<HolidayRequestDTO> dtos = requestService.getRequestAfterDateWithStatus(DateTime.now().toDate(), event.getUser(), HolidayRequest.ACCEPTED_STATUS, HolidayRequest.PENDING_STATUS);
		if(event.isLocked()){
			for(HolidayRequestDTO dto : dtos){
				requestService.denyRequest(dto.id);
			}
		}
	}
}
