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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.HolidayBalanceDTO;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayRequestDTO;
import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayBalanceServiceTest extends AbstractServiceTest {

	private static final String TEST_STRING = "type";
	private static final String USERNAME= "username";
	private static final int QUANTITY = 3;
	private static final int EFFECTIVE_MONTH = HolidayType.JUNE;
	private static final int PERIOD = HolidayType.PERIOD_MONTH;
	
	private static final int AVAIL = QUANTITY * PERIOD;

	private static final Date DATE_TODAY = new DateTime().withZone(DateTimeZone.UTC).toDate();
	private static final Date DATE_TOMORROW = new DateTime().plusDays(1).withZone(DateTimeZone.UTC).toDate();
	
	private static final Date FUTURE_HIRE_DATE = new DateTime().plusMonths(1).withDayOfMonth(15).toDateMidnight().toDate();
	private static final Date PASSED_HIRE_DATE = new DateTime().withMonthOfYear(6).withDayOfMonth(15).minusYears(3).toDateMidnight().toDate();

	private static final Date NEXT_COMPLETE_MONTH_DATE_FOR_FUTURE_HIRE = new DateTime(FUTURE_HIRE_DATE).plusMonths(1).withDayOfMonth(1).toDateMidnight().toDate();

	private static final Date END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR = new DateTime().withMonthOfYear(1).withDayOfMonth(1).plusYears(1).toDateMidnight().toDate();


	@Inject
	private HolidayBalanceService holidayBalanceService;

	@Inject
	private HolidayService holidayService;

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	@Inject
	private HolidayRequestService requestService;

	//	@Inject
	//	private HolidayRequestService requestService;

	//******************************************************************************//
	// Helpers
	//******************************************************************************//
	
	private Date getNextCompleteMonth(){
		if(DateTime.now().getDayOfMonth() != 1){
			return DateTime.now().withDayOfMonth(1).plusMonths(1).toDateMidnight().toDate();
		}
		else{
			return DateTime.now().toDateMidnight().toDate();
		}
	}

	private Date getNextEndDateForNewBalance(){
		DateTime theoricEnd = new DateTime().withDayOfMonth(1).withMonthOfYear(EFFECTIVE_MONTH);
		if(DateTime.now().isAfter(theoricEnd.toDateMidnight())){
			return theoricEnd.plusYears(1).toDateMidnight().toDate();
		}
		else{
			return theoricEnd.toDateMidnight().toDate();
		}
	}

	private HolidayDTO createHolidayTypeAllMonthForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		request.anticipation = false;
		request.unlimited = false;
		return holidayService.createOrUpdateHoliday(request);
	}

	private HolidayDTO createHolidayTypeUnlimitedForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		request.anticipation = true;
		request.unlimited = true;
		return holidayService.createOrUpdateHoliday(request);
	}

	private HolidayDTO createHolidayTypeAllMonthAnticipatedForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		request.unlimited = false;
		request.anticipation = true;
		return holidayService.createOrUpdateHoliday(request);
	}

	private HolidayDTO createHolidayTypeSpecificMonthForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = EFFECTIVE_MONTH;
		request.anticipation = false;
		request.unlimited = false;
		return holidayService.createOrUpdateHoliday(request);
	}

	private HolidayDTO createHolidayTypeSpecificMonthAnticipatedForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.name = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = EFFECTIVE_MONTH;
		request.anticipation = true;
		request.unlimited = false;
		return holidayService.createOrUpdateHoliday(request);
	}

	private UserDTO createUserNewHiredForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME;
		request.password = TEST_STRING;
		request.hire = FUTURE_HIRE_DATE;
		return userService.createUser(request);
	}

	private UserDTO createUserOldHiredForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME;
		request.password = TEST_STRING;
		request.hire = PASSED_HIRE_DATE;
		return userService.createUser(request);
	}

	//******************************************************************************//
	// CreateBalanceForNewUser method
	//******************************************************************************//
	
	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceWithNullUser(){
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		holidayBalanceService.createHolidayBalance(type.id, Long.valueOf(0),  0);		
	}

	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceWithNullType(){
		UserDTO user = createUserOldHiredForTest();
		holidayBalanceService.createHolidayBalance(0, user.id, 0);		
	}

	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceForNewUserWithNullUser(){
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		holidayBalanceService.createHolidayBalanceForNewUser(type.id, Long.valueOf(0));		
	}

	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceForNewUserWithNullType(){
		UserDTO user = createUserOldHiredForTest();
		holidayBalanceService.createHolidayBalanceForNewUser(0, user.id);		
	}

	@Test
	public void createNewBalanceForNewUserForAllMonthTest(){
		UserDTO user = createUserNewHiredForTest();

		HolidayDTO type = createHolidayTypeAllMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalanceForNewUser(type.id, user.id);
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(NEXT_COMPLETE_MONTH_DATE_FOR_FUTURE_HIRE , newBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , newBalance.endDate);
	}

	@Test
	public void createNewBalanceForNewUserForSpecMonthTest(){
		UserDTO user = createUserNewHiredForTest();
		HolidayDTO type = createHolidayTypeSpecificMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalanceForNewUser(type.id, user.id);
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(NEXT_COMPLETE_MONTH_DATE_FOR_FUTURE_HIRE , newBalance.beginDate);
		assertEquals(this.getNextEndDateForNewBalance() , newBalance.endDate);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void createTwoBalancesForNewUserForTheSameTypeTest(){
		UserDTO user = createUserNewHiredForTest();
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		holidayBalanceService.createHolidayBalanceForNewUser(type.id, user.id);
		holidayBalanceService.createHolidayBalanceForNewUser(type.id,user.id);
	}

	//******************************************************************************//
	// CreateHolidayBalance method
	//******************************************************************************//
	
	@Test
	public void createNewBalanceForOldUserForAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , newBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , newBalance.endDate);
	}

	@Test
	public void createNewBalanceForOldUserWithSpecificYear(){
		UserDTO user = createUserOldHiredForTest();
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, 2000);
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).withYear(2000).toDate() , newBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).withYear(2001).toDate() , newBalance.endDate);
	}

	@Test
	public void createNewBalanceForOldUserForSpecMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		HolidayDTO type = createHolidayTypeSpecificMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , newBalance.beginDate);
		assertEquals(this.getNextEndDateForNewBalance() , newBalance.endDate);
	}

	@Test
	public void createNextBalanceForAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(1).getYear());
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(1).toDate() , newBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(1).toDate() , newBalance.endDate);
		HolidayBalanceDTO nextBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(1).getYear());
		assertEquals(0, nextBalance.availableBalance, 0);
		assertEquals(AVAIL, nextBalance.availableBalanceUpdated, 0);
		assertEquals(newBalance.endDate , nextBalance.beginDate);
		Date endDate = new DateTime(newBalance.endDate).plusYears(1).toDate();
		assertEquals(endDate, nextBalance.endDate);
	}

	@Test
	public void createNextBalanceForSpecificMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		HolidayDTO type = createHolidayTypeSpecificMonthForTest();
		HolidayBalanceDTO newBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(1).getYear());
		assertEquals(0, newBalance.availableBalance, 0);
		assertEquals(AVAIL, newBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(1).toDate() , newBalance.beginDate);
		assertEquals(new DateTime(this.getNextEndDateForNewBalance()).minusYears(1).toDate() , newBalance.endDate);
		HolidayBalanceDTO nextBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, nextBalance.availableBalance, 0);
		assertEquals(AVAIL, nextBalance.availableBalanceUpdated, 0);
		assertEquals(newBalance.endDate , nextBalance.beginDate);
		Date endDate = new DateTime(newBalance.endDate).plusYears(1).toDate();
		assertEquals(endDate, nextBalance.endDate);
	}
	
	@Test
	public void createBalanceInTheFuture(){
		UserDTO user = createUserOldHiredForTest();
		HolidayDTO type = createHolidayTypeSpecificMonthForTest();
		// Will create a balance for the current year
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Try to create a balance for the next year
		HolidayBalanceDTO balance = holidayBalanceService.createHolidayBalance(type.id, user.id);	
		assertNull(balance);
	}
	
	//******************************************************************************//
	// GetAvailableBalances method
	//******************************************************************************//

	@Test
	public void retrieveAllAvailableBalancesWithNoAnticipationSpecMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeSpecificMonthForTest();
		//Balance for the year N - 2
		HolidayBalanceDTO m2yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(this.getNextEndDateForNewBalance()).minusYears(2).toDate() , m2yBalance.endDate);

		assertEquals(m2yBalance.endDate , m1yBalance.beginDate);
		assertEquals(new DateTime(m2yBalance.endDate).plusYears(1).toDate() , m1yBalance.endDate);

		assertEquals(m1yBalance.endDate , actualBalance.beginDate);
		assertEquals(new DateTime(m1yBalance.endDate).plusYears(1).toDate() , actualBalance.endDate);

		// With this holiday type, getHolidayBalancesAvailable should return 2 balances. The third being not available for the moment
		Set<HolidayBalanceDTO> availBalances = holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id);
		assertEquals(2, availBalances.size());		
	}

	@Test
	public void retrieveAllAvailableBalancesWithAnticipationSpecMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeSpecificMonthAnticipatedForTest();
		//Balance for the year N - 2
		HolidayBalanceDTO m2yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(this.getNextEndDateForNewBalance()).minusYears(2).toDate() , m2yBalance.endDate);

		assertEquals(m2yBalance.endDate , m1yBalance.beginDate);
		assertEquals(new DateTime(m2yBalance.endDate).plusYears(1).toDate() , m1yBalance.endDate);

		assertEquals(m1yBalance.endDate , actualBalance.beginDate);
		assertEquals(new DateTime(m1yBalance.endDate).plusYears(1).toDate() , actualBalance.endDate);

		// With this holiday type, getHolidayBalancesAvailable should return 3 balances. The third (actual) being available for the moment
		Set<HolidayBalanceDTO> availBalances = holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id);
		assertEquals(3, availBalances.size());		
	}

	@Test
	public void retrieveAllAvailableBalancesWithNoAnticipationAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		//Balance for the year N - 2
		HolidayBalanceDTO m2yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(2).toDate() , m2yBalance.endDate);

		assertEquals(m2yBalance.endDate , m1yBalance.beginDate);
		assertEquals(new DateTime(m2yBalance.endDate).plusYears(1).toDate() , m1yBalance.endDate);

		assertEquals(m1yBalance.endDate , actualBalance.beginDate);
		assertEquals(new DateTime(m1yBalance.endDate).plusYears(1).toDate() , actualBalance.endDate);

		// With this holiday type, getHolidayBalancesAvailable should return 2 balances. The third being not available for the moment
		Set<HolidayBalanceDTO> availBalances = holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id);
		assertEquals(2, availBalances.size());		
	}

	@Test
	public void retrieveAllAvailableBalancesWithAnticipationAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();
		//Balance for the year N - 2
		HolidayBalanceDTO m2yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(2).toDate() , m2yBalance.endDate);

		assertEquals(m2yBalance.endDate , m1yBalance.beginDate);
		assertEquals(new DateTime(m2yBalance.endDate).plusYears(1).toDate() , m1yBalance.endDate);

		assertEquals(m1yBalance.endDate , actualBalance.beginDate);
		assertEquals(new DateTime(m1yBalance.endDate).plusYears(1).toDate() , actualBalance.endDate);

		// With this holiday type, getHolidayBalancesAvailable should return 3 balances. The third (actual) being available for the moment
		Set<HolidayBalanceDTO> availBalances = holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id);
		assertEquals(3, availBalances.size());		
	}

	@Test
	public void retrieveAllAvailableBalancesWithAnticipationWithOneBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		// With this holiday type, getHolidayBalancesAvailable should return 1 balance, for N. N+1 and N+2 are not considered
		Set<HolidayBalanceDTO> availBalances = holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id);
		assertEquals(1, availBalances.size());		
	}

	@Test
	public void retrieveAllAvailableBalancesWithNoAnticipationWithOneBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		// With this holiday type, getHolidayBalancesAvailable should return 0 balances.
		Set<HolidayBalanceDTO> availBalances = holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id);
		assertEquals(0, availBalances.size());	
	}

	//******************************************************************************//
	// GetProcessedBalance method
	//******************************************************************************//

	
	@Test
	public void retrieveLatestBalanceProcessed(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(1).getYear());
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(1).toDate() , m1yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(1).toDate() , m1yBalance.endDate);

		assertEquals(m1yBalance.endDate , actualBalance.beginDate);
		assertEquals(new DateTime(m1yBalance.endDate).plusYears(1).toDate() , actualBalance.endDate);

		HolidayBalanceDTO processedBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);
		assertEquals(actualBalance.beginDate, processedBalance.beginDate);
		assertEquals(actualBalance.endDate, processedBalance.endDate);
	}
	
	//******************************************************************************//
	// IncrementBalance method
	//******************************************************************************//

	@Test(expected = IllegalArgumentException.class)
	public void incrementBalanceWithNoBalance(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		holidayBalanceService.incrementBalance(type.id, user.id);
	}

	@Test
	public void incrementBalanceAnticipationForAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , actualBalance.endDate);

		holidayBalanceService.incrementBalance(type.id, user.id);

		actualBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);

		assertEquals(QUANTITY, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , actualBalance.endDate);
	}

	@Test
	public void incrementBalanceMassivelyForAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , actualBalance.endDate);

		for(int i = 0; i < 1000; i++){
			holidayBalanceService.incrementBalance(type.id, user.id);
		}
		actualBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);

		assertEquals(QUANTITY * 1000, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , actualBalance.endDate);
	}
	
	@Test
	public void incrementSeveralBalanceForAllMonthTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		HolidayBalanceDTO m2yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		holidayBalanceService.incrementBalance(type.id, user.id);
		m2yBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);

		assertEquals(QUANTITY, m2yBalance.availableBalance, 0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(2).toDate() , m2yBalance.endDate);

		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);
		m1yBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);

		assertEquals(QUANTITY, m1yBalance.availableBalance, 0);
		assertEquals(AVAIL, m1yBalance.availableBalanceUpdated, 0);
		assertEquals(m2yBalance.endDate , m1yBalance.beginDate);
		assertEquals(new DateTime(m2yBalance.endDate).plusYears(1).toDate() , m1yBalance.endDate);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);
		actualBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);

		assertEquals(QUANTITY, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(m1yBalance.endDate , actualBalance.beginDate);
		assertEquals(new DateTime(m1yBalance.endDate).plusYears(1).toDate() , actualBalance.endDate);

	}

	@Test
	public void incrementUnlimitedBalance(){
		UserDTO user = createUserOldHiredForTest();
		// With unlimited holiday type
		HolidayDTO type = createHolidayTypeUnlimitedForTest();
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actualBalance.availableBalance, 0);
		assertEquals(0, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , actualBalance.endDate);

		holidayBalanceService.incrementBalance(type.id, user.id);

		actualBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);
		assertEquals(0, actualBalance.availableBalance, 0);
		assertEquals(0, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR , actualBalance.endDate);
	}

	@Test
	public void incrementBalanceForSpecificMonth(){
		UserDTO user = createUserOldHiredForTest();
		// With unlimited holiday type
		HolidayDTO type = createHolidayTypeSpecificMonthForTest();
		HolidayBalanceDTO actualBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(this.getNextEndDateForNewBalance() , actualBalance.endDate);

		holidayBalanceService.incrementBalance(type.id, user.id);

		actualBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);
		assertEquals(QUANTITY, actualBalance.availableBalance, 0);
		assertEquals(AVAIL, actualBalance.availableBalanceUpdated, 0);
		assertEquals(this.getNextCompleteMonth() , actualBalance.beginDate);
		assertEquals(this.getNextEndDateForNewBalance() , actualBalance.endDate);
	}
	
	//******************************************************************************//
	// HasAvailableDays method
	//******************************************************************************//


	@Test
	public void balancesDoNotHaveAvailableDaysTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();
		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(false, holidayBalanceService.hasAvailableDays(type.instanceId, user.id, 1));
	}

	@Test
	public void balancesHaveAvailableDaysWithoutAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();
		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		holidayBalanceService.incrementBalance(type.id, user.id);
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		assertEquals(true, holidayBalanceService.hasAvailableDays(type.instanceId, user.id, QUANTITY));
	}

	@Test
	public void balancesDoNotHaveAvailableDaysWithAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();
		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Increment balance for year N.
		holidayBalanceService.incrementBalance(type.id, user.id);
		// Must be false because latest balance is not available due to lack of anticipation
		assertEquals(false, holidayBalanceService.hasAvailableDays(type.instanceId, user.id, QUANTITY));
	}

	@Test
	public void balancesHaveAvailableDaysWithAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();
		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Increment balance for year N.
		holidayBalanceService.incrementBalance(type.id, user.id);
		// Must be false because latest balance is not available due to lack of anticipation
		assertEquals(true, holidayBalanceService.hasAvailableDays(type.instanceId, user.id, QUANTITY));
	}
	
	//******************************************************************************//
	// RemoveAvailableDays method
	//******************************************************************************//

	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableDaysWhenNotEnoughDaysTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		holidayBalanceService.removeAvailableDays(type.instanceId, user.id, QUANTITY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableDaysWithWrongDaysValueTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		float wrongDays =  QUANTITY+0.001F;
		holidayBalanceService.removeAvailableDays(type.instanceId, user.id, wrongDays);
	}

	@Test
	public void removeAvailableDaysWithEnoughDaysInOneBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.instanceId, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		holidayBalanceService.removeAvailableDays(type.instanceId, user.id, QUANTITY);

		// We're in a non anticipated type, and getHolidayBalancesAvailable return balances in ascending order date. So index 0 => m2yBalance and 1 => m1yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.instanceId, user.id)).get(0);

		assertEquals(QUANTITY, m2yBalance.availableBalance, 0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(2).toDate() , m2yBalance.endDate);

	}

	@Test
	public void removeAvailableDaysWithEnoughDaysInSeveralBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m1yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m1yBalance.availableBalance,0);
		assertEquals(AVAIL, m1yBalance.availableBalanceUpdated,0);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		holidayBalanceService.removeAvailableDays(type.id, user.id, QUANTITY*3);

		// We're in a non anticipated type. So index 0 => m1yBalance and 1 => m2yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		m1yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(1);
		assertEquals(0, m2yBalance.availableBalance, 0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated, 0);

		assertEquals(QUANTITY, m1yBalance.availableBalance, 0);
		assertEquals(AVAIL, m1yBalance.availableBalanceUpdated, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableDaysWithNotEnoughDaysDueToNoAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO newestBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, newestBalance.availableBalance,0);
		assertEquals(AVAIL, newestBalance.availableBalanceUpdated,0);

		holidayBalanceService.removeAvailableDays(type.id, user.id, QUANTITY*3);
	}

	@Test
	public void removeAvailableDaysWithEnoughDaysWithAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO newestBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, newestBalance.availableBalance,0);
		assertEquals(AVAIL, newestBalance.availableBalanceUpdated,0);

		holidayBalanceService.removeAvailableDays(type.id, user.id, QUANTITY*3);

		// We're in a non anticipated type. So index 0 => m1yBalance and 1 => m2yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		newestBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(2);
		assertEquals(0, m2yBalance.availableBalance, 0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated, 0);

		assertEquals(QUANTITY, newestBalance.availableBalance, 0);
		assertEquals(AVAIL, newestBalance.availableBalanceUpdated, 0);
	}

	//******************************************************************************//
	// RemoveDaysInAvailableUpdatedFromRequest method
	//******************************************************************************//
	
	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableTemporaryDaysWhenNotEnoughDaysTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL * 4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableTemporaryDaysWithWrongDaysValueTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		float wrongDays =  QUANTITY+0.001F;
		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, wrongDays);
	}

	@Test
	public void removeAvailableTemporaryDaysWithEnoughDaysInOneBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, QUANTITY);

		// We're in a non anticipated type, and getHolidayBalancesAvailable return balances in ascending order date. So index 0 => m2yBalance and 1 => m1yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);

		assertEquals(QUANTITY*2, m2yBalance.availableBalance, 0);
		assertEquals(AVAIL - QUANTITY, m2yBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(2).toDate() , m2yBalance.endDate);

	}

	@Test
	public void removeAvailableTemporaryDaysWithEnoughDaysInOneBalanceBisTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, ((QUANTITY * 2) - 0.5F));

		// We're in a non anticipated type, and getHolidayBalancesAvailable return balances in ascending order date. So index 0 => m2yBalance and 1 => m1yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);

		assertEquals(QUANTITY*2, m2yBalance.availableBalance, 0);
		assertEquals(AVAIL - ((QUANTITY * 2) - 0.5F), m2yBalance.availableBalanceUpdated, 0);
		assertEquals(new DateTime(this.getNextCompleteMonth()).minusYears(2).toDate() , m2yBalance.beginDate);
		assertEquals(new DateTime(END_DATE_ALL_MONTH_TYPE_CURRENT_YEAR).minusYears(2).toDate() , m2yBalance.endDate);

	}

	@Test
	public void removeAvailableTemporaryDaysWithEnoughDaysInSeveralBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m1yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m1yBalance.availableBalance,0);
		assertEquals(AVAIL, m1yBalance.availableBalanceUpdated,0);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);
		
		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL*1.5F);

		// We're in a non anticipated type. So index 0 => m1yBalance and 1 => m2yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		m1yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(1);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance, 0);
		assertEquals(0, m2yBalance.availableBalanceUpdated, 0);

		assertEquals(QUANTITY*2, m1yBalance.availableBalance, 0);
		assertEquals(AVAIL/2, m1yBalance.availableBalanceUpdated, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableTemporaryDaysWithNotEnoughDaysDueToNoAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO newestBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, newestBalance.availableBalance,0);
		assertEquals(AVAIL, newestBalance.availableBalanceUpdated,0);

		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL*3);
	}

	@Test
	public void removeAvailableTemporaryDaysWithEnoughDaysWithAnticipationTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO m2yBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance,0);
		assertEquals(AVAIL, m2yBalance.availableBalanceUpdated,0); 

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		// Increment this balance with QUANTITY two times
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		HolidayBalanceDTO newestBalance = holidayBalanceService.getProcessedBalance(type.id, user.id);
		assertEquals(QUANTITY*2, newestBalance.availableBalance,0);
		assertEquals(AVAIL, newestBalance.availableBalanceUpdated,0);

		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL*3);

		// We're in a non anticipated type. So index 0 => m1yBalance and 1 => m2yBalance
		m2yBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		newestBalance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(2);
		assertEquals(QUANTITY*2, m2yBalance.availableBalance, 0);
		assertEquals(0, m2yBalance.availableBalanceUpdated, 0);

		assertEquals(QUANTITY*2, newestBalance.availableBalance, 0);
		assertEquals(0, newestBalance.availableBalanceUpdated, 0);
	}

	//******************************************************************************//
	// AddDaysInAvailableUpdatedFromRequest method
	//******************************************************************************//
	@Test(expected = IllegalArgumentException.class)
	public void addDaysInAvailableUpdatedWithWrongValueTest(){
		UserDTO user = createUserOldHiredForTest();
		// With non anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		holidayBalanceService.createHolidayBalance(type.id, user.id);

		float wrongDays =  QUANTITY+0.001F;
		holidayBalanceService.addDaysInAvailableUpdatedFromRequest(type.id, user.id, wrongDays);
	}

	@Test
	public void addDaysInAvailableUpdatedWithEnoughSpaceInFirstBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		holidayBalanceService.createHolidayBalance(type.id, user.id);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);
		
		// Clean balances
		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL*3);

		holidayBalanceService.addDaysInAvailableUpdatedFromRequest(type.id, user.id, QUANTITY);

		// We're in a non anticipated type, and getHolidayBalancesAvailable return balances in ascending order date. So index 0 => m2yBalance and 1 => m1yBalance
		List<HolidayBalanceDTO> balances = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id));
		actual = balances.get(balances.size()-1);
		assertEquals(0, actual.availableBalance, 0);
		assertEquals(QUANTITY, actual.availableBalanceUpdated, 0);
	}

	@Test
	public void addDaysInAvailableUpdatedWithEnoughSpaceInSeveralBalanceTest(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		//Balance for the year N - 2
		holidayBalanceService.createHolidayBalance(type.id, user.id, DateTime.now().minusYears(2).getYear());
		//Balance for the year N - 1
		HolidayBalanceDTO m1yBalance = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, m1yBalance.availableBalance,0);
		assertEquals(AVAIL, m1yBalance.availableBalanceUpdated,0);

		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);
		
		// Clean balances
		holidayBalanceService.removeDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL * 3);
		
		holidayBalanceService.addDaysInAvailableUpdatedFromRequest(type.id, user.id, AVAIL *1.5F);

		// We're in a non anticipated type, and getHolidayBalancesAvailable return balances in ascending order date. So index 0 => m2yBalance and 1 => m1yBalance
		List<HolidayBalanceDTO> balances = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id));
		actual = balances.get(balances.size()-1);
		m1yBalance = balances.get(balances.size()-2);
		assertEquals(0, actual.availableBalance, 0);
		assertEquals(AVAIL, actual.availableBalanceUpdated, 0);
		assertEquals(0, m1yBalance.availableBalance, 0);
		assertEquals(AVAIL/2, m1yBalance.availableBalanceUpdated, 0);

	}

	//******************************************************************************//
	// RemovePastHolidays method
	//******************************************************************************//
	@Test
	public void removePastHolidaysWithoutEffect(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);

		holidayBalanceService.removePastHolidays();

		// We're in a non anticipated type. So index 0 => m1yBalance and 1 => m2yBalance
		HolidayBalanceDTO balance = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);


		assertEquals(0,balance.availableBalance,0);
		assertEquals(AVAIL,balance.availableBalanceUpdated,0);
	}


	@Test
	public void removePastHolidays(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME, managed);

		authenticateAs(USERNAME);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);

		// Get some holidays
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2,actual.availableBalance,0);
		assertEquals(AVAIL,actual.availableBalanceUpdated,0);

		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TODAY).toString(fmt);
		detailDTO2.day = new DateTime(DATE_TOMORROW).toString(fmt);

		detailDTO1.typeInstanceId = type.id;
		detailDTO2.typeInstanceId = type.id;

		detailDTO1.am = true;
		detailDTO1.pm = true;
		detailDTO2.am = true;
		detailDTO2.pm = false;



		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = "";

		requestService.registerRequestAndDetails(request);

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2,actual.availableBalance,0);
		assertEquals(AVAIL - 1.5F,actual.availableBalanceUpdated,0);

		// Get and accept request
		List<HolidayRequestDTO> dtos = requestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		requestService.acceptRequest(dtos.get(0).id);

		// Remove holidays
		holidayBalanceService.removePastHolidays();

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2 - 1.5F,actual.availableBalance,0); // The user has take "today" complete and "tomorrow" morning
		assertEquals(AVAIL - 1.5F,actual.availableBalanceUpdated,0);
	}

	@Test
	public void removePastHolidaysTwoTimes(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME, managed);

		authenticateAs(USERNAME);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);

		// Get some holidays
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2,actual.availableBalance,0);
		assertEquals(AVAIL,actual.availableBalanceUpdated,0);

		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TODAY).toString(fmt);
		detailDTO2.day = new DateTime(DATE_TOMORROW).toString(fmt);

		detailDTO1.typeInstanceId = type.id;
		detailDTO2.typeInstanceId = type.id;

		detailDTO1.am = true;
		detailDTO1.pm = true;

		detailDTO2.am = true;

		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);
		list.add(detailDTO2);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = "";

		requestService.registerRequestAndDetails(request);

		// Get and accept request
		List<HolidayRequestDTO> dtos = requestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		requestService.acceptRequest(dtos.get(0).id);

		// Remove holidays
		holidayBalanceService.removePastHolidays();

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2 - 1.5,actual.availableBalance,0); // The user has take "today" complete and "tomorrow" morning
		assertEquals(AVAIL - 1.5,actual.availableBalanceUpdated,0); // The user has take "today" complete and "tomorrow" morning


		// Remove holidays a second time
		holidayBalanceService.removePastHolidays();

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2 - 1.5,actual.availableBalance,0); // Nothing changed
		assertEquals(AVAIL - 1.5,actual.availableBalanceUpdated,0); 
	}

	@Test
	public void notRemoveTomorrowRequest(){
		UserDTO user = createUserOldHiredForTest();
		// With anticipated holiday type
		HolidayDTO type = createHolidayTypeAllMonthAnticipatedForTest();

		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME, managed);

		authenticateAs(USERNAME);
		//Balance for the year N => Anticipated balance in that case of holiday type.
		HolidayBalanceDTO actual = holidayBalanceService.createHolidayBalance(type.id, user.id);
		assertEquals(0, actual.availableBalance,0);
		assertEquals(AVAIL, actual.availableBalanceUpdated,0);

		// Get some holidays
		holidayBalanceService.incrementBalance(type.id, user.id);
		holidayBalanceService.incrementBalance(type.id, user.id);

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2,actual.availableBalance,0);
		assertEquals(AVAIL,actual.availableBalanceUpdated,0);

		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TOMORROW).toString(fmt);
		
		detailDTO1.typeInstanceId = type.id;

		detailDTO1.am = true;

		List<HolidayDetailCreationRequestDTO> list = new ArrayList<HolidayDetailCreationRequestDTO>();
		list.add(detailDTO1);

		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		request.details = list;
		request.requesterComment = "";

		requestService.registerRequestAndDetails(request);

		// Get and accept request
		List<HolidayRequestDTO> dtos = requestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		requestService.acceptRequest(dtos.get(0).id);

		// Remove holidays
		holidayBalanceService.removePastHolidays();

		actual = new ArrayList<HolidayBalanceDTO>(holidayBalanceService.getHolidayBalancesAvailable(type.id, user.id)).get(0);
		assertEquals(QUANTITY*2,actual.availableBalance,0); // Nothing changed
		assertEquals(AVAIL - 0.5,actual.availableBalanceUpdated,0);

	}

}
