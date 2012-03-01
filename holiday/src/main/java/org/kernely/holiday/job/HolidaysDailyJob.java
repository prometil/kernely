package org.kernely.holiday.job;

import org.kernely.holiday.service.HolidayBalanceService;
import org.kernely.holiday.service.HolidayRequestService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * A job executed daily:
 * - Decrement balances with past holidays
 * - Send a mail to managers which have to accept or deny holiday requests.
 */
public class HolidaysDailyJob implements Job {
	private static Logger log = LoggerFactory.getLogger(HolidaysDailyJob.class);

	@Inject
	private HolidayBalanceService balanceService;
	
	@Inject
	private HolidayRequestService requestService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("/========================================/");
		log.debug("HolidayDailyJob: Decrementing balances...");
		log.debug("/========================================/");
		balanceService.removePastHolidays();
		log.debug("/========================================/");
		log.debug("HolidayDailyJob: Decrementing balance done.");
		log.debug("/========================================/");
		log.debug("HolidayDailyJob: Recall managers...");
		log.debug("/========================================/");
		requestService.sendRecallToManagers();
		log.debug("/========================================/");
		log.debug("HolidayDailyJob: Recall managers done.");
		log.debug("/========================================/");
	}
}
