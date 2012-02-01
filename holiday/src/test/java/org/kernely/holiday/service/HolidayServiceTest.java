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
import org.kernely.holiday.dto.HolidayCreationRequestDTO;
import org.kernely.holiday.dto.HolidayDTO;
import org.kernely.holiday.dto.HolidayUpdateRequestDTO;

import com.google.inject.Inject;

public class HolidayServiceTest extends AbstractServiceTest {

	private static final String TYPE = "type";
	private static final int QUANTITY = 25;

	@Inject
	private HolidayService holidayService;

	@Test
	public void getHolidayDTOTest(){
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		cdto.quantity = QUANTITY;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = holidayService.getAllHoliday().get(0);
		assertEquals(TYPE, holidayService.getHolidayDTO(hdto.id).name);
		assertEquals(QUANTITY, holidayService.getHolidayDTO(hdto.id).quantity);
	}
	
	@Test
	public void creationHoliday() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		cdto.quantity = QUANTITY;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = holidayService.getAllHoliday().get(0);
		assertEquals(TYPE, hdto.name);
		assertEquals(QUANTITY, hdto.quantity, 0.0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayWithNullRequest() {
		holidayService.createHoliday(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayWithNullType() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = null;
		holidayService.createHoliday(cdto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayWithVoidType() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = "  ";
		holidayService.createHoliday(cdto);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void creationHolidayAlreadyExist(){
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		cdto.quantity = QUANTITY;
		holidayService.createHoliday(cdto);
		HolidayCreationRequestDTO cdto2 = new HolidayCreationRequestDTO();
		cdto2.type = TYPE;
		cdto2.quantity = QUANTITY;
		holidayService.createHoliday(cdto2);	
	}

	@Test
	public void updateHoliday() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = new HolidayDTO();
		hdto = holidayService.getAllHoliday().get(0);

		HolidayUpdateRequestDTO udto = new HolidayUpdateRequestDTO();
		udto.id = (int) hdto.id;
		udto.frequency = 6;
		udto.quantity = QUANTITY;
		udto.type = "new type";
		holidayService.updateHoliday(udto);

		HolidayDTO hdto2 = new HolidayDTO();
		hdto2 = holidayService.getAllHoliday().get(0);
		assertEquals("new type", hdto2.name);
		assertEquals(QUANTITY, hdto2.quantity);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateHolidayWithNullRequest() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		holidayService.createHoliday(cdto);
		holidayService.updateHoliday(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateHolidayWithNullType() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = new HolidayDTO();
		hdto = holidayService.getAllHoliday().get(0);

		HolidayUpdateRequestDTO udto = new HolidayUpdateRequestDTO();
		udto.id = (int) hdto.id;
		udto.frequency = 6;
		udto.type = null;
		holidayService.updateHoliday(udto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateHolidayWithVoidType() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = new HolidayDTO();
		hdto = holidayService.getAllHoliday().get(0);

		HolidayUpdateRequestDTO udto = new HolidayUpdateRequestDTO();
		udto.id = (int) hdto.id;
		udto.frequency = 6;
		udto.type = "      ";
		holidayService.updateHoliday(udto);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateHolidayWithSignedFrequency() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = new HolidayDTO();
		hdto = holidayService.getAllHoliday().get(0);

		HolidayUpdateRequestDTO udto = new HolidayUpdateRequestDTO();
		udto.id = (int) hdto.id;
		udto.frequency = -6;
		udto.type = "new type";
		holidayService.updateHoliday(udto);
	}

	@Test
	public void deleteHoliday() {
		HolidayCreationRequestDTO cdto = new HolidayCreationRequestDTO();
		cdto.type = TYPE;
		holidayService.createHoliday(cdto);
		HolidayDTO hdto = new HolidayDTO();
		hdto = holidayService.getAllHoliday().get(0);

		holidayService.deleteHoliday(hdto.id);
		assertEquals(0, holidayService.getAllHoliday().size());
	}

}
