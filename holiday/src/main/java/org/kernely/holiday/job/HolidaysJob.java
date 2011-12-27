package org.kernely.holiday.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.kernely.holiday.dto.HolidayBalanceDTO;
import org.kernely.holiday.service.HolidayBalanceService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class HolidaysJob implements Job {
	private static Logger log = LoggerFactory.getLogger(HolidaysJob.class);

	@Inject
	private HolidayBalanceService balanceService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("Computing holidays...");
		List<HolidayBalanceDTO> allBalances = balanceService.getAllHolidayBalances();
		
	    Date curDate = new Date ();
	    TimeZone zone = TimeZone.getTimeZone("UTC");
	    Calendar calendar = Calendar.getInstance (zone);
	    calendar.setTime (curDate);
	    int curMonth = calendar.get(Calendar.MONTH);

		for (HolidayBalanceDTO balance : allBalances) {
			balanceService.incrementBalance(balance.id);
			
			// Transfer future balance into available balance if needeed
			if (balance.effectiveMonth == curMonth){
				balanceService.transferFutureBalance(balance.id);
			}
		}
		
		log.debug("Computing holidays : end.");
	}
}
