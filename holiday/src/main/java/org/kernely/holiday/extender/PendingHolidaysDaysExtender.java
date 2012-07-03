/**
 * 
 */
package org.kernely.holiday.extender;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;
import org.kernely.core.model.User;
import org.kernely.extension.Extender;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.service.HolidayRequestService;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * This extender returns a list of invalid date
 */
public class PendingHolidaysDaysExtender extends Extender {

	@Inject
	AbstractConfiguration configuration;

	@Inject
	protected Provider<EntityManager> em;

	@Inject
	private HolidayRequestService requestService;

	/**
	 * The method wait for two parameters
	 * <ul>
	 * <li>start : inclusive date to look for
	 * <li>
	 * </li>end : exclused date to look for</li>
	 * </ul>
	 */
	@Override
	public HashMap<String, Object> call(HashMap<String, Object> params) {

		DateTime date1 = new DateTime((Date) params.get("start"));
		DateTime date2 = new DateTime((Date) params.get("end"));

		Query query = em.get().createQuery("SELECT e FROM User e WHERE username = :username");
		query.setParameter("username", SecurityUtils.getSubject().getPrincipal());
		User user = (User) query.getSingleResult();

		List<HolidayRequestDTO> requests = requestService.getRequestBetweenDatesWithStatus(date1.toDate(), date2.toDate(), user,
				HolidayRequest.PENDING_STATUS);

		Set<Date> pendingDates = new HashSet<Date>();

		for (HolidayRequestDTO request : requests) {
			for (HolidayDetailDTO detail : request.details) {
				if (detail.am || detail.pm) {
					pendingDates.add(detail.day);
				}
			}
		}

		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("dates", pendingDates);

		return result;
	}

	@Override
	public String getExtensionPointName() {
		return "pending_holidays_dates";
	}

}
