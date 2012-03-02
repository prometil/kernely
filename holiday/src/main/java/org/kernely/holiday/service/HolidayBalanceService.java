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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.kernely.holiday.model.HolidayProfile;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;
import org.kernely.holiday.model.HolidayTypeInstance;
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

	@Inject
	private HolidayService holidayProfileService;

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
	public HolidayBalanceDTO getHolidayBalanceDTO(long id) {
		HolidayBalance balance = em.get().find(HolidayBalance.class, id);
		return new HolidayBalanceDTO(balance);
	}

	/**
	 * Create a new Holiday balance in database for the current year. Available
	 * balance are set to 0.
	 * 
	 * @param typeId
	 *            The holiday type of this balance.
	 * @param userId
	 *            The user associated to this balance.
	 * @return A DTO representing the new balance created
	 */
	public HolidayBalanceDTO createHolidayBalance(long typeId, long userId) {
		return this.createHolidayBalance(typeId, userId, DateTime.now().getYear());
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
	 * @param typeId
	 *            The holiday type of this balance.
	 * @param userId
	 *            The user associated to this balance.
	 * @param year
	 *            The year associated to the new balance. If 0, this is the
	 *            current year.
	 * @return A DTO representing the new balance created
	 */
	@Transactional
	public HolidayBalanceDTO createHolidayBalance(long typeId, long userId, int year) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, typeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + typeId + "is not defined.");
		}

		HolidayBalance balance = new HolidayBalance();
		balance.setUser(user);
		balance.setHolidayTypeInstance(type.getCurrentInstance());
		balance.setAvailableBalance(0);
		if (!type.isUnlimited()) {
			// This field contains the theoric value of the balance : Quantity *
			// Period Unit * 12 to have that for the year
			balance.setAvailableBalanceUpdated(type.getQuantity() * type.getPeriodUnit() * 12);
		} else {
			// This field doesn't matter in the case of illimited balance
			balance.setAvailableBalanceUpdated(0);
		}
		DateTimeZone zoneUTC = DateTimeZone.UTC;
		balance.setLastUpdate(new DateTime().withZone(zoneUTC).toDate());

		// In the case where there is an effective month, we set the begin date
		// at the end of the previous balance,
		// and the end date to effective month of the N+1 year
		try {
			HolidayBalanceDTO previous = this.getPreviousBalance(type.getCurrentInstance().getId(), userId);
			if (previous == null) {
				// In the case where there is no previous, when
				// it's a new balance
				balance.setBeginDate(this.getNextCompleteMonth(DateTime.now().withYear(year).toDate()));
				// We check when the new balance is created
				// If the current date is after the effectiveness of this type
				// of
				// holiday, end date is set one year later
				// else, end date is the current year, the first of the
				// effective
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
			} else { // In the case where there is a previous, when this is just
				// the
				// balance for the next period
				DateTime beginDate = new DateTime(previous.endDate).toDateMidnight().toDateTime();
				if (beginDate.isAfter(DateTime.now().plusDays(1))) {
					throw new IllegalArgumentException("Impossible to create a balance in the future.");
				}
				balance.setBeginDate(new DateTime(previous.endDate).toDateMidnight().toDate());
				balance.setEndDate(new DateTime(previous.endDate).plusYears(1).toDateMidnight().toDate());
			}

			em.get().persist(balance);

			return new HolidayBalanceDTO(balance);
		} catch (IllegalArgumentException iae) {
			log.debug(iae.getMessage());
			return null;
		}
	}

	/**
	 * Create a new balance for a new user in function of his hire date.
	 * 
	 * @param typeId
	 *            Id of the new balance's type
	 * @param userId
	 *            Id the the concerned user
	 * @return A DTO representing the new balance created
	 */
	public HolidayBalanceDTO createHolidayBalanceForNewUser(long typeId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, typeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + typeId + "is not defined.");
		}
		// Verify if a balance has already been created for this user and this
		// type.
		if (getPreviousBalance(type.getCurrentInstance().getId(), userId) != null) {
			throw new IllegalArgumentException("The user " + userId + " has already a balance for the type " + typeId
					+ ". Impossible to create the new balance.");
		}

		HolidayBalance balance = new HolidayBalance();
		balance.setUser(user);
		balance.setHolidayTypeInstance(type.getCurrentInstance());
		balance.setAvailableBalance(0);
		// This field contains the theoric value of the balance : Quantity *
		// Period Unit * 12 to have that for the year
		balance.setAvailableBalanceUpdated(type.getQuantity() * type.getPeriodUnit() * 12);
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
	 * @param holidayTypeInstanceId
	 *            Instance of Type of the balance concerned
	 * @param userId
	 *            Id of the owner of the balance concerned
	 * @return A DTO representing the previous balance, null if doesn't exist
	 */
	@Transactional
	public HolidayBalanceDTO getPreviousBalance(long holidayTypeInstanceId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayTypeInstance typeInstance = em.get().find(HolidayTypeInstance.class, holidayTypeInstanceId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (typeInstance == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeInstanceId + "is not defined.");
		}

		Query balanceRequest = em.get().createQuery(
				"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayTypeInstance=:type ORDER BY endDate DESC");
		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", typeInstance);
		balanceRequest.setMaxResults(1); // We just want the first element
		try {
			HolidayBalance balance = (HolidayBalance) balanceRequest.getSingleResult();
			return new HolidayBalanceDTO(balance);
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Verify if a balance exists for the given period of time.
	 * 
	 * @param holidayTypeInstanceId
	 *            Instance of the type id of the balance to verify
	 * @param userId
	 *            The owner of the balance
	 * @param begin
	 *            Begin date of the period to check
	 * @param end
	 *            End date of the period to check
	 * @return True if a balance exists for this period of time, false
	 *         elsewhere.
	 */
	@Transactional
	public boolean existBalanceForTimePeriod(long holidayTypeInstanceId, long userId, Date begin, Date end) {
		User user = em.get().find(User.class, userId);
		HolidayTypeInstance typeInstance = em.get().find(HolidayTypeInstance.class, holidayTypeInstanceId);

		Query balanceRequest = em
				.get()
				.createQuery(
						"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayTypeInstance=:type AND beginDate = :begin AND endDate = :end ORDER BY endDate DESC");
		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", typeInstance);
		balanceRequest.setParameter("begin", begin);
		balanceRequest.setParameter("end", end);
		balanceRequest.setMaxResults(1); // We just want the first element
		try {
			balanceRequest.getSingleResult();
			return true;
		} catch (NoResultException nre) {
			return false;
		}
	}

	/**
	 * Get a set of holiday balance, matching a specific user id and a specific
	 * holiday type id. If the balance is associated to a type with possibility
	 * of anticipation, this method will return all old balances and the balance
	 * for the current year. If this is not a type with possibility of
	 * anticipation, it will return all balances except the one for the current
	 * year. Balance are returned from the oldest to the newest.
	 * 
	 * @param userId
	 *            The user associated to this balance.
	 * @param holidayTypeInstanceId
	 *            Instance of the holiday type of this balance.
	 * @return A set of all available balances
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Set<HolidayBalanceDTO> getHolidayBalancesAvailable(long holidayTypeInstanceId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayTypeInstance typeInstance = em.get().find(HolidayTypeInstance.class, holidayTypeInstanceId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (typeInstance == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeInstanceId + "is not defined.");
		}

		Query balanceRequest;
		if (typeInstance.isAnticipated()) {
			balanceRequest = em.get().createQuery(
					"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayTypeInstance=:type AND endDate <= :date ORDER BY beginDate ASC");
			balanceRequest.setParameter("date", DateTime.now().plusYears(1).toDateMidnight().toDate());
		} else {
			balanceRequest = em.get().createQuery(
					"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayTypeInstance=:type AND endDate <= :date ORDER BY begin_date ASC");
			balanceRequest.setParameter("date", DateTime.now().toDateMidnight().toDate());
		}

		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", typeInstance);
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
	 * @param holidayTypeInstanceId
	 *            Instance of type of the balance needed
	 * @param userId
	 *            Id of the owner of the balance needed
	 * @return A DTO representing the latest balance created
	 */
	@Transactional
	public HolidayBalanceDTO getProcessedBalance(long holidayTypeInstanceId, long userId) {
		User user = em.get().find(User.class, userId);
		HolidayTypeInstance typeInstance = em.get().find(HolidayTypeInstance.class, holidayTypeInstanceId);

		Query balanceRequest = em.get().createQuery(
				"SELECT b FROM HolidayBalance b WHERE user=:user AND holidayTypeInstance=:type ORDER BY end_date DESC");
		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", typeInstance);
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
	 * availableBalance of the amount of the Quantity field defined in the type
	 * associated * the period unit field defined in the type associated too.
	 * 
	 * 
	 * @param typeId
	 *            The id of the type linked to the balance.
	 * @param userId
	 *            Id of the owner of the balance to increment
	 */
	@Transactional
	public void incrementBalance(long typeId, long userId) {
		HolidayType type = em.get().find(HolidayType.class, typeId);
		HolidayBalanceDTO bDto = getProcessedBalance(type.getCurrentInstance().getId(), userId);
		if (bDto == null) {
			throw new IllegalArgumentException("There is no balance associated to the user with the type " + userId
					+ " and the holiday type with id " + typeId);
		}
		HolidayBalance balance = em.get().find(HolidayBalance.class, bDto.id);

		// Only limited balances are incremented
		if (!type.isUnlimited()) {

			// Quantity of time (in days) earned each period
			int quantity = type.getQuantity();

			// Adjust quantity: balances contains twelths of days
			// Multiply quantity by the ratio of the period.
			// The period unit is 12 for months, 1 for years (constants in
			// HolidayType class).
			quantity = quantity * type.getPeriodUnit();

			// If there is no effective month of the type of holidays, the
			// available balance is incremented, otherwise the future balance is
			// incremented.
			int newBalance = balance.getAvailableBalance() + quantity;
			log.debug("Holiday (id:{}) had available balance: {}", balance.getId(), balance.getAvailableBalance());
			log.debug("Holiday (id:{}) incremented by {}", balance.getId(), quantity);
			balance.setAvailableBalance(newBalance);
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
	 * @param holidayTypeInstanceId
	 *            Id of the instance of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 * @return True if "days" are available, else false
	 */
	@Transactional
	public boolean hasAvailableDays(long holidayTypeInstanceId, long userId, float days) {
		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(holidayTypeInstanceId, userId);
		float availDays = 0F;
		for (HolidayBalanceDTO b : balances) {
			availDays += (b.availableBalance * TWELTHS_DAYS);
		}
		return availDays >= (days * TWELTHS_DAYS);
	}

	/**
	 * Verify if the balance has the amount of days considering the amount of
	 * days updated with requests.
	 * 
	 * @param typeInstanceId
	 *            Id of the instance of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 * @return True if "days" are available, else false
	 */
	@Transactional
	public boolean hasAvailableDaysUpdated(long typeInstanceId, long userId, float days) {
		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(typeInstanceId, userId);
		float availDays = 0F;
		for (HolidayBalanceDTO b : balances) {
			availDays += (b.availableBalanceUpdated * TWELTHS_DAYS);
		}
		return availDays >= (days * TWELTHS_DAYS);
	}

	/**
	 * Remove days or half days to the available balance. Verify if the balance
	 * has the amount of days.
	 * 
	 * @param holidayTypeInstanceId
	 *            Id of the instance of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 */
	@Transactional
	public void removeAvailableDays(long holidayTypeInstanceId, long userId, float days) {
		HolidayTypeInstance instance = em.get().find(HolidayTypeInstance.class, holidayTypeInstanceId);

		// Can only remove days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}

		// If the type associated doesn't allowed anticipation, balance don't
		// have to be negative.
		if (!instance.isAnticipated() && !hasAvailableDays(holidayTypeInstanceId, userId, days)) {
			throw new IllegalArgumentException("Can not retrieve " + days + " days: holiday balance linked to type with id " + holidayTypeInstanceId
					+ " for the user with id " + userId + " has not enough available days.");
		}

		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(holidayTypeInstanceId, userId);
		int remainToRemove = (int) (days * TWELTHS_DAYS);
		HolidayBalance currentBalanceModel;
		for (HolidayBalanceDTO b : balances) {
			currentBalanceModel = em.get().find(HolidayBalance.class, b.id);
			int availInThisBalance = currentBalanceModel.getAvailableBalance();
			if (availInThisBalance > 0F) {
				if (availInThisBalance >= remainToRemove) {
					int newBalanceAvail = availInThisBalance - remainToRemove;
					remainToRemove = 0;
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
		// If we are in an anticipated type, and there are yet some days to
		// remove, we remove these days from the newest balance.
		if (remainToRemove > 0 && instance.isAnticipated()) {
			HolidayBalanceDTO last = this.getProcessedBalance(holidayTypeInstanceId, userId);
			HolidayBalance lastBalance = em.get().find(HolidayBalance.class, last.id);

			int newBalanceAvail = lastBalance.getAvailableBalance() - remainToRemove;
			lastBalance.setAvailableBalance(newBalanceAvail);
			em.get().merge(lastBalance);
		}
	}

	/**
	 * Updates the availableBalanceUpdated field when a request is made by a
	 * user. Removes automatically days from the oldest balance available.
	 * 
	 * @param typeInstanceId
	 *            Id of the instance of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 */
	@Transactional
	public void removeDaysInAvailableUpdatedFromRequest(long typeInstanceId, long userId, float days) {
		// Can only remove days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}

		if (!hasAvailableDaysUpdated(typeInstanceId, userId, days)) {
			throw new IllegalArgumentException("Can not retrieve " + days + " days: holiday balance linked to type with id " + typeInstanceId
					+ " for the user with id " + userId + " has not enough available days.");
		}

		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(typeInstanceId, userId);
		int remainToRemove = (int) (days * TWELTHS_DAYS);
		HolidayBalance currentBalanceModel;
		for (HolidayBalanceDTO b : balances) {
			currentBalanceModel = em.get().find(HolidayBalance.class, b.id);
			int availInThisBalance = currentBalanceModel.getAvailableBalanceUpdated();
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
	 * @param typeInstanceId
	 *            Id of the instance of the type of holiday needed
	 * @param userId
	 *            User concerned by the balance needed
	 * @param days
	 *            Number of days requested
	 */
	@Transactional
	public void addDaysInAvailableUpdatedFromRequest(long typeInstanceId, long userId, float days) {
		// Can only add days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}
		HolidayTypeInstance typeInstance = em.get().find(HolidayTypeInstance.class, typeInstanceId);
		Set<HolidayBalanceDTO> balances = getHolidayBalancesAvailable(typeInstanceId, userId);
		// The method 'getHolidayBalancesAvailable' gives balances in descending
		// order. We have to reverse the collection to have the ascending order.
		List<HolidayBalanceDTO> balancesReversed = new ArrayList<HolidayBalanceDTO>(balances);
		Collections.reverse(balancesReversed);
		int remainToAdd = (int) (days * TWELTHS_DAYS);
		HolidayBalance currentBalanceModel;
		int maxOfThisBalance;
		int availInThisBalance;
		for (HolidayBalanceDTO b : balancesReversed) {
			currentBalanceModel = em.get().find(HolidayBalance.class, b.id);
			// Retrieve the maximum quantity of this balance
			maxOfThisBalance = (int) (typeInstance.getQuantity() * typeInstance.getPeriodUnit() * 12);
			// Current available quantity
			availInThisBalance = currentBalanceModel.getAvailableBalanceUpdated();
			// If the balance is full, switch to the next balance, else we fill
			// it as possible.
			if (availInThisBalance < maxOfThisBalance) {
				// If there is enough space in this balance to add all days
				if (maxOfThisBalance >= (remainToAdd + availInThisBalance)) {
					int newBalanceAvail = availInThisBalance + remainToAdd;
					currentBalanceModel.setAvailableBalanceUpdated(newBalanceAvail);
					em.get().merge(currentBalanceModel);
					break;
				} else { // Increase the balance until its maximum and decrease
					// the remain quantity to add.
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
			Map<Long, Float> days = new HashMap<Long, Float>();

			DateTime beginTime = new DateTime(request.beginDate);

			DateTime today = new DateTime();

			log.debug("Begin: {}", beginTime);
			log.debug("Today: {}", today.toDateMidnight());
			// Update balances by removing days of accepted holidays that have
			// been accepted and that are passed
			if (today.toDateMidnight().isAfter(beginTime.toDateMidnight()) || today.toDateMidnight().isEqual(beginTime.toDateMidnight())) {
				// Calculate the amount of days of this request
				for (HolidayDetailDTO detail : request.details) {

					if (!days.containsKey(detail.typeInstanceId)) {
						days.put(detail.typeInstanceId, 0F);
					}

					if (detail.am) {
						days.put(detail.typeInstanceId, Float.valueOf(days.get(detail.typeInstanceId) + HALF_DAY));
					}
					if (detail.pm) {
						days.put(detail.typeInstanceId, Float.valueOf(days.get(detail.typeInstanceId) + HALF_DAY));
					}
				}

				// Consider this requests as "past"
				holidayRequestService.archiveRequest(request.id);

			}

			// Remove days from all concerned balances
			for (Entry<Long, Float> entries : days.entrySet()) {
				removeAvailableDays(entries.getKey(), userId, entries.getValue());
			}

		}
	}

	/**
	 * Method called automatically by the Quartz Job. Retrieves all the created
	 * holiday profiles and their types associated. Increments each balances
	 * retrieved by the association type/user. If this method is called on the
	 * effective month of the holiday type, create the balance for the next year
	 */
	public void computeHolidays() {
		DateTime now = DateTime.now().toDateMidnight().toDateTime();

		for (HolidayProfile profile : holidayProfileService.getAllProfile()) {
			for (HolidayType type : profile.getHolidayTypes()) {
				HolidayTypeInstance currentInstance = type.getCurrentInstance();
				for (User user : currentInstance.getUsers()) {
					this.incrementBalance(type.getId(), user.getId());
					HolidayBalanceDTO balance = this.getProcessedBalance(currentInstance.getId(), user.getId());
					DateTime endDate = new DateTime(balance.endDate);
					if (now.isEqual(endDate) || now.isAfter(endDate)) {
						this.createHolidayBalance(type.getId(), user.getId());
					}
				}
			}
		}
	}

}