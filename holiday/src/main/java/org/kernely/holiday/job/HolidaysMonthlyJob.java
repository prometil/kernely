package org.kernely.holiday.job;

import org.kernely.holiday.service.HolidayBalanceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * A job executed monthly, which compute holidays.
 */
public class HolidaysMonthlyJob implements Job {
	private static Logger log = LoggerFactory.getLogger(HolidaysMonthlyJob.class);

	@Inject
	private HolidayBalanceService balanceService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("/========================================/");
		log.debug("HolidayMounthlyJob: Computing holidays...");
		log.debug("/========================================/");
		balanceService.computeHolidays();
		log.debug("/========================================/");
		log.debug("HolidayMounthlyJob: Computing holidays done.");
		log.debug("/========================================/");
	}
}
