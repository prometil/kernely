package org.kernely.timesheet.extender;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.model.User;
import org.kernely.extension.Extender;
import org.kernely.timesheet.dto.TimeSheetDayDTO;
import org.kernely.timesheet.service.TimeSheetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class LockedDaysExtender extends Extender{
	@Inject
	protected Provider<EntityManager> em;
	
	@Inject
	protected TimeSheetService timeSheetService; 
	
	protected final Logger log = LoggerFactory.getLogger(this.getClass());


	@Override
	public HashMap<String, Object> call(HashMap<String, Object> params) {
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username = :username");
		query.setParameter("username", SecurityUtils.getSubject().getPrincipal());
		User user = (User)query.getSingleResult();
		Date date1 = (Date)params.get("start");
		Date date2 = (Date)params.get("end");
		
		log.debug("[LockedDaysExtender] called with params [Start : {}] and [End : {}]", date1, date2);
		List<TimeSheetDayDTO> days = timeSheetService.getTimeSheetDayForUserBetweenDates(date1, date2, user.getId());
		HashMap<String, Object> result = new HashMap<String, Object>();
		boolean locked = false;

		for(TimeSheetDayDTO tsd : days){
			if(tsd.validated){
				locked = true;
				break;
			}
		}
		result.put("locked", locked);
		
		return result;
	}

	@Override
	public String getExtensionPointName() {
		return "timesheet_lockedDays";
	}
}
