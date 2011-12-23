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

import org.kernely.holiday.migrations.Migration01;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.holiday.controller.HolidayAdminController;
import org.kernely.holiday.controller.HolidayRequestController;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.holiday.service.HolidayService;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

public class HolidayPlugin extends AbstractPlugin {

	@Inject
	EventBus eventBus;
	

	/**
	 * Default constructor
	 */
	public HolidayPlugin(){
		super("Holiday", "/holiday");
		registerController(HolidayAdminController.class);
		registerController(HolidayRequestController.class);
		registerModel(HolidayType.class);
		registerModel(HolidayBalance.class);
		registerModel(HolidayRequest.class);
		registerModel(HolidayRequestDetail.class);
		registerAdminPage("Holiday admin", "/admin/holiday");
		registerMigration(new Migration01());
	}
	
	@Override
	public void start(){

	}
	
	@Override
	protected void configure() {
		bind(HolidayService.class);
		bind(HolidayRequestService.class);
	}

}
