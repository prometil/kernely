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
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayBalanceServiceTest extends AbstractServiceTest {

	private static final String TEST_STRING = "type";
	private static final int QUANTITY = 3;
	
	@Inject
	private HolidayBalanceService holidayBalanceService;
	
	@Inject
	private HolidayService holidayService;
	
	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	private HolidayDTO createHolidayTypeForTest(){
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		holidayService.createHoliday(request);
		return holidayService.getAllHoliday().get(0);
	}
	
	private UserDTO createUserForTest(){
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);

		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = TEST_STRING;
		request.password = TEST_STRING;
		userService.createUser(request);
		return userService.getAllUsers().get(0);
	}

	@Test
	public void getHolidayBalance() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, type.id);
		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);
	}
	
	@Test
	public void getAllHolidayBalances() {
		HolidayDTO type = createHolidayTypeForTest();
		UserDTO user = createUserForTest();
		
		holidayBalanceService.createHolidayBalance(user.id, type.id);
		HolidayBalanceDTO balance = holidayBalanceService.getAllHolidayBalances().get(0);
		assertEquals(0,balance.availableBalance,0);
		assertEquals(0,balance.futureBalance,0);
	}
	
	@Test
	public void incrementSpecificMonthHoliday(){
		UserDTO user = createUserForTest();
		
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.FEBRUARY;
		holidayService.createHoliday(request);
		HolidayDTO specificHoliday = holidayService.getAllHoliday().get(0);
		
		holidayBalanceService.createHolidayBalance(user.id, specificHoliday.id);
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, specificHoliday.id);

		assertEquals(0F, balance.availableBalance,0);
		assertEquals(0F, balance.futureBalance,0);
		
		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, specificHoliday.id);
		
		assertEquals(0, balance.availableBalance);
		assertEquals(QUANTITY*12, balance.futureBalance);
	}
	
	@Test
	public void incrementMonthlyHoliday(){
		UserDTO user = createUserForTest();
		
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.ALL_MONTH;
		holidayService.createHoliday(request);
		HolidayDTO monthlyHoliday = holidayService.getAllHoliday().get(0);
		
		holidayBalanceService.createHolidayBalance(user.id, monthlyHoliday.id);
		
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, monthlyHoliday.id);

		assertEquals(0F, balance.availableBalance,0);
		assertEquals(0F, balance.futureBalance,0);
		
		holidayBalanceService.incrementBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, monthlyHoliday.id);

		assertEquals(QUANTITY*12, balance.availableBalance);
		assertEquals(0,balance.futureBalance);
	}

	@Test
	public void transferFutureToAvailableBalance(){
		UserDTO user = createUserForTest();
		
		HolidayCreationRequestDTO request = new HolidayCreationRequestDTO();
		request.type = TEST_STRING;
		request.quantity = QUANTITY;
		request.unity = HolidayType.PERIOD_MONTH;
		request.effectiveMonth = HolidayType.FEBRUARY;

		holidayService.createHoliday(request);
		HolidayDTO holidayType = holidayService.getAllHoliday().get(0);
		
		holidayBalanceService.createHolidayBalance(user.id, holidayType.id);
		
		HolidayBalanceDTO balance = holidayBalanceService.getHolidayBalance(user.id, holidayType.id);

		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.incrementBalance(balance.id);
		holidayBalanceService.transferFutureBalance(balance.id);

		balance = holidayBalanceService.getHolidayBalance(user.id, holidayType.id);
		
		assertEquals(QUANTITY*3*12, balance.availableBalance);
		assertEquals(0, balance.futureBalance);

	}
}
