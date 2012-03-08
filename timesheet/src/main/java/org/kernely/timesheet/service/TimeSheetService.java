package org.kernely.timesheet.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.joda.time.DateTime;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.kernely.timesheet.dto.TimeSheetCalendarDTO;
import org.kernely.timesheet.dto.TimeSheetCreationRequestDTO;
import org.kernely.timesheet.dto.TimeSheetDTO;
import org.kernely.timesheet.model.TimeSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for organization pages
 * 
 */
@Singleton
public class TimeSheetService extends AbstractService {

	@Inject
	UserService userService;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Gets the lists of all timesheets contained in the database.
	 * 
	 * @return the list of all timesheets contained in the database.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public List<TimeSheetDTO> getAllTimeSheets() {
		Query query = em.get().createQuery("SELECT t FROM TimeSheet t");
		List<TimeSheet> collection = (List<TimeSheet>) query.getResultList();
		List<TimeSheetDTO> dtos = new ArrayList<TimeSheetDTO>();
		for (TimeSheet timeSheet : collection) {
			dtos.add(new TimeSheetDTO(timeSheet));
		}
		return dtos;
	}

	/**
	 * Create a timesheet
	 * 
	 * @return the dto of the timesheet created
	 */
	@Transactional
	public TimeSheetDTO createOrUpdateTimeSheet(TimeSheetCreationRequestDTO request) {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot be null ");
		}

		TimeSheet timeSheet;

		long id = request.id;
		if (id == 0) {
			// Create a new type
			timeSheet = new TimeSheet();
		} else {
			// Type is already in database
			timeSheet = em.get().find(TimeSheet.class, id);
		}

		User user = em.get().find(User.class, request.userId);

		timeSheet.setBeginDate(request.begin);
		timeSheet.setEndDate(request.end);
		timeSheet.setFeesStatus(request.feesStatus);
		timeSheet.setStatus(request.status);
		timeSheet.setUser(user);

		if (id == 0) {
			// Create a new time sheet
			em.get().persist(timeSheet);
			log.debug("TimeSheetService: new timesheet created from {} to {}", request.begin, request.end);
		} else {
			// Update case
			em.get().merge(timeSheet);
			log.debug("TimeSheetService: timesheet from {} to {} updated", request.begin, request.end);
		}

		return new TimeSheetDTO(timeSheet);
	}

	/**
	 * Return the timesheet corresponding to the week and the user.
	 * 
	 * @param week
	 *            The week (from 1 to 52) in the year.
	 * @param year
	 *            The year.
	 * @param userIdThe
	 *            id of the user.
	 * @parma withCreation If true, will create the timesheet if it does not exists, and return it.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public TimeSheetDTO getTimeSheet(int week, int year, long userId, boolean withCreation) {
		Query query = em.get().createQuery("SELECT t FROM TimeSheet t WHERE user = :user");

		User user = em.get().find(User.class, userId);

		query.setParameter("user", user);
		List<TimeSheet> timeSheets;
		timeSheets = (List<TimeSheet>) query.getResultList();
		DateTime weekDate = new DateTime().withWeekyear(year).withWeekOfWeekyear(week);
		DateTime firstDay = weekDate.withDayOfWeek(1);
		DateTime lastDay = weekDate.withDayOfWeek(7);

		for (TimeSheet sheet : timeSheets) {
			if (firstDay.isEqual(sheet.getBeginDate().getTime()) && lastDay.isEqual(sheet.getEndDate().getTime())) {
				return new TimeSheetDTO(sheet);
			}
		}

		// Create the time sheet if not founded.
		TimeSheet timeSheet = new TimeSheet(firstDay.toDate(), lastDay.toDate(), TimeSheet.TIMESHEET_PENDING, TimeSheet.FEES_TO_VALIDATE, user);

		em.get().persist(timeSheet);
		
		return new TimeSheetDTO(timeSheet);
	}

	/**
	 * Return the timesheet corresponding to the week and the user.
	 * 
	 * @param week
	 *            The week (from 1 to 52) in the year.
	 * @param year
	 *            The year.
	 * @param userIdThe
	 *            id of the user.
	 * @parma withCreation If true, will create the timesheet if it does not exists, and return it.
	 */
	public TimeSheetCalendarDTO getTimeSheetCalendar(int week, int year, long userId, boolean withCreation) {
		TimeSheetDTO timeSheet = this.getTimeSheet(week, year, userId, withCreation);

		List<Date> dates = new ArrayList<Date>();
		List<String> stringDates = new ArrayList<String>();

		for (int i = 0; i <= 6; i++) {
			dates.add(new DateTime(timeSheet.begin).plusDays(i).toDate());
			stringDates.add(new DateTime(timeSheet.begin).plusDays(i).toString("MM/dd/yy"));
		}
		
		return new TimeSheetCalendarDTO(timeSheet, dates, stringDates);

	}
}
