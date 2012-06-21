/**
 * 
 */
package org.kernely.holiday.extender;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.configuration.AbstractConfiguration;
import org.joda.time.DateTime;
import org.kernely.extension.Extender;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This extender returns a list of invalid date
 */
public class NotWorkingDaysExtender extends Extender {
	
	@Inject
	AbstractConfiguration configuration;
	
	@Inject
	protected Provider<EntityManager> em;
	
	/**
	 * The method wait for two parameters
	 * <ul>
	 * 	<li>start : inclusive date to look for<li>
	 * 	</li>end : exclused date to look for</li>
	 * </ul>
	 */
	@Override
	public HashMap<String, Object> call(HashMap<String, Object> params) {
		
		DateTime date1 = new DateTime((Date)params.get("start"));
		DateTime date2 = new DateTime((Date)params.get("end"));

		// Get non working days (from 1: Monday to 7: Sunday)
		String[] notWorkingDays = configuration.getStringArray("notWorkingDays");
		Set<Integer> notWorkingDaysSet = new HashSet<Integer>();
		for (String day : notWorkingDays){
			notWorkingDaysSet.add(new Integer(day));
		}
		
		// Retrieve all days non available in order to disable them in the UI
		HashMap<Date, Float> newHashMap = Maps.newHashMap();
		for (DateTime date = date1; date.isBefore(date2); date = date.plusDays(1)) {
			int dayOfWeek = date.getDayOfWeek();
			if (notWorkingDaysSet.contains(dayOfWeek)){
				newHashMap.put(date.toDate(), 1F);
			}
		}
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("dates", newHashMap);

		return result;
	}

	@Override
	public String getExtensionPointName() {
		return "date";
	}

}
