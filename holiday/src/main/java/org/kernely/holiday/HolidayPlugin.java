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

import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.holiday.controller.HolidayAdminController;
import org.kernely.holiday.controller.HolidayRequestController;
import org.kernely.holiday.job.HolidaysJob;
import org.kernely.holiday.migrations.Migration01;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.holiday.service.HolidayService;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.ScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * The Plugin for holiday
 * @author b.grandperret
 *
 */
public class HolidayPlugin extends AbstractPlugin {

	/**
	 * Default constructor
	 */
	@SuppressWarnings({ "unchecked" })
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
		
		// Register job
		// Create the holidays computing schedule with a cron expression :
		// 0  : at the second 0
		// 0  : at the minute 0
		// 23 : at 11 p.m
		// L  : the last day of the month
		// *  : every month
		// ?  : the day of the week is not important
		// *  : every year
		
        ScheduleBuilder holidaysSchedule = CronScheduleBuilder.cronSchedule("0 0 23 L * ? *");

        // Create the holidays trigger
        Trigger holidaysTrigger = TriggerBuilder.
                newTrigger().
                withSchedule(holidaysSchedule).
                startAt(DateBuilder.futureDate(15, IntervalUnit.SECOND)).build();
        
        registerJob(HolidaysJob.class, holidaysTrigger);
		
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
