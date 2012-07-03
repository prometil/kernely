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
		log.debug("HolidayDailyJob: Remove days in balances...");
		balanceService.removePastHolidays();
		log.debug("HolidayDailyJob: Remove days in balances done.");
		log.debug("HolidayDailyJob: Recall managers...");
		requestService.sendRecallToManagers();
		log.debug("HolidayDailyJob: Recall managers done.");
	}
}
