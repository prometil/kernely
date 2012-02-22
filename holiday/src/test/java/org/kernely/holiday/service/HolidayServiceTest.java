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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.dto.RoleDTO;
import org.kernely.core.dto.UserCreationRequestDTO;
import org.kernely.core.dto.UserDTO;
import org.kernely.core.dto.UserDetailsDTO;
import org.kernely.core.model.Role;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayProfileCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayServiceTest extends AbstractServiceTest {

	private static final String TYPE_1 = "type1";
	private static final String TYPE_2 = "type2";
	private static final String NAME_1 = "name1";
	private static final String NAME_2 = "name2";
	
	private static final int QUANTITY_1 = 25;
	private static final int QUANTITY_2 = 12;
	private static final String COLOR_1 = "#FFFFFF";
	private static final String COLOR_2 = "#000000";

	@Inject
	private HolidayService holidayService;

	@Inject
	private UserService userService;
	
	@Inject
	private RoleService roleService;

	private UserDTO creationOfTestUser(String username) {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
		
		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = username;
		request.password = username;
		request.firstname = username;
		request.lastname = username;
		return userService.createUser(request);
	}
	
	@Test
	public void getHolidayDTOTest(){
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.name = TYPE_1;
		cdto.quantity = QUANTITY_1;
		holidayService.createOrUpdateHoliday(cdto);
		HolidayDTO hdto = holidayService.getAllHoliday().get(0);
		assertEquals(TYPE_1, holidayService.getHolidayDTO(hdto.id).name);
		assertEquals(QUANTITY_1, holidayService.getHolidayDTO(hdto.id).quantity);
	}
	
	@Test
	public void creationHoliday() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.name = TYPE_1;
		cdto.quantity = QUANTITY_1;
		holidayService.createOrUpdateHoliday(cdto);
		HolidayDTO hdto = holidayService.getAllHoliday().get(0);
		assertEquals(TYPE_1, hdto.name);
		assertEquals(QUANTITY_1, hdto.quantity, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayWithNullRequest() {
		holidayService.createOrUpdateHoliday(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayWithNullType() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.name = null;
		holidayService.createOrUpdateHoliday(cdto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayWithVoidType() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.name = "  ";
		holidayService.createOrUpdateHoliday(cdto);
	}

	@Test
	public void updateHolidayType() {
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();

		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.APRIL;
		creation.quantity = QUANTITY_1;
		creation.unity = HolidayType.PERIOD_MONTH;
		creation.anticipation = false;
		creation.color = COLOR_1;
		
		HolidayDTO holidayType = holidayService.createOrUpdateHoliday(creation);
		
		assertEquals(TYPE_1,holidayType.name);
		assertEquals(false,holidayType.unlimited);
		assertEquals(HolidayType.APRIL,holidayType.effectiveMonth);
		assertEquals(QUANTITY_1,holidayType.quantity);
		assertEquals(HolidayType.PERIOD_MONTH,holidayType.periodUnit);
		assertEquals(false,holidayType.anticipation);
		assertEquals(COLOR_1,holidayType.color);
		
		HolidayCreationRequestDTO update = new HolidayCreationRequestDTO();
		
		update.name = TYPE_2;
		update.unlimited = false;
		update.effectiveMonth = HolidayType.FEBRUARY;
		update.quantity = QUANTITY_2;
		update.unity = HolidayType.PERIOD_YEAR;
		update.anticipation = true;
		update.color = COLOR_2;
		
		holidayType = holidayService.createOrUpdateHoliday(update);

		assertEquals(TYPE_2,holidayType.name);
		assertEquals(false,holidayType.unlimited);
		assertEquals(HolidayType.FEBRUARY,holidayType.effectiveMonth);
		assertEquals(QUANTITY_2,holidayType.quantity);
		assertEquals(HolidayType.PERIOD_YEAR,holidayType.periodUnit);
		assertEquals(true,holidayType.anticipation);
		assertEquals(COLOR_2,holidayType.color);
		
	}


	@Test
	public void deleteHoliday() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.name = TYPE_1;
		holidayService.createOrUpdateHoliday(cdto);
		HolidayDTO hdto = new HolidayDTO();
		hdto = holidayService.getAllHoliday().get(0);

		holidayService.deleteHoliday(hdto.id);
		assertEquals(0, holidayService.getAllHoliday().size());
	}
	
	@Test
	public void createHolidayProfile(){
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.APRIL;
		creation.quantity = QUANTITY_1;
		creation.unity = HolidayType.PERIOD_MONTH;
		creation.anticipation = false;
		creation.color = COLOR_1;
		
		HolidayDTO holidayType1 = holidayService.createOrUpdateHoliday(creation);
		creation.name = TYPE_2;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.FEBRUARY;
		creation.quantity = QUANTITY_2;
		creation.unity = HolidayType.PERIOD_YEAR;
		creation.anticipation = true;
		creation.color = COLOR_2;
		
		HolidayDTO holidayType2 = holidayService.createOrUpdateHoliday(creation);
		
		HolidayProfileCreationRequestDTO profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_1;
		List<Integer> typesId = new ArrayList<Integer>();
		typesId.add(holidayType1.id);
		typesId.add(holidayType2.id);
		profileCreation.holidayTypesId = typesId;
		
		HolidayProfileDTO profile = holidayService.createOrUpdateHolidayProfile(profileCreation);
		
		assertEquals(NAME_1,profile.name);
		assertEquals(2,profile.holidayTypes.size());
	}
	
	@Test
	public void createTwoHolidayProfile(){
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.APRIL;
		creation.quantity = QUANTITY_1;
		creation.unity = HolidayType.PERIOD_MONTH;
		creation.anticipation = false;
		creation.color = COLOR_1;
		
		HolidayDTO holidayType1 = holidayService.createOrUpdateHoliday(creation);
		creation.name = TYPE_2;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.FEBRUARY;
		creation.quantity = QUANTITY_2;
		creation.unity = HolidayType.PERIOD_YEAR;
		creation.anticipation = true;
		creation.color = COLOR_2;
		
		HolidayDTO holidayType2 = holidayService.createOrUpdateHoliday(creation);
		
		// Profile with only the first type
		HolidayProfileCreationRequestDTO profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_1;
		List<Integer> typesId = new ArrayList<Integer>();
		typesId.add(holidayType1.id);
		profileCreation.holidayTypesId = typesId;
		
		HolidayProfileDTO profile1 = holidayService.createOrUpdateHolidayProfile(profileCreation);
		
		// Profile with only the second type
		profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_2;
		typesId = new ArrayList<Integer>();
		typesId.add(holidayType2.id);
		profileCreation.holidayTypesId = typesId;
		
		HolidayProfileDTO profile2 = holidayService.createOrUpdateHolidayProfile(profileCreation);
		
		assertEquals(NAME_1,profile1.name);
		assertEquals(1,profile1.holidayTypes.size());

		assertEquals(NAME_2,profile2.name);
		assertEquals(1,profile1.holidayTypes.size());

		
	}
	
	@Test
	public void updateProfileUsersTest(){
		this.creationOfTestUser(NAME_2);
		
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = HolidayType.APRIL;
		creation.quantity = QUANTITY_1;
		creation.unity = HolidayType.PERIOD_MONTH;
		creation.anticipation = false;
		creation.color = COLOR_1;
		
		HolidayDTO holidayType1 = holidayService.createOrUpdateHoliday(creation);
		
		HolidayProfileCreationRequestDTO profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_1;
		List<Integer> typesId = new ArrayList<Integer>();
		typesId.add(holidayType1.id);
		profileCreation.holidayTypesId = typesId;
		
		HolidayProfileDTO profile = holidayService.createOrUpdateHolidayProfile(profileCreation);
		
		// Add users
		List<String> usernames = new ArrayList<String>();
		usernames.add(NAME_2);
		holidayService.updateProfileUsers(profile.id, usernames);
		
		List<UserDetailsDTO> users = holidayService.getUsersInProfile(profile.id);
		assertEquals(1,users.size());
		assertEquals(NAME_2,users.get(0).user.username);
	}

}
