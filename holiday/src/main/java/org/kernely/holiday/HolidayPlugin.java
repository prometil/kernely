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

import org.joda.time.DateTime;
import org.kernely.core.plugin.AbstractPlugin;
import org.kernely.holiday.controller.HolidayAdminController;
import org.kernely.holiday.controller.HolidayMainController;
import org.kernely.holiday.controller.HolidayManagerRequestController;
import org.kernely.holiday.controller.HolidayManagerUserController;
import org.kernely.holiday.controller.HolidayRequestController;
import org.kernely.holiday.controller.HolidayUserViewController;
import org.kernely.holiday.job.HolidaysDailyJob;
import org.kernely.holiday.job.HolidaysMonthlyJob;
import org.kernely.holiday.migrations.Migration01;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.service.HolidayManagerUserService;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.holiday.service.HolidayService;
import org.quartz.CronScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.DateBuilder.IntervalUnit;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

/**
 * The Plugin for holiday
 */
public class HolidayPlugin extends AbstractPlugin {

	public static final String NAME = "holiday";

	@Inject
	private EventBus eventBus;
	
	@Inject
	private HolidayUserEventHandler userEventHandler;
	
	/**
	 * Default constructor
	 */
	@SuppressWarnings({ "unchecked" })
	public HolidayPlugin(){
		super(NAME, "/holiday");
		registerController(HolidayMainController.class);
		registerController(HolidayAdminController.class);
		registerController(HolidayRequestController.class);
		registerController(HolidayManagerUserController.class);
		registerController(HolidayManagerRequestController.class);
		registerController(HolidayUserViewController.class);
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
        
        registerJob(HolidaysMonthlyJob.class, holidaysTrigger);

		 // create the Mail schedule, run every 5 minutes
        ScheduleBuilder dailySchedule = SimpleScheduleBuilder.
                simpleSchedule().
                withIntervalInHours(24).
                repeatForever();
 
        // Create the Mail trigger
        Trigger mailTrigger = TriggerBuilder.
                newTrigger().
                withSchedule(dailySchedule).
              startAt(DateTime.now().toDateMidnight().toDate()).build();
        
        registerJob(HolidaysDailyJob.class, mailTrigger);

        
	}
	
	@Override
	public void start() {
		eventBus.register(userEventHandler);
	}
	
	@Override
	protected void configure() {
		bind(HolidayService.class);
		bind(HolidayRequestService.class);
		bind(HolidayManagerUserService.class);
	}

}
