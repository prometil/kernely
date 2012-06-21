/**
 * 
 */
package org.kernely.holiday.extender;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.shiro.SecurityUtils;
import org.kernely.core.model.User;
import org.kernely.extension.Extender;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.service.HolidayRequestService;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This extender returns a list of invalid date
 */
public class HolidayDateExtender extends Extender {
	
	@Inject
	protected Provider<EntityManager> em;
	
	@Inject
	protected HolidayRequestService holidayRequestService;
	
	
	/**
	 * The method wait for two parameters
	 * <ul>
	 * 	<li>start : inclusive date to look for<li>
	 * 	</li>end : exclused date to look for</li>
	 * </ul>
	 */
	@Override
	public HashMap<String, Object> call(HashMap<String, Object> params) {
		Query query = em.get().createQuery("SELECT e FROM User e WHERE username = :username");
		query.setParameter("username", SecurityUtils.getSubject().getPrincipal());
		User user = (User)query.getSingleResult();
		Date date1 = (Date)params.get("start");
		Date date2 = (Date)params.get("end");
		List<HolidayRequestDTO> currentRequests = holidayRequestService.getRequestBetweenDatesWithStatus(date1, date2, user, HolidayRequest.PENDING_STATUS,
				HolidayRequest.ACCEPTED_STATUS);
		HashMap<String, Object> result = new HashMap<String, Object>();
		// Retrieve all days non available in order to disable them in the UI
		HashMap<Date, Float> newHashMap = Maps.newHashMap();
		for (HolidayRequestDTO req : currentRequests) {
			for(HolidayDetailDTO detail : req.details){
				// If the day is already in the map, it can be at 0.5 or 1. If it is inferior to 1, adds 0.5.
				if (newHashMap.containsKey(detail.day) && newHashMap.get(detail.day) < 1){
					newHashMap.put(detail.day, new Float(newHashMap.get(detail.day) + 0.5));
				} else {
					// Add 0.5 for am and pm.
					Float amount = 0F;
					if (detail.am){
						amount += 0.5F;
					}
					if (detail.pm){
						amount += 0.5F;
					}
					newHashMap.put(detail.day, amount);
				}
			}
			
		}
		result.put("dates", newHashMap);

		return result;
	}

	@Override
	public String getExtensionPointName() {
		return "date";
	}

}
