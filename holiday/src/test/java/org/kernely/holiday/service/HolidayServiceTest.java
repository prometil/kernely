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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

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
import org.kernely.holiday.dto.HolidayDetailCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileCreationRequestDTO;
import org.kernely.holiday.dto.HolidayProfileDTO;
import org.kernely.holiday.dto.HolidayProfilesSummaryDTO;
import org.kernely.holiday.dto.HolidayRequestCreationRequestDTO;
import org.kernely.holiday.dto.HolidayUserSummaryDTO;
import org.kernely.holiday.model.HolidayType;

import com.google.inject.Inject;

public class HolidayServiceTest extends AbstractServiceTest {

	private static final String TYPE_1 = "type1";
	private static final String TYPE_2 = "type2";
	private static final String TYPE_3 = "type3";
	private static final String TYPE_4 = "type4";
	
	private static final String TYPE_1_MODIFIED = "type1_modified";
	
	private static final int EFFECTIVE_MONTH = HolidayType.APRIL;
	
	private static final int EFFECTIVE_MONTH_MODIFIED = HolidayType.FEBRUARY;
	
	private static final int PERIOD_UNIT = HolidayType.PERIOD_MONTH;
	
	private static final int PERIOD_UNIT_MODIFIED = HolidayType.PERIOD_YEAR;

	private static final String NAME_1 = "name1";
	private static final String NAME_2 = "name2";
	private static final String NAME_3 = "name3";

	private static final String PROFILE_1 = "p1";
	private static final String PROFILE_2 = "p2";

	private static final int QUANTITY_1 = 25;
	private static final int QUANTITY_2 = 12;
	
	private static final int QUANTITY_1_MODIFIED = 20;
	
	private static final String COLOR_1 = "#FFFFFF";
	private static final String COLOR_2 = "#000000";

	private static final String COLOR_1_MODIFIED = "#123456";
	
	@Inject
	private HolidayService holidayService;

	@Inject
	private HolidayBalanceService balanceService;

	@Inject
	private HolidayRequestService requestService;

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	private void creationOfUserRole() {
		RoleDTO requestRole = new RoleDTO(1, Role.ROLE_USER);
		roleService.createRole(requestRole);
	}

	private UserDTO creationOfTestUser(String username) {

		UserCreationRequestDTO request = new UserCreationRequestDTO();
		request.username = username;
		request.password = username;
		request.firstname = username;
		request.lastname = username;
		request.locked = false;
		return userService.createUser(request);
	}

	@Test
	public void getHolidayDTOTest() {
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
		assertNotNull(hdto.instanceId);
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
		creation.effectiveMonth = EFFECTIVE_MONTH;
		creation.quantity = QUANTITY_1;
		creation.unity = PERIOD_UNIT;
		creation.anticipation = false;
		creation.color = COLOR_1;

		HolidayDTO holidayType = holidayService.createOrUpdateHoliday(creation);
		assertEquals(TYPE_1, holidayType.name);
		assertEquals(false, holidayType.unlimited);
		assertEquals(EFFECTIVE_MONTH, holidayType.effectiveMonth);
		assertEquals(QUANTITY_1, holidayType.quantity);
		assertEquals(PERIOD_UNIT, holidayType.periodUnit);
		assertEquals(false, holidayType.anticipation);
		assertEquals(COLOR_1, holidayType.color);
		
		HolidayCreationRequestDTO update = new HolidayCreationRequestDTO();

		update.id = holidayType.id;
		update.name = TYPE_1_MODIFIED;
		update.unlimited = false;
		update.effectiveMonth = EFFECTIVE_MONTH_MODIFIED;
		update.quantity = QUANTITY_1_MODIFIED;
		update.unity = PERIOD_UNIT_MODIFIED;
		update.anticipation = true;
		update.color = COLOR_1_MODIFIED;

		holidayType = holidayService.createOrUpdateHoliday(update);

		assertEquals(TYPE_1_MODIFIED, holidayType.name);
		assertEquals(false, holidayType.unlimited);
		assertEquals(EFFECTIVE_MONTH_MODIFIED, holidayType.effectiveMonth);
		assertEquals(QUANTITY_1_MODIFIED, holidayType.quantity);
		assertEquals(PERIOD_UNIT_MODIFIED, holidayType.periodUnit);
		assertEquals(true, holidayType.anticipation);
		assertEquals(COLOR_1_MODIFIED, holidayType.color);

	}
	
	@Test
	public void updateNameOrColorWhenNoLinksWithUserHolidayType() {
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();

		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = EFFECTIVE_MONTH;
		creation.quantity = QUANTITY_1;
		creation.unity = PERIOD_UNIT;
		creation.anticipation = false;
		creation.color = COLOR_1;

		HolidayDTO holidayType = holidayService.createOrUpdateHoliday(creation);
		
		assertEquals(TYPE_1, holidayType.name);
		assertEquals(false, holidayType.unlimited);
		assertEquals(EFFECTIVE_MONTH, holidayType.effectiveMonth);
		assertEquals(QUANTITY_1, holidayType.quantity);
		assertEquals(PERIOD_UNIT, holidayType.periodUnit);
		assertEquals(false, holidayType.anticipation);
		assertEquals(COLOR_1, holidayType.color);
		assertEquals(holidayType.instanceId, holidayType.nextInstanceId);
		
		HolidayDTO currentInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, currentInstOld.name);
		assertEquals(false, currentInstOld.unlimited);
		assertEquals(QUANTITY_1, currentInstOld.quantity);
		assertEquals(PERIOD_UNIT, currentInstOld.periodUnit);
		assertEquals(false, currentInstOld.anticipation);
		assertEquals(COLOR_1, currentInstOld.color);
		
		HolidayDTO nextInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, nextInstOld.name);
		assertEquals(false, nextInstOld.unlimited);
		assertEquals(QUANTITY_1, nextInstOld.quantity);
		assertEquals(PERIOD_UNIT, nextInstOld.periodUnit);
		assertEquals(false, nextInstOld.anticipation);
		assertEquals(COLOR_1, nextInstOld.color);
		
		HolidayCreationRequestDTO update = new HolidayCreationRequestDTO();

		update.id = holidayType.id;
		update.name = TYPE_1_MODIFIED;
		update.unlimited = false;
		update.effectiveMonth = EFFECTIVE_MONTH;
		update.quantity = QUANTITY_1;
		update.unity = PERIOD_UNIT;
		update.anticipation = false;
		update.color = COLOR_1_MODIFIED;

		HolidayDTO holidayTypeUpdated = holidayService.createOrUpdateHoliday(update);

		assertEquals(TYPE_1_MODIFIED, holidayTypeUpdated.name);
		assertEquals(false, holidayTypeUpdated.unlimited);
		assertEquals(EFFECTIVE_MONTH, holidayTypeUpdated.effectiveMonth);
		assertEquals(QUANTITY_1, holidayTypeUpdated.quantity);
		assertEquals(PERIOD_UNIT, holidayTypeUpdated.periodUnit);
		assertEquals(false, holidayTypeUpdated.anticipation);
		assertEquals(COLOR_1_MODIFIED, holidayTypeUpdated.color);
		assertEquals(holidayTypeUpdated.instanceId, holidayTypeUpdated.nextInstanceId);
		
		assertEquals(holidayType.instanceId, holidayTypeUpdated.instanceId);
		assertEquals(holidayType.nextInstanceId, holidayTypeUpdated.nextInstanceId);
		
		HolidayDTO currentInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.instanceId);
		assertEquals(TYPE_1_MODIFIED, currentInstUpd.name);
		assertEquals(false, currentInstUpd.unlimited);
		assertEquals(QUANTITY_1, currentInstUpd.quantity);
		assertEquals(PERIOD_UNIT, currentInstUpd.periodUnit);
		assertEquals(false, currentInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, currentInstUpd.color);
		
		HolidayDTO nextInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.nextInstanceId);
		assertEquals(TYPE_1_MODIFIED, nextInstUpd.name);
		assertEquals(false, nextInstUpd.unlimited);
		assertEquals(QUANTITY_1, nextInstUpd.quantity);
		assertEquals(PERIOD_UNIT, nextInstUpd.periodUnit);
		assertEquals(false, nextInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, nextInstUpd.color);
		
	}
	
	@Test
	public void updateOtherFieldsWhenNoLinksWithUserHolidayType() {
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();

		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = EFFECTIVE_MONTH;
		creation.quantity = QUANTITY_1;
		creation.unity = PERIOD_UNIT;
		creation.anticipation = false;
		creation.color = COLOR_1;

		HolidayDTO holidayType = holidayService.createOrUpdateHoliday(creation);
		assertEquals(TYPE_1, holidayType.name);
		assertEquals(false, holidayType.unlimited);
		assertEquals(EFFECTIVE_MONTH, holidayType.effectiveMonth);
		assertEquals(QUANTITY_1, holidayType.quantity);
		assertEquals(PERIOD_UNIT, holidayType.periodUnit);
		assertEquals(false, holidayType.anticipation);
		assertEquals(COLOR_1, holidayType.color);
		assertEquals(holidayType.instanceId, holidayType.nextInstanceId);
		
		HolidayDTO currentInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, currentInstOld.name);
		assertEquals(false, currentInstOld.unlimited);
		assertEquals(QUANTITY_1, currentInstOld.quantity);
		assertEquals(PERIOD_UNIT, currentInstOld.periodUnit);
		assertEquals(false, currentInstOld.anticipation);
		assertEquals(COLOR_1, currentInstOld.color);
		
		HolidayDTO nextInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, nextInstOld.name);
		assertEquals(false, nextInstOld.unlimited);
		assertEquals(QUANTITY_1, nextInstOld.quantity);
		assertEquals(PERIOD_UNIT, nextInstOld.periodUnit);
		assertEquals(false, nextInstOld.anticipation);
		assertEquals(COLOR_1, nextInstOld.color);
		
		HolidayCreationRequestDTO update = new HolidayCreationRequestDTO();

		update.id = holidayType.id;
		update.name = TYPE_1_MODIFIED;
		update.unlimited = false;
		update.effectiveMonth = EFFECTIVE_MONTH_MODIFIED;
		update.quantity = QUANTITY_1_MODIFIED;
		update.unity = PERIOD_UNIT_MODIFIED;
		update.anticipation = false;
		update.color = COLOR_1_MODIFIED;

		HolidayDTO holidayTypeUpdated = holidayService.createOrUpdateHoliday(update);

		assertEquals(TYPE_1_MODIFIED, holidayTypeUpdated.name);
		assertEquals(false, holidayTypeUpdated.unlimited);
		assertEquals(EFFECTIVE_MONTH_MODIFIED, holidayTypeUpdated.effectiveMonth);
		assertEquals(QUANTITY_1_MODIFIED, holidayTypeUpdated.quantity);
		assertEquals(PERIOD_UNIT_MODIFIED, holidayTypeUpdated.periodUnit);
		assertEquals(false, holidayTypeUpdated.anticipation);
		assertEquals(COLOR_1_MODIFIED, holidayTypeUpdated.color);
		assertEquals(holidayType.instanceId, holidayTypeUpdated.nextInstanceId);
		
		HolidayDTO currentInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.instanceId);
		assertEquals(TYPE_1_MODIFIED, currentInstUpd.name);
		assertEquals(false, currentInstUpd.unlimited);
		assertEquals(QUANTITY_1_MODIFIED, holidayTypeUpdated.quantity);
		assertEquals(PERIOD_UNIT_MODIFIED, holidayTypeUpdated.periodUnit);
		assertEquals(false, currentInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, currentInstUpd.color);
		
		HolidayDTO nextInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.nextInstanceId);
		assertEquals(TYPE_1_MODIFIED, nextInstUpd.name);
		assertEquals(false, nextInstUpd.unlimited);
		assertEquals(QUANTITY_1_MODIFIED, holidayTypeUpdated.quantity);
		assertEquals(PERIOD_UNIT_MODIFIED, holidayTypeUpdated.periodUnit);
		assertEquals(false, nextInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, nextInstUpd.color);
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
	public void createHolidayProfile() {
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
		List<Long> typesId = new ArrayList<Long>();
		typesId.add(holidayType1.id);
		typesId.add(holidayType2.id);
		profileCreation.holidayTypesId = typesId;

		HolidayProfileDTO profile = holidayService.createOrUpdateHolidayProfile(profileCreation);

		assertEquals(NAME_1, profile.name);
		assertEquals(2, profile.holidayTypes.size());
	}

	@Test
	public void createTwoHolidayProfile() {
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
		List<Long> typesId = new ArrayList<Long>();
		typesId.add(holidayType1.id);
		profileCreation.holidayTypesId = typesId;

		HolidayProfileDTO profile1 = holidayService.createOrUpdateHolidayProfile(profileCreation);

		// Profile with only the second type
		profileCreation = new HolidayProfileCreationRequestDTO();
		profileCreation.name = NAME_2;
		typesId = new ArrayList<Long>();
		typesId.add(holidayType2.id);
		profileCreation.holidayTypesId = typesId;

		HolidayProfileDTO profile2 = holidayService.createOrUpdateHolidayProfile(profileCreation);

		assertEquals(NAME_1, profile1.name);
		assertEquals(1, profile1.holidayTypes.size());

		assertEquals(NAME_2, profile2.name);
		assertEquals(1, profile1.holidayTypes.size());

	}

	@Test
	public void updateProfileUsersTest() {
		this.creationOfUserRole();
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
		List<Long> typesId = new ArrayList<Long>();
		typesId.add(holidayType1.id);
		profileCreation.holidayTypesId = typesId;

		HolidayProfileDTO profile = holidayService.createOrUpdateHolidayProfile(profileCreation);

		// Add users
		List<String> usernames = new ArrayList<String>();
		usernames.add(NAME_2);
		holidayService.updateProfileUsers(profile.id, usernames);

		List<UserDetailsDTO> users = holidayService.getUsersInProfile(profile.id);
		assertEquals(1, users.size());
		assertEquals(NAME_2, users.get(0).user.username);
	}


	@Test
	public void summaryWithProfilesWithoutRequests() {
		// Creation of users
		this.creationOfUserRole();
		UserDTO user1 = this.creationOfTestUser(NAME_1);
		UserDTO user2 = this.creationOfTestUser(NAME_2);
		UserDTO user3 = this.creationOfTestUser(NAME_3);

		// Creation of types
		HolidayDTO type1 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_1, false, QUANTITY_1, HolidayType.PERIOD_MONTH,
				HolidayType.ALL_MONTH, true, COLOR_1));
		HolidayDTO type2 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_2, false, QUANTITY_2, HolidayType.PERIOD_MONTH,
				HolidayType.JANUARY, true, COLOR_1));
		HolidayDTO type3 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_3, false, QUANTITY_1, HolidayType.PERIOD_YEAR,
				HolidayType.FEBRUARY, true, COLOR_2));
		HolidayDTO type4 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_4, false, QUANTITY_2, HolidayType.PERIOD_YEAR,
				HolidayType.DECEMBER, true, COLOR_2));

		// First profile, with 3 types and 1 users
		List<Long> profile1Types = new ArrayList<Long>();
		profile1Types.add(type1.id);
		profile1Types.add(type2.id);
		profile1Types.add(type3.id);

		List<String> profile1Usernames = new ArrayList<String>();
		profile1Usernames.add(user1.username);

		HolidayProfileCreationRequestDTO profileRequest = new HolidayProfileCreationRequestDTO();
		profileRequest.name = PROFILE_1;
		profileRequest.holidayTypesId = profile1Types;

		HolidayProfileDTO profile1 = holidayService.createOrUpdateHolidayProfile(profileRequest);

		holidayService.updateProfileUsers(profile1.id, profile1Usernames);

		// Second profile, with 1 type and 2 users
		List<Long> profile2Types = new ArrayList<Long>();
		profile1Types.add(type4.id);

		List<String> profile2Usernames = new ArrayList<String>();
		profile2Usernames.add(user2.username);
		profile2Usernames.add(user3.username);

		profileRequest = new HolidayProfileCreationRequestDTO();
		profileRequest.name = PROFILE_2;
		profileRequest.holidayTypesId = profile2Types;

		HolidayProfileDTO profile2 = holidayService.createOrUpdateHolidayProfile(profileRequest);

		holidayService.updateProfileUsers(profile2.id, profile2Usernames);

		List<HolidayProfilesSummaryDTO> summary = holidayService.getSummmaryForAllProfiles(1, 2000);
		assertEquals(2, summary.size());
	}

	@Test
	public void summaryWithProfilesWithRequests() {
		// Creation of users
		this.creationOfUserRole();
		UserDTO user1 = this.creationOfTestUser(NAME_1);
		UserDTO user2 = this.creationOfTestUser(NAME_2);
		UserDTO user3 = this.creationOfTestUser(NAME_3);

		// Creation of types
		HolidayDTO type1 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_1, false, QUANTITY_1, HolidayType.PERIOD_MONTH,
				HolidayType.ALL_MONTH, true, COLOR_1));
		HolidayDTO type2 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_2, false, QUANTITY_2, HolidayType.PERIOD_MONTH,
				HolidayType.JANUARY, true, COLOR_1));
		HolidayDTO type3 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_3, false, QUANTITY_1, HolidayType.PERIOD_YEAR,
				HolidayType.FEBRUARY, true, COLOR_2));
		HolidayDTO type4 = holidayService.createOrUpdateHoliday(new HolidayCreationRequestDTO(TYPE_4, false, QUANTITY_2, HolidayType.PERIOD_YEAR,
				HolidayType.DECEMBER, true, COLOR_2));

		// First profile, with 3 types and 1 user
		List<Long> profile1Types = new ArrayList<Long>();
		profile1Types.add(type1.id);
		profile1Types.add(type2.id);
		profile1Types.add(type3.id);

		List<String> profile1Usernames = new ArrayList<String>();
		profile1Usernames.add(user1.username);

		HolidayProfileCreationRequestDTO profileRequest = new HolidayProfileCreationRequestDTO();
		profileRequest.name = PROFILE_1;
		profileRequest.holidayTypesId = profile1Types;

		HolidayProfileDTO profile1 = holidayService.createOrUpdateHolidayProfile(profileRequest);

		holidayService.updateProfileUsers(profile1.id, profile1Usernames);

		// Second profile, with 1 type and 2 users
		List<Long> profile2Types = new ArrayList<Long>();
		profile2Types.add(type4.id);

		List<String> profile2Usernames = new ArrayList<String>();
		profile2Usernames.add(user2.username);
		profile2Usernames.add(user3.username);

		profileRequest = new HolidayProfileCreationRequestDTO();
		profileRequest.name = PROFILE_2;
		profileRequest.holidayTypesId = profile2Types;

		HolidayProfileDTO profile2 = holidayService.createOrUpdateHolidayProfile(profileRequest);

		holidayService.updateProfileUsers(profile2.id, profile2Usernames);

		// Increment balances
		balanceService.incrementBalance(type1.id,user1.id);
		balanceService.incrementBalance(type2.id,user1.id);
		balanceService.incrementBalance(type3.id,user1.id);
		balanceService.incrementBalance(type4.id,user2.id);
		balanceService.incrementBalance(type4.id,user3.id);

		// Take holidays
		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail1 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail2 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail3 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail4 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail5 = new HolidayDetailCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail6 = new HolidayDetailCreationRequestDTO();

		// Holidays for the first user
		authenticateAs(user1.username);

		detail1.am = true;
		detail1.day = "10/10/2010";
		detail1.typeInstanceId = type1.instanceId;
		detail2.pm = true;
		detail2.day = "10/10/2010";
		detail2.typeInstanceId = type3.instanceId;

		List<HolidayDetailCreationRequestDTO> details = new ArrayList<HolidayDetailCreationRequestDTO>();
		details.add(detail1);
		details.add(detail2);
		request.details = details;

		requestService.registerRequestAndDetails(request);

		detail3.am = true;
		detail3.day = "10/19/2010";
		detail3.typeInstanceId = type3.instanceId;
		detail4.pm = true;
		detail4.day = "10/19/2010";
		detail4.typeInstanceId = type3.instanceId;
		detail5.am = true;
		detail5.day = "10/20/2010";
		detail5.typeInstanceId = type3.instanceId;
		detail6.pm = true;
		detail6.day = "10/20/2010";
		detail6.typeInstanceId = type1.instanceId;

		details = new ArrayList<HolidayDetailCreationRequestDTO>();
		details.add(detail3);
		details.add(detail4);
		details.add(detail5);
		details.add(detail6);
		request.details = details;
		requestService.registerRequestAndDetails(request);

		// Holidays for the third user
		authenticateAs(user3.username);

		detail1.am = true;
		detail1.day = "10/07/2010";
		detail1.typeInstanceId = type4.instanceId;
		detail2.pm = true;
		detail2.day = "10/07/2010";
		detail2.typeInstanceId = type4.instanceId;
		detail3.am = true;
		detail3.day = "10/08/2010";
		detail3.typeInstanceId = type4.instanceId;
		detail4.pm = true;
		detail4.day = "10/08/2010";
		detail4.typeInstanceId = type4.instanceId;

		details = new ArrayList<HolidayDetailCreationRequestDTO>();
		details.add(detail1);
		details.add(detail2);
		details.add(detail3);
		details.add(detail4);
		request.details = details;
		requestService.registerRequestAndDetails(request);

		List<HolidayProfilesSummaryDTO> summary = holidayService.getSummmaryForAllProfiles(10, 2010);

		float user1Type1 = 0;
		float user1Type2 = 0;
		float user1Type3 = 0;
		float user2Type4 = 0;
		float user3Type4 = 0;

		int user1Types = 0;
		int user2Types = 0;
		int user3Types = 0;

		for (HolidayProfilesSummaryDTO summaryProfile : summary) {
			if (summaryProfile.name.equals(profile1.name)) {
				HolidayUserSummaryDTO userSummary = summaryProfile.usersSummaries.get(0);
				user1Types = userSummary.typesSummaries.size();
				user1Type1 = userSummary.typesSummaries.get(0).pending;
				user1Type2 = userSummary.typesSummaries.get(1).pending;
				user1Type3 = userSummary.typesSummaries.get(2).pending;
			} else if (summaryProfile.name.equals(profile2.name)) {
				for (HolidayUserSummaryDTO userSummary : summaryProfile.usersSummaries) {
					if (userSummary.details.user.username.equals(user2.username)) {
						user2Types = userSummary.typesSummaries.size();
						user2Type4 = userSummary.typesSummaries.get(0).pending;
					} else if (userSummary.details.user.username.equals(user3.username)) {
						user3Types = userSummary.typesSummaries.size();
						user3Type4 = userSummary.typesSummaries.get(0).pending;
					}
				}
			}

		}
		assertEquals(3, user1Types);
		assertEquals(1, user2Types);
		assertEquals(1, user3Types);
		assertEquals(1, user1Type1, 0);
		assertEquals(0, user1Type2, 0);
		assertEquals(2, user1Type3, 0);
		assertEquals(0, user2Type4, 0);
		assertEquals(2, user3Type4, 0);

	}
	
	@Test
	public void getProfilesForUserTest(){
		this.creationOfUserRole();
		UserDTO user = this.creationOfTestUser(NAME_2);
		
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
		List<Long> typesId = new ArrayList<Long>();
		typesId.add(holidayType1.id);
		profileCreation.holidayTypesId = typesId;
		
		HolidayProfileDTO profile = holidayService.createOrUpdateHolidayProfile(profileCreation);
		
		// Add users
		List<String> usernames = new ArrayList<String>();
		usernames.add(NAME_2);
		holidayService.updateProfileUsers(profile.id, usernames);
		
		List<HolidayProfileDTO> profiles = holidayService.getProfilesForUser(user.id);
		assertEquals(1,profiles.size());
		assertEquals(NAME_1, profiles.get(0).name);
	}

	@Test
	public void updateNameOrColorTypeWithRequestMade(){
		// Creation of users
		this.creationOfUserRole();
		UserDTO user1 = this.creationOfTestUser(NAME_1);
		
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = EFFECTIVE_MONTH;
		creation.quantity = QUANTITY_1;
		creation.unity = PERIOD_UNIT;
		creation.anticipation = true;
		creation.color = COLOR_1;

		HolidayDTO holidayType = holidayService.createOrUpdateHoliday(creation);
		
		HolidayDTO currentInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, currentInstOld.name);
		assertEquals(false, currentInstOld.unlimited);
		assertEquals(QUANTITY_1, currentInstOld.quantity);
		assertEquals(PERIOD_UNIT, currentInstOld.periodUnit);
		assertEquals(true, currentInstOld.anticipation);
		assertEquals(COLOR_1, currentInstOld.color);
		
		HolidayDTO nextInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, nextInstOld.name);
		assertEquals(false, nextInstOld.unlimited);
		assertEquals(QUANTITY_1, nextInstOld.quantity);
		assertEquals(PERIOD_UNIT, nextInstOld.periodUnit);
		assertEquals(true, nextInstOld.anticipation);
		assertEquals(COLOR_1, nextInstOld.color);
		
		List<Long> profile1Types = new ArrayList<Long>();
		profile1Types.add(holidayType.id);
		
		List<String> profile1Usernames = new ArrayList<String>();
		profile1Usernames.add(user1.username);

		HolidayProfileCreationRequestDTO profileRequest = new HolidayProfileCreationRequestDTO();
		profileRequest.name = PROFILE_1;
		profileRequest.holidayTypesId = profile1Types;

		HolidayProfileDTO profile1 = holidayService.createOrUpdateHolidayProfile(profileRequest);

		holidayService.updateProfileUsers(profile1.id, profile1Usernames);
		
		// Take holidays
		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail1 = new HolidayDetailCreationRequestDTO();
		
		// Holidays for the user
		authenticateAs(user1.username);

		detail1.am = true;
		detail1.day = "10/10/2010";
		detail1.typeInstanceId = holidayType.instanceId;
		
		List<HolidayDetailCreationRequestDTO> details = new ArrayList<HolidayDetailCreationRequestDTO>();
		details.add(detail1);
		request.details = details;
		
		requestService.registerRequestAndDetails(request);
		
		HolidayCreationRequestDTO update = new HolidayCreationRequestDTO();
		update.id = holidayType.id;
		update.name = TYPE_1_MODIFIED;
		update.unlimited = false;
		update.effectiveMonth = EFFECTIVE_MONTH;
		update.quantity = QUANTITY_1;
		update.unity = PERIOD_UNIT;
		update.anticipation = true;
		update.color = COLOR_1_MODIFIED;

		HolidayDTO holidayTypeUpdated = holidayService.createOrUpdateHoliday(update);
		
		assertEquals(TYPE_1_MODIFIED, holidayTypeUpdated.name);
		assertEquals(false, holidayTypeUpdated.unlimited);
		assertEquals(EFFECTIVE_MONTH, holidayTypeUpdated.effectiveMonth);
		assertEquals(QUANTITY_1, holidayTypeUpdated.quantity);
		assertEquals(PERIOD_UNIT, holidayTypeUpdated.periodUnit);
		assertEquals(true, holidayTypeUpdated.anticipation);
		assertEquals(COLOR_1_MODIFIED, holidayTypeUpdated.color);
		assertEquals(holidayTypeUpdated.instanceId, holidayTypeUpdated.nextInstanceId);
		
		assertEquals(holidayType.instanceId, holidayTypeUpdated.instanceId);
		assertEquals(holidayType.nextInstanceId, holidayTypeUpdated.nextInstanceId);
		
		HolidayDTO currentInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.instanceId);
		assertEquals(TYPE_1_MODIFIED, currentInstUpd.name);
		assertEquals(false, currentInstUpd.unlimited);
		assertEquals(QUANTITY_1, currentInstUpd.quantity);
		assertEquals(PERIOD_UNIT, currentInstUpd.periodUnit);
		assertEquals(true, currentInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, currentInstUpd.color);
		
		HolidayDTO nextInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.nextInstanceId);
		assertEquals(TYPE_1_MODIFIED, nextInstUpd.name);
		assertEquals(false, nextInstUpd.unlimited);
		assertEquals(QUANTITY_1, nextInstUpd.quantity);
		assertEquals(PERIOD_UNIT, nextInstUpd.periodUnit);
		assertEquals(true, nextInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, nextInstUpd.color);
	}
	
	@Test
	public void updateOtherFieldsTypeWithRequestMade(){
		// Creation of users
		this.creationOfUserRole();
		UserDTO user1 = this.creationOfTestUser(NAME_1);
		
		HolidayCreationRequestDTO creation = new HolidayCreationRequestDTO();
		creation.name = TYPE_1;
		creation.unlimited = false;
		creation.effectiveMonth = EFFECTIVE_MONTH;
		creation.quantity = QUANTITY_1;
		creation.unity = PERIOD_UNIT;
		creation.anticipation = true;
		creation.color = COLOR_1;

		HolidayDTO holidayType = holidayService.createOrUpdateHoliday(creation);
		
		HolidayDTO currentInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, currentInstOld.name);
		assertEquals(false, currentInstOld.unlimited);
		assertEquals(QUANTITY_1, currentInstOld.quantity);
		assertEquals(PERIOD_UNIT, currentInstOld.periodUnit);
		assertEquals(true, currentInstOld.anticipation);
		assertEquals(COLOR_1, currentInstOld.color);
		
		HolidayDTO nextInstOld = holidayService.getHolidayTypeInstanceFromId(holidayType.instanceId);
		assertEquals(TYPE_1, nextInstOld.name);
		assertEquals(false, nextInstOld.unlimited);
		assertEquals(QUANTITY_1, nextInstOld.quantity);
		assertEquals(PERIOD_UNIT, nextInstOld.periodUnit);
		assertEquals(true, nextInstOld.anticipation);
		assertEquals(COLOR_1, nextInstOld.color);
		
		List<Long> profile1Types = new ArrayList<Long>();
		profile1Types.add(holidayType.id);
		
		List<String> profile1Usernames = new ArrayList<String>();
		profile1Usernames.add(user1.username);

		HolidayProfileCreationRequestDTO profileRequest = new HolidayProfileCreationRequestDTO();
		profileRequest.name = PROFILE_1;
		profileRequest.holidayTypesId = profile1Types;

		HolidayProfileDTO profile1 = holidayService.createOrUpdateHolidayProfile(profileRequest);

		holidayService.updateProfileUsers(profile1.id, profile1Usernames);
		
		// Take holidays
		HolidayRequestCreationRequestDTO request = new HolidayRequestCreationRequestDTO();
		HolidayDetailCreationRequestDTO detail1 = new HolidayDetailCreationRequestDTO();
		
		// Holidays for the user
		authenticateAs(user1.username);

		detail1.am = true;
		detail1.day = "10/10/2010";
		detail1.typeInstanceId = holidayType.instanceId;
		
		List<HolidayDetailCreationRequestDTO> details = new ArrayList<HolidayDetailCreationRequestDTO>();
		details.add(detail1);
		request.details = details;

		requestService.registerRequestAndDetails(request);
		
		HolidayCreationRequestDTO update = new HolidayCreationRequestDTO();
		update.id = holidayType.id;
		update.name = TYPE_1_MODIFIED;
		update.unlimited = false;
		update.effectiveMonth = EFFECTIVE_MONTH_MODIFIED;
		update.quantity = QUANTITY_1_MODIFIED;
		update.unity = PERIOD_UNIT_MODIFIED;
		update.anticipation = true;
		update.color = COLOR_1_MODIFIED;

		HolidayDTO holidayTypeUpdated = holidayService.createOrUpdateHoliday(update);
		
		assertEquals(TYPE_1_MODIFIED, holidayTypeUpdated.name);
		assertEquals(false, holidayTypeUpdated.unlimited);
		assertEquals(EFFECTIVE_MONTH_MODIFIED, holidayTypeUpdated.effectiveMonth);
		assertEquals(QUANTITY_1_MODIFIED, holidayTypeUpdated.quantity);
		assertEquals(PERIOD_UNIT_MODIFIED, holidayTypeUpdated.periodUnit);
		assertEquals(true, holidayTypeUpdated.anticipation);
		assertEquals(COLOR_1_MODIFIED, holidayTypeUpdated.color);
		assertNotSame(holidayTypeUpdated.instanceId, holidayTypeUpdated.nextInstanceId);
		
		assertEquals(holidayType.instanceId, holidayTypeUpdated.instanceId);
		assertNotSame(holidayType.nextInstanceId, holidayTypeUpdated.nextInstanceId);
		
		HolidayDTO currentInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.instanceId);
		assertEquals(TYPE_1, currentInstUpd.name);
		assertEquals(false, currentInstUpd.unlimited);
		assertEquals(QUANTITY_1, currentInstUpd.quantity);
		assertEquals(PERIOD_UNIT, currentInstUpd.periodUnit);
		assertEquals(true, currentInstUpd.anticipation);
		assertEquals(COLOR_1, currentInstUpd.color);
		
		HolidayDTO nextInstUpd = holidayService.getHolidayTypeInstanceFromId(holidayTypeUpdated.nextInstanceId);
		assertEquals(TYPE_1_MODIFIED, nextInstUpd.name);
		assertEquals(false, nextInstUpd.unlimited);
		assertEquals(QUANTITY_1_MODIFIED, nextInstUpd.quantity);
		assertEquals(PERIOD_UNIT_MODIFIED, nextInstUpd.periodUnit);
		assertEquals(true, nextInstUpd.anticipation);
		assertEquals(COLOR_1_MODIFIED, nextInstUpd.color);
	}
	
	
	
}
