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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	
	private static final Date DATE_TODAY = new DateTime().withZone(DateTimeZone.UTC).toDate();
	private static final Date DATE_TOMORROW = new DateTime().plusDays(1).withZone(DateTimeZone.UTC).toDate();

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

	private HolidayDTO createHolidayTypeForTest() {
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		return holidayService.createHoliday(request);
	}

	private UserDTO createUserForTest() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = USERNAME;
		request.password = TEST_STRING;
		return userService.createUser(request);
	}

	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceWithNullUser(){
		HolidayDTO type = createHolidayTypeForTest();
		holidayBalanceService.createHolidayBalance(Long.valueOf(0), type.id);		
	}

	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceWithNullType(){
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, 0);		
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void createHolidayBalanceAlreadyExist(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayDTO type2 = createHolidayTypeForTest();
		UserDTO user2 = createUserForTest();
		holidayBalanceService.createHolidayBalance(user2.id, type2.id);		
	}
		
	@Test
	public void getHolidayBalance() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(0, balance.availableBalance, 0);
		assertEquals(0, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getHolidayBalanceUserNull(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		holidayBalanceService.getHolidayBalance(user.id+1, type.id);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void getHolidayBalanceTypeNull(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		holidayBalanceService.getHolidayBalance(user.id, type.id+1);		
	}

	@Test
	public void getAllHolidayBalances() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();

		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getAllHolidayBalances().get(0);
		assertEquals(0, balance.availableBalance, 0);
		assertEquals(0, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);
	}

	@Test
	public void incrementSpecificMonthHoliday() {
		UserDTO user = createUserForTest();

		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.FEBRUARY;
		holidayService.createHoliday(request);
		HolidayDTO specificHoliday = holidayService.getAllHoliday().get(0);

		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, specificHoliday.id);

		assertEquals(0F, balance.availableBalance, 0);
		assertEquals(0F, balance.availableBalanceUpdated, 0);
		assertEquals(0F, balance.futureBalance, 0);

		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, specificHoliday.id);

		assertEquals(0, balance.availableBalance, 0);
		assertEquals(0, balance.availableBalanceUpdated, 0);
		assertEquals(QUANTITY, balance.futureBalance, 0);
	}

	@Test
	public void incrementMonthlyHoliday() {
		UserDTO user = createUserForTest();

		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		holidayService.createHoliday(request);
		HolidayDTO monthlyHoliday = holidayService.getAllHoliday().get(0);

		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, monthlyHoliday.id);

		assertEquals(0F, balance.availableBalance, 0);
		assertEquals(0F, balance.availableBalanceUpdated, 0);
		assertEquals(0F, balance.futureBalance, 0);

		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, monthlyHoliday.id);

		assertEquals(QUANTITY, balance.availableBalance, 0);
		assertEquals(QUANTITY, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);
	}

	@Test
	public void incrementMonthlyHolidayMassively() {
		UserDTO user = createUserForTest();

		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		holidayService.createHoliday(request);
		HolidayDTO monthlyHoliday = holidayService.getAllHoliday().get(0);

		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, monthlyHoliday.id);

		assertEquals(0F, balance.availableBalance, 0);
		assertEquals(0F, balance.availableBalanceUpdated, 0);
		assertEquals(0F, balance.futureBalance, 0);

		for (int i = 0; i < 1000; i++){
			holidayBalanceService.incrementBalance(balance.id);
		}

		balance = holidayBalanceService.getHolidayBalance(user.id, monthlyHoliday.id);

		assertEquals(QUANTITY*1000, balance.availableBalance, 0);
		assertEquals(QUANTITY*1000, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);
	}
	

	@Test
	public void incrementUnlimitedMonthlyHolidayBalance() {
		UserDTO user = createUserForTest();

		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.unlimited = true;
		HolidayDTO unlimitedHoliday = holidayService.createHoliday(request);

		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, unlimitedHoliday.id);

		assertEquals(0, balance.availableBalance, 0);
		assertEquals(0, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);
		
		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, unlimitedHoliday.id);

		assertEquals(0, balance.availableBalance, 0);
		assertEquals(0, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);
	}
	
	@Test
	public void transferFutureToAvailableBalance() {
		UserDTO user = createUserForTest();

		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.FEBRUARY;

		holidayService.createHoliday(request);
		HolidayDTO holidayType = holidayService.getAllHoliday().get(0);

		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, holidayType.id);

		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.transferFutureBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, holidayType.id);

		assertEquals(QUANTITY * 3, balance.availableBalance, 0);
		assertEquals(QUANTITY * 3, balance.availableBalanceUpdated, 0);
		assertEquals(0, balance.futureBalance, 0);

	}

	@Test
	public void hasAvailableDays() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		assertFalse(holidayBalanceService.hasAvailableDays(balance.id, QUANTITY));

		holidayBalanceService.incrementBalance(balance.id);
		assertTrue(holidayBalanceService.hasAvailableDays(balance.id, QUANTITY));
		holidayBalanceService.incrementBalance(balance.id);
		assertTrue(holidayBalanceService.hasAvailableDays(balance.id, QUANTITY));
	}

	@Test
	public void removeAvailableDays() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.incrementBalance(balance.id);
		
		assertEquals(QUANTITY * 3, holidayBalanceService.getHolidayBalanceDTO(balance.id).availableBalance, 0);
		assertEquals(QUANTITY * 3, holidayBalanceService.getHolidayBalanceDTO(balance.id).availableBalanceUpdated, 0);

		holidayBalanceService.removeAvailableDays(balance.id, 2);
		assertEquals(QUANTITY * 3 - 2, holidayBalanceService.getHolidayBalanceDTO(balance.id).availableBalance, 0);
		assertEquals(QUANTITY * 3, holidayBalanceService.getHolidayBalanceDTO(balance.id).availableBalanceUpdated, 0);

		holidayBalanceService.removeAvailableDays(balance.id, 4.5F);
		assertEquals(QUANTITY * 3 - 6.5, holidayBalanceService.getHolidayBalanceDTO(balance.id).availableBalance, 0);
		assertEquals(QUANTITY * 3, holidayBalanceService.getHolidayBalanceDTO(balance.id).availableBalanceUpdated, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeNotAvailableDays() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		holidayBalanceService.removeAvailableDays(balance.id, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void removeAvailableDaysWithWrongFormat() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		holidayBalanceService.removeAvailableDays(balance.id, 1.2F);
	}
	
	@Test
	public void addAndRemoveAvailableDays(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		
		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.removeAvailableDays(balance.id, QUANTITY);

		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		assertEquals(0,balance.availableBalance,0);
		assertEquals(QUANTITY,balance.availableBalanceUpdated,0);
		assertEquals(0,balance.futureBalance,0);
	}
	
	@Test
	public void removePastHolidaysWithoutEffect(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);
		
		holidayBalanceService.removePastHolidays();
		
		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);
	}
	
	
	@Test
	public void removePastHolidays(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME, managed);

		authenticateAs(USERNAME);
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.availableBalanceUpdated,0);
		assertEquals(0,balance.futureBalance,0);

		// Get some holidays
		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY,balance.availableBalance,0);
		assertEquals(QUANTITY,balance.availableBalanceUpdated,0);
		assertEquals(0,balance.futureBalance,0);
		
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TODAY).toString(fmt);
		detailDTO2.day = new DateTime(DATE_TOMORROW).toString(fmt);

		detailDTO1.typeId = type.id;
		detailDTO2.typeId = type.id;
		
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
		
		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY,balance.availableBalance,0);
		assertEquals(QUANTITY - 1.5,balance.availableBalanceUpdated,0);
		assertEquals(0,balance.futureBalance,0);
		
		// Get and accept request
		List<HolidayRequestDTO> dtos = requestService.getAllRequestsWithStatus(HolidayRequest.PENDING_STATUS);
		requestService.acceptRequest(dtos.get(0).id);

		// Remove holidays
		holidayBalanceService.removePastHolidays();
		
		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY - 1.5,balance.availableBalance,0); // The user has take "today" complete and "tomorrow" morning
		assertEquals(QUANTITY - 1.5,balance.availableBalanceUpdated,0);
		assertEquals(0,balance.futureBalance,0);
	}
	
	@Test
	public void removePastHolidaysTwoTimes(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME, managed);

		authenticateAs(USERNAME);
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);

		// Get some holidays
		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);
		
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detailDTO2 = new HolidayDetailCreationRequestDTO();

		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TODAY).toString(fmt);
		detailDTO2.day = new DateTime(DATE_TOMORROW).toString(fmt);

		detailDTO1.typeId = type.id;
		detailDTO2.typeId = type.id;
		
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
		
		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY - 1.5,balance.availableBalance,0); // The user has take "today" complete and "tomorrow" morning
		assertEquals(0,balance.futureBalance,0);
		
		// Remove holidays a second time
		holidayBalanceService.removePastHolidays();
		
		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY - 1.5,balance.availableBalance,0); // Nothing should have change
		assertEquals(0,balance.futureBalance,0);
	}
	
	@Test
	public void notRemoveTomorrowRequest(){
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		
		List<UserDTO> managed = new ArrayList<UserDTO>();
		managed.add(user);
		userService.updateManager(USERNAME, managed);

		authenticateAs(USERNAME);
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);

		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);

		// Get some holidays
		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);
		
		HolidayDetailCreationRequestDTO detailDTO1 = new HolidayDetailCreationRequestDTO();
		
		DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
		detailDTO1.day = new DateTime(DATE_TOMORROW).toString(fmt);

		detailDTO1.typeId = type.id;
		
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
		
		balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(QUANTITY,balance.availableBalance,0); // Nothing should have been decremented
		assertEquals(0,balance.futureBalance,0);
	}
	
}
