package org.kernely.timesheet.extender;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.model.User;
import org.kernely.extension.Extender;
import org.kernely.timesheet.dto.TimeSheetDayAmountDTO;
import org.kernely.timesheet.service.TimeSheetService;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ChargedDaysExtender extends Extender{
	
	@Inject
	protected Provider<EntityManager> em;
	
	@Inject
	protected TimeSheetService timeSheetService; 

	@Override
	public HashMap<String, Object> call(HashMap<String, Object> params) {
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username = :username");
		query.setParameter("username", SecurityUtils.getSubject().getPrincipal());
		User user = (User)query.getSingleResult();
		Date date1 = (Date)params.get("start");
		Date date2 = (Date)params.get("end");
		
		List<TimeSheetDayAmountDTO> dayAmounts = timeSheetService.getTimeSheetDayAmountForUserBetweenDates(date1, date2, user.getId());
		HashMap<String, Object> result = new HashMap<String, Object>();
		// Retrieve all days non available in order to disable them in the UI
		HashMap<Date, Float> newHashMap = Maps.newHashMap();
		for(TimeSheetDayAmountDTO tsda : dayAmounts){
			newHashMap.put(tsda.day, tsda.amount);
		}
		result.put("dates", newHashMap);
		
		return result;
	}

	@Override
	public String getExtensionPointName() {
		return "timesheet";
	}

}
