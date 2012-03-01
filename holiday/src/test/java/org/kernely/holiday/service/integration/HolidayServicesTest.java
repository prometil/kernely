package org.kernely.holiday.service.integration;

import static org.junit.Assert.*;

import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.service.user.RoleService;
import org.kernely.core.service.user.UserService;
import org.kernely.holiday.service.HolidayBalanceService;
import org.kernely.holiday.service.HolidayRequestService;
import org.kernely.holiday.service.HolidayService;

import com.google.inject.Inject;


public class HolidayServicesTest extends AbstractServiceTest {

	@Inject
	private HolidayRequestService holidayRequestService;

	@Inject
	private UserService userService;

	@Inject
	private RoleService roleService;

	@Inject
	private HolidayService holidayService;

	@Inject
	private HolidayBalanceService holidayBalanceService;
	
	@Test
	public void simulateProfileTypeAndBalanceCreation(){
		assertTrue(true);
	}
}
