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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	 * @return the holiday balance dto of the holiday balance matching the id passed in parameter.
	 */
	@Transactional
	public HolidayBalanceDTO getHolidayBalanceDTO(int id) {
		HolidayBalance balance = em.get().find(HolidayBalance.class, id);
		return new HolidayBalanceDTO(balance);
	}

	/**
	 * Create a new Holiday balance in database. Available balance and future balance are set to 0.
	 * 
	 * @param holidayTypeId
	 *            The holiday type of this balance.
	 * @param userId
	 *            The user associated to this balance.
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public void createHolidayBalance(long userId, int holidayTypeId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeId + "is not defined.");
		}
		Query verifExist = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type");
		verifExist.setParameter("user", user);
		verifExist.setParameter("type", type);
		List<HolidayBalance> list = (List<HolidayBalance>) verifExist.getResultList();
		if (!list.isEmpty()) {
			throw new IllegalArgumentException("Balance of type " + type.getName() + " and user " + user.getUsername() + " already exists.");
		}

		HolidayBalance balance = new HolidayBalance();
		balance.setUser(user);
		balance.setHolidayType(type);
		balance.setAvailableBalance(0);
		balance.setFutureBalance(0);
		DateTimeZone zoneUTC = DateTimeZone.UTC;
		balance.setLastUpdate(new DateTime().withZone(zoneUTC).toDate());

		em.get().persist(balance);
	}

	/**
	 * Get a specific holiday balance, matching a specific user id and a specific holiday type id.
	 * 
	 * @param userId
	 *            The user associated to this balance.
	 * @param holidayTypeId
	 *            The holiday type of this balance.
	 */
	@Transactional
	public HolidayBalanceDTO getHolidayBalance(long userId, int holidayTypeId) {
		User user = em.get().find(User.class, userId);
		HolidayType type = em.get().find(HolidayType.class, holidayTypeId);

		if (user == null) {
			throw new IllegalArgumentException("Can not create holiday balance : user with id " + userId + " is not defined.");
		}

		if (type == null) {
			throw new IllegalArgumentException("Can not create holiday balance : type with id " + holidayTypeId + "is not defined.");
		}
		Query balanceRequest = em.get().createQuery("SELECT b FROM HolidayBalance b WHERE user=:user AND holidayType=:type");
		balanceRequest.setParameter("user", user);
		balanceRequest.setParameter("type", type);
		try {
			HolidayBalance balance = (HolidayBalance) balanceRequest.getSingleResult();

			return new HolidayBalanceDTO(balance);
		} catch (NoResultException nre) {
			return null;
		}

	}

	/**
	 * Increment the balance with the quantity of time corresponding to the type of holiday.
	 * 
	 * @param holidayBalanceId
	 *            The id of the balance to increment.
	 */
	@Transactional
	public void incrementBalance(int holidayBalanceId) {
		HolidayBalance balance = em.get().find(HolidayBalance.class, holidayBalanceId);

		// Quantity of time (in days) earned each period
		int quantity = balance.getHolidayType().getQuantity();

		// Adjust quantity: balances contains twelths of days
		// Multiply quantity by the ratio of the period.
		// The period unit is 12 for months, 1 for years (constants in HolidayType class).
		quantity = quantity * balance.getHolidayType().getPeriodUnit();

		int newBalance;

		// If there is no effective month of the type of holidays, the available balance is incremented, otherwise the future balance is incremented.
		if (balance.getHolidayType().getEffectiveMonth() == HolidayType.ALL_MONTH) {
			newBalance = balance.getAvailableBalance() + quantity;
			log.debug("Holiday (id:{}) had available balance: {}", holidayBalanceId, balance.getAvailableBalance());
			log.debug("Holiday (id:{}) incremented by {}", holidayBalanceId, quantity);
			balance.setAvailableBalance(newBalance);
			log.debug("Holiday (id:{}) new balance: {}", holidayBalanceId, balance.getAvailableBalance());

		} else {
			// If there is a specific month when future balance is added to available balance, adds the quantity to the future balance
			log.debug("Holiday (id:{}) had future balance: {}", holidayBalanceId, balance.getAvailableBalance());
			log.debug("Holiday (id:{}) incremented by {}", holidayBalanceId, quantity);
			newBalance = balance.getFutureBalance() + quantity;
			balance.setFutureBalance(newBalance);
			log.debug("Holiday (id:{}) future balance: {}", holidayBalanceId, balance.getAvailableBalance());
		}

		DateTimeZone zoneUTC = DateTimeZone.UTC;
		Date today = new DateTime().withZone(zoneUTC).toDate();

		// Actualize date
		balance.setLastUpdate(today);

		em.get().merge(balance);
	}

	/**
	 * Adds the future balance to the available balance.
	 * 
	 * @param holidayBalanceId
	 *            the balance concerned by the transfer.
	 */
	@Transactional
	public void transferFutureBalance(int holidayBalanceId) {
		HolidayBalance balance = em.get().find(HolidayBalance.class, holidayBalanceId);

		balance.setAvailableBalance(balance.getAvailableBalance() + balance.getFutureBalance());

		// Reset future balance
		balance.setFutureBalance(0);

		em.get().merge(balance);
	}

	/**
	 * Verify if the balance has the amount of days.
	 * 
	 * @param holidayBalanceId
	 *            the id of the balance.
	 * @param days
	 *            the number of days.
	 */
	@Transactional
	public boolean hasAvailableDays(int holidayBalanceId, float days) {
		HolidayBalanceDTO balance = getHolidayBalanceDTO(holidayBalanceId);
		return balance.availableBalance >= days;
	}

	/**
	 * Remove days or half days to the available balance.
	 */
	/**
	 * Verify if the balance has the amount of days.
	 * 
	 * @param holidayBalanceId
	 *            the id of the balance.
	 * @param days
	 *            the number of days.
	 */
	@Transactional
	public void removeAvailableDays(int holidayBalanceId, float days) {
		// Can only remove days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only retrieve days or half days. " + days + " is not a multiple of half day");
		}

		if (!hasAvailableDays(holidayBalanceId, days)) {
			throw new IllegalArgumentException("Can not retrieve " + days + " days: holiday balance with id " + holidayBalanceId
					+ " has not enough available days.");
		}

		HolidayBalance balance = em.get().find(HolidayBalance.class, holidayBalanceId);
		int twelthsAvailableDays = balance.getAvailableBalance();

		// Convert days to twelth days, because in database we store twelths days.
		int toRetrieve = (int) (days * TWELTHS_DAYS);

		balance.setAvailableBalance(twelthsAvailableDays - toRetrieve);

		em.get().merge(balance);

	}

	/**
	 * Update balances by removing days of past holiday requests. A holiday is considered past when the first date of the request is today.
	 */
	@Transactional
	public void removePastHolidays() {
		List<HolidayRequestDTO> requests = holidayRequestService.getAllRequestsWithStatus(HolidayRequest.ACCEPTED_STATUS);
		
		log.debug("Removing past holidays");
		
		for (HolidayRequestDTO request : requests){
			
			// Days for each type of holidays
			Map<String,Float> days = new HashMap<String,Float>();

			// Balance id associated to holidays types
			Map<String,Integer> balances = new HashMap<String,Integer>();

			DateTime beginTime = new DateTime(request.beginDate);

			DateTimeZone zoneUTC = DateTimeZone.UTC;
			DateTime today = new DateTime().withZone(zoneUTC);

			log.debug("Begin: "+beginTime.withZone(DateTimeZone.UTC));
			log.debug("Today: "+today.withZone(DateTimeZone.UTC).toDateMidnight());

			if (today.withZone(DateTimeZone.UTC).toDateMidnight().isEqual(beginTime.withZone(DateTimeZone.UTC).plusHours(1).toDateMidnight())){

				// Calculate the amount of days of this request
				for (HolidayDetailDTO detail : request.details){

					if(!days.containsKey(detail.type)){
						days.put(detail.type, 0F);
						balances.put(detail.type, detail.balanceId);
					}
					
					if (detail.am){
						days.put(detail.type, Float.valueOf(days.get(detail.type) + HALF_DAY));
					}
					if (detail.pm){
						days.put(detail.type, Float.valueOf(days.get(detail.type) + HALF_DAY));
					}
				}
			}

			for (Entry<String, Float> set : days.entrySet()){
				removeAvailableDays(balances.get(set.getKey()), days.get(set.getKey()));
			}

		}
	}

}