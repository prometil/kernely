/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Map.Entry;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.kernely.core.model.User;
import org.kernely.core.service.AbstractService;
import org.kernely.holiday.dto.HolidayBalanceDTO;
import org.kernely.holiday.dto.HolidayDetailDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayBalance;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;

/**
 * The service for Holiday balance pages
 */
@Singleton
public class HolidayBalanceService extends AbstractService {

	private static final float RANGE = 0.0001F;

	private static final int TWELTHS_DAYS = 12;

	private static final float HALF_DAY = 0.5F;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private HolidayRequestService holidayRequestService;

	/**
	 * Gets the lists of all balances contained in the database.
	 * 
	 * @return the list of all balances contained in the database.
	 */
	@Transactional
	@SuppressWarnings("unchecked")
	public List<HolidayBalanceDTO> getAllHolidayBalances() {

		Query query = em.get().createQuery("SELECT e FROM HolidayBalance e");
		List<HolidayBalance> collection = (List<HolidayBalance>) query.getResultList();
		List<HolidayBalanceDTO> dtos = new ArrayList<HolidayBalanceDTO>();
		log.debug("HolidayBalanceService found {} balances", collection.size());
		for (HolidayBalance holiday : collection) {
			dtos.add(new HolidayBalanceDTO(holiday));
		}
		return dtos;
	}

	/**
	 * Get the holiday balance dto with the specified id
	 * 
	 * @param id
	 *            The id of the holiday balance
	 * @return the holiday balance dto of the holiday balance matching the id
	 *         passed in parameter.
	 */
	@Transactional
	public HolidayBalanceDTO getHolidayBalanceDTO(int id) {
		HolidayBalance balance = em.get().find(HolidayBalance.class, id);
		return new HolidayBalanceDTO(balance);
	}

	/**
	 * Create a new Holiday balance in database for the current year. Available
	 * balance are set to 0.
	 * 
	 * @param holidayTypeId
	 *            The holiday type of this balance.
	 * @param userId
	 *            The user associated to this balance.
	 * @return A DTO representing the new balance created
	 */
	public HolidayBalanceDTO createHolidayBalance(int holidayTypeId, long userId) {
		return this.createHolidayBalance(holidayTypeId, userId, DateTime.now().getYear());
	}

	/**
	 * Create a new Holiday balance in database for the specified year.
	 * Available balance are set to 0. If at least one balance exists for this
	 * association type/user, this function creates the balance for the year
	 * immediately following the previous
	 * 
	 * E.G. : When a first balance is created for the year 2000, the next call
	 * of this function with the same association user/type will create a
	 * balance for the year 2001, even if year is not specified
	 * 
	 * @param holidayTypeId
	 *            The holiday type of this balance.
	 * @param userId
	 *            The user associated to this balance.
	 * @param year
	 *            The year associated to the new balance. If 0, this is the
	 *            current year.
	 * @return A DTO representing the new balance created
	 */
	@Transactional
	public HolidayBalanceDTO createHolidayBalance(int holidayTypeId, long userId, int year) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeId + "is not defined.");
		}

		HolidayBalance balance = new HolidayBalance();
		balance.setUser(user);
		balance.setHolidayType(type);
		balance.setAvailableBalance(0);
		balance.setAvailableBalanceUpdated(0);
		DateTimeZone zoneUTC = DateTimeZone.UTC;
		balance.setLastUpdate(new DateTime().withZone(zoneUTC).toDate());

		// In the case where there is an effective month, we set the begin date
		// at the end of the previous balance,
		// and the end date to effective month of the N+1 year
		// If there is no previous, in the case of the hire month, we take the
		// next month as beginning
		HolidayBalanceDTO previous = this.getPreviousBalance(holidayTypeId, userId);
		if (previous == null) {
			// In the case where there is no previous, when
			// it's a new balance
			balance.setBeginDate(this.getNextCompleteMonth(DateTime.now().withYear(year).toDate()));
			// We check when the new balance is created
			// If the current date is after the effectiveness of this type of
			// holiday, end date is set one year later
			// else, end date is the current year, the first of the effective
			// month
			if (type.getEffectiveMonth() == HolidayType.ALL_MONTH) {
				balance.setEndDate(new DateTime().withMonthOfYear(1).withDayOfMonth(1).withYear(year).plusYears(1).toDateMidnight().toDate());
			} else {

				if (DateTime.now().withYear(year).isAfter(
						new DateTime().withDayOfMonth(1).withMonthOfYear(type.getEffectiveMonth()).withYear(year).toDateMidnight())) {
					balance.setEndDate(new DateTime().withMonthOfYear(type.getEffectiveMonth()).withYear(year).plusYears(1).withDayOfMonth(1)
							.toDateMidnight().toDate());
				} else {
					balance.setEndDate(new DateTime().withMonthOfYear(type.getEffectiveMonth()).withDayOfMonth(1).withYear(year).toDateMidnight()
							.toDate());
				}
			}
		} else { // In the case where there is a previous, when this is just the
			// balance for the next period
			balance.setBeginDate(new DateTime(previous.endDate).toDateMidnight().toDate());
			balance.setEndDate(new DateTime(previous.endDate).plusYears(1).toDateMidnight().toDate());
		}

		em.get().persist(balance);
		return new HolidayBalanceDTO(balance);
	}

	/**
	 * Create a new balance for a new user in function of his hire date.
	 * 
	 * @param userId
	 *            Id the the concerned user
	 * @param holidayTypeId
	 *            Id of the new balance's type
	 * @return A DTO representing the new balance created
	 */
	public HolidayBalanceDTO createHolidayBalanceForNewUser(int holidayTypeId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeId + "is not defined.");
		}
		// Verify if a balance has already been created for this user and this
		// type.
		if (getPreviousBalance(holidayTypeId, userId) != null) {
			throw new IllegalArgumentException("The user " + userId + " has already a balance for the type " + holidayTypeId
					+ ". Impossible to create the new balance.");
		}

		HolidayBalance balance = new HolidayBalance();
		balance.setUser(user);
		balance.setHolidayType(type);
		balance.setAvailableBalance(0);
		balance.setAvailableBalanceUpdated(0);
		DateTimeZone zoneUTC = DateTimeZone.UTC;
		balance.setLastUpdate(new DateTime().withZone(zoneUTC).toDate());

		// Retrieve hire date of the user
		DateTime hireDate = new DateTime(user.getUserDetails().getHire());

		balance.setBeginDate(this.getNextCompleteMonth(hireDate.toDate()));
		if (type.getEffectiveMonth() == HolidayType.ALL_MONTH) {
			// We take 1/1/N+1
			balance.setEndDate(new DateTime().withMonthOfYear(1).withDayOfMonth(1).plusYears(1).toDateMidnight().toDate());
		} else {
			// If the hire date is after the effectiveness of this type of
			// holiday, end date is set one year later
			// else, end date is the current year, the first of the effective
			// month
			if (hireDate.isAfter(new DateTime().withDayOfMonth(1).withMonthOfYear(type.getEffectiveMonth()))) {
				balance.setEndDate(new DateTime().withMonthOfYear(type.getEffectiveMonth()).withDayOfMonth(1).plusYears(1).toDateMidnight().toDate());
			} else {
				balance.setEndDate(new DateTime().withMonthOfYear(type.getEffectiveMonth()).withDayOfMonth(1).toDateMidnight().toDate());
			}
		}
		em.get().persist(balance);
		return new HolidayBalanceDTO(balance);
	}

	private Date getNextCompleteMonth(Date d) {
		DateTime dt = new DateTime(d);
		if (dt.getDayOfMonth() != 1) {
			return dt.withDayOfMonth(1).plusMonths(1).toDateMidnight().toDate();
		} else {

			return dt.toDateMidnight().toDate();
		}
	}

	/**
	 * Retrieve the directly previous balance for the association type/user
	 * given.
	 * 
	 * @param holidayTypeId
	 *            Type of the balance concerned
	 * @param userId
	 *            Id of the owner of the balance concerned
	 * @return A DTO representing the previous balance, null if doesn't exist
	 */
	@Transactional
	public HolidayBalanceDTO getPreviousBalance(int holidayTypeId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeId + "is not defined.");
		}

		Query balanceRequest = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type ORDER BY endDate DESC");
		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", type);
		balanceRequest.setMaxResults(1); // We just want the first element
		try {
			return new HolidayBalanceDTO((HolidayBalance) balanceRequest.getSingleResult());
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Get a set of holiday balance, matching a specific user id and a specific
	 * holiday type id. If the balance is associated to a type with possibility
	 * of anticipation, this method will return all old balances and the balance
	 * for the current year. If this is not a type with possibility of
	 * anticipation, it will return all balances except the one for the current
	 * year.
	 * 
	 * @param userId
	 *            The user associated to this balance.
	 * @param holidayTypeId
	 *            The holiday type of this balance.
	 * @return A set of all available balances
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<HolidayBalanceDTO> getHolidayBalancesAvailable(int holidayTypeId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeId + "is not defined.");
		}

		Query balanceRequest;
		if (type.isAnticipated()) {
			balanceRequest = em
					.get()
					.createQuery(
							"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type AND endDate <= :date ORDER BY beginDate ASC");
			balanceRequest.setParameter("date", DateTime.now().plusYears(1).toDateMidnight().toDate());
		} else {
			balanceRequest = em.get().createQuery(
					"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type AND endDate <= :date ORDER BY begin_date ASC");
			balanceRequest.setParameter("date", DateTime.now().toDateMidnight().toDate());
		}

		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", type);
		try {
			List<HolidayBalance> balances = (List<HolidayBalance>) balanceRequest.getResultList();
			Set<HolidayBalanceDTO> balancesDTO = new TreeSet<HolidayBalanceDTO>();
			for (HolidayBalance b : balances) {
				balancesDTO.add(new HolidayBalanceDTO(b));
			}
			return balancesDTO;
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Return the latest balance created.
	 * 
	 * @param holidayTypeId
	 *            Type of the balance needed
	 * @param userId
	 *            Id of the owner of the balance needed
	 * @return A DTO representing the latest balance created
	 */
	@Transactional
	public HolidayBalanceDTO getProcessedBalance(int holidayTypeId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		Query balanceRequest = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type ORDER BY end_date DESC");
		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", type);
		balanceRequest.setMaxResults(1);
		try {
			HolidayBalance balance = (HolidayBalance) balanceRequest.getSingleResult();
			return new HolidayBalanceDTO(balance);
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Increments the balance with the quantity of time corresponding to the
	 * type of holiday. This method increments automatically the most recent
	 * balance of the association user/type. This method increments both
	 * availableBalance and availableBalanceUpdated of the amount of the
	 * Quantity field defined in the type associated * the period unit field
	 * defined in the type associated too.
	 * 
	 * 
	 * @param holidayTypeId
	 *            The id of the type linked to the balance.
	 * @param userId
	 *            Id of the owner of the balance to increment
	 */
	@Transactional
	public void incrementBalance(int holidayTypeId, long userId) {
		HolidayBalanceDTO bDto = getProcessedBalance(holidayTypeId, userId);
		if (bDto == null) {
			throw new IllegalArgumentException("There is no balance associated to the user with the type " + userId
					+ " and the holiday type with id " + holidayTypeId);
		}
		HolidayBalance balance = em.get().find(HolidayBalance.class, bDto.id);

		// Only limited balances are incremented
		if (!balance.getHolidayType().isUnlimited()) {

			// Quantity of time (in days) earned each period
			int quantity = balance.getHolidayType().getQuantity();

			// Adjust quantity: balances contains twelths of days
			// Multiply quantity by the ratio of the period.
			// The period unit is 12 for months, 1 for years (constants in
			// HolidayType class).
			quantity = quantity * balance.getHolidayType().getPeriodUnit();

			// If there is no effective month of the type of holidays, the
			// available balance is incremented, otherwise the future balance is
			// incremented.
			int newBalance = balance.getAvailableBalance() + quantity;
			int newBalanceUpdated = balance.getAvailableBalanceUpdated() + quantity;
			log.debug("Holiday (id:{}) had available balance: {}", balance.getId(), balance.getAvailableBalance());
			log.debug("Holiday (id:{}) incremented by {}", balance.getId(), quantity);
			balance.setAvailableBalance(newBalance);
			balance.setAvailableBalanceUpdated(newBalanceUpdated);
			log.debug("Holiday (id:{}) new balance: {}", balance.getId(), balance.getAvailableBalance());

			DateTimeZone zoneUTC = DateTimeZone.UTC;
			Date today = new DateTime().withZone(zoneUTC).toDate();

			// Actualize date
			balance.setLastUpdate(today);
			em.get().merge(balance);
		}
	}

	/**
	 * Verify if the balance has the amount of days.
	 * 
	 * @param holidayTypeId
	 *            Id of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 * @return True if "days" are available, else false
	 */
	@Transactional
	public boolean hasAvailableDays(int holidayTypeId, long userId, float days) {
		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(holidayTypeId, userId);
		float availDays = 0F;
		for (HolidayBalanceDTO b : balances) {
			availDays += (b.availableBalance * TWELTHS_DAYS);
		}
		return availDays >= (days * TWELTHS_DAYS);
	}

	/**
	 * Remove days or half days to the available balance. Verify if the balance
	 * has the amount of days.
	 * 
	 * @param holidayTypeId
	 *            Id of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 */
	@Transactional
	public void removeAvailableDays(int holidayTypeId, long userId, float days) {
		// Can only remove days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}

		if (!hasAvailableDays(holidayTypeId, userId, days)) {
			throw new IllegalArgumentException("Can not retrieve " + days + " days: holiday balance linked to type with id " + holidayTypeId
					+ " for the user with id " + userId + " has not enough available days.");
		}

		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(holidayTypeId, userId);
		int remainToRemove = (int) (days * TWELTHS_DAYS);
		HolidayBalance currentBalanceModel;
		for (HolidayBalanceDTO b : balances) {
			currentBalanceModel = em.get().find(HolidayBalance.class, b.id);
			int availInThisBalance = currentBalanceModel.getAvailableBalance();
			if (availInThisBalance > 0F) {
				if (availInThisBalance >= remainToRemove) {
					int newBalanceAvail = availInThisBalance - remainToRemove;
					currentBalanceModel.setAvailableBalance(newBalanceAvail);
					em.get().merge(currentBalanceModel);
					break;
				} else {
					currentBalanceModel.setAvailableBalance(0);
					remainToRemove -= availInThisBalance;
					em.get().merge(currentBalanceModel);
				}
			}
		}
	}

	/**
	 * Updates the availableBalanceUpdated field when a request is made by a
	 * user. Removes automacally days from the oldest balance available.
	 * 
	 * @param holidayTypeId
	 *            Id of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 */
	@Transactional
	public void removeDaysInAvailableUpdatedFromRequest(int holidayTypeId, long userId, float days) {
		// Can only remove days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}

		if (!hasAvailableDays(holidayTypeId, userId, days)) {
			throw new IllegalArgumentException("Can not retrieve " + days + " days: holiday balance linked to type with id " + holidayTypeId
					+ " for the user with id " + userId + " has not enough available days.");
		}

		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(holidayTypeId, userId);
		int remainToRemove = (int) (days * TWELTHS_DAYS);
		HolidayBalance currentBalanceModel;
		for (HolidayBalanceDTO b : balances) {
			currentBalanceModel = em.get().find(HolidayBalance.class, b.id);
			int availInThisBalance = currentBalanceModel.getAvailableBalance();
			if (availInThisBalance > 0F) {
				if (availInThisBalance >= remainToRemove) {
					int newBalanceAvail = availInThisBalance - remainToRemove;
					currentBalanceModel.setAvailableBalanceUpdated(newBalanceAvail);
					em.get().merge(currentBalanceModel);
					break;
				} else {
					currentBalanceModel.setAvailableBalanceUpdated(0);
					remainToRemove -= availInThisBalance;
					em.get().merge(currentBalanceModel);
				}
			}
		}
	}

	/**
	 * Updates the availableBalanceUpdated field when a request is made by a
	 * user. Adds automatically days from the newest balance available. The
	 * available quantity can not be greater than the quantity field * the
	 * period unit defined in the associated type.
	 * 
	 * @param holidayTypeId
	 *            Id of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 */
	@Transactional
	public void addDaysInAvailableUpdatedFromRequest(int holidayTypeId, long userId, float days) {
		// Can only add days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}

		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(holidayTypeId, userId);
		// The method 'getHolidayBalancesAvailable' gives balances in descending order. We have to reverse the collection to have the ascending order.
		List<HolidayBalanceDTO> balancesReversed = new ArrayList<HolidayBalanceDTO>(balances);
		Collections.reverse(balancesReversed);
		int remainToAdd = (int) (days * TWELTHS_DAYS);
		HolidayBalance currentBalanceModel;
		HolidayType type;
		System.out.println("remains : " + remainToAdd);
		for (HolidayBalanceDTO b : balancesReversed) {
			currentBalanceModel = em.get().find(HolidayBalance.class, b.id);
			// Retrieve the maximum quantity of this balance
			int maxOfThisBalance;
			type = currentBalanceModel.getHolidayType();
			maxOfThisBalance = (int) (type.getQuantity() * Math.pow(type.getPeriodUnit(),2));
			
			// Current available quantity
			int availInThisBalance = currentBalanceModel.getAvailableBalanceUpdated();
			System.out.println("avail = > " + availInThisBalance);
			// If the balance is full, switch to the next balance, else we fill it as possible.
			if (availInThisBalance < maxOfThisBalance) {
				// If there is enough space in this balance to add all days
				if (maxOfThisBalance >= (remainToAdd + availInThisBalance)) {
					int newBalanceAvail = availInThisBalance + remainToAdd;
					currentBalanceModel.setAvailableBalanceUpdated(newBalanceAvail);
					em.get().merge(currentBalanceModel);
					break;
				} else { // Increase the balance until its maximum and decrease the remain quantity to add.
					currentBalanceModel.setAvailableBalanceUpdated(maxOfThisBalance);
					remainToAdd -= (maxOfThisBalance - availInThisBalance);
					em.get().merge(currentBalanceModel);
				}
			}
		}
	}

	/**
	 * Update balances by removing days of past holiday requests. A holiday is
	 * considered past when the first date of the request is today. Retrieve all
	 * the details from all requests and remove days from concerned balances.
	 */
	@Transactional
	public void removePastHolidays() {

		log.debug("Removing past holidays");

		List<HolidayRequestDTO> acceptedRequests = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.ACCEPTED_STATUS);

		log.debug("{} requests with accepted status.", acceptedRequests.size());

		long userId;

		for (HolidayRequestDTO request : acceptedRequests) {
			userId = request.userDTO.id;

			// Days for each type of holidays
			Map<Integer, Float> days = new HashMap<Integer, Float>();

			DateTime beginTime = new DateTime(request.beginDate);

			DateTime today = new DateTime();

			log.debug("Begin: {}", beginTime);
			log.debug("Today: {}", today.toDateMidnight());
			// Update balances by removing days of accepted holidays that have
			// been accepted and that are passed
			if (today.toDateMidnight().isAfter(beginTime.toDateMidnight()) || today.toDateMidnight().isEqual(beginTime.toDateMidnight())) {
				// Calculate the amount of days of this request
				for (HolidayDetailDTO detail : request.details) {

					if (!days.containsKey(detail.typeId)) {
						days.put(detail.typeId, 0F);
					}

					if (detail.am) {
						days.put(detail.typeId, Float.valueOf(days.get(detail.typeId) + HALF_DAY));
					}
					if (detail.pm) {
						days.put(detail.typeId, Float.valueOf(days.get(detail.typeId) + HALF_DAY));
					}
				}

				// Consider this requests as "past"
				holidayRequestService.archiveRequest(request.id);

			}

			// Remove days from all concerned balances
			for (Entry<Integer, Float> entries : days.entrySet()) {
				removeAvailableDays(entries.getKey(), userId, entries.getValue());
			}

		}
	}

	/**
	 * TODO Calculate holidays: increment all balances.
	 */
	public void computeHolidays() {
		List<HolidayBalanceDTO> allBalances = this.getAllHolidayBalances();

		Date curDate = new Date();
		TimeZone zone = TimeZone.getTimeZone("UTC");
		Calendar calendar = Calendar.getInstance(zone);
		calendar.setTime(curDate);
		int curMonth = calendar.get(Calendar.MONTH);

		for (HolidayBalanceDTO balance : allBalances) {
			// this.incrementBalance(balance.id);

			// Transfer future balance into available balance if needeed
			if (balance.effectiveMonth == curMonth) {

			}
		}
	}

}