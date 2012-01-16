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

import org.kernely.core.event.UserCreationEvent;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.service.HolidayBalanceService;
import org.kernely.holiday.service.HolidayService;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

/**
 * This class is used to listen event bus and add balances when a new user is added.
 */
public class HolidayUserEventHandler {
	
	@Inject
	private HolidayBalanceService balanceService;
	
	@Inject
	private HolidayService holidayTypeService;

	/**
	 * Detect the creation of an user and create a balance for each holiday type which exists.
	 * 
	 * @param e
	 *            The event, containing user data : id and username...
	 */
	@Subscribe
	public void onUserCreation(UserCreationEvent event) {
		long userId = event.getId();

		List<HolidayDTO> holidayTypes = holidayTypeService.getAllHoliday();
		
		// Create a balance associated to this user for each holiday type.
		for (HolidayDTO type : holidayTypes){
			balanceService.createHolidayBalance(userId, type.id);
		}
	}
}
