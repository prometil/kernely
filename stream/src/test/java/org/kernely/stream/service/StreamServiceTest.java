/*
Copyright 2011 Prometil SARL

This file is part of Kernely.

Kernely is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of
the License, or (at your option) any later version.

Kernely is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public
License along with Kernely.
If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.stream.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.core.common.TestModule;
import org.kernely.core.event.UserCreationEvent;
import org.kernely.stream.UserEventHandler;
import org.kernely.stream.dto.StreamDTO;
import org.kernely.stream.dto.StreamMessageDTO;
import org.kernely.stream.model.Stream;

import com.google.common.eventbus.EventBus;
import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;

/**
 * 
 * @author g.breton
 * 
 */
public class StreamServiceTest extends AbstractServiceTest {

	private static final String USERNAME = "TEST_USERNAME";
	private static final String STREAM = "TEST_STREAM";
	
	@Inject
	private StreamService service;
	
	@Inject
	private EventBus bus;
	
	@Inject
	private UserEventHandler handler;

	@Test
	public void testGetNullMessages() {
		assertEquals(0, service.getMessages().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddVoidMessage() {
		service.addMessage("",0);
	}
	
	@Test
	public void testCreateStream(){
		service.createStream(STREAM, Stream.CATEGORY_USERS);
		
		assertNotNull(service.getStream(STREAM, Stream.CATEGORY_USERS));
	}

	@Test
	public void testAddMessage() {
		service.createStream(STREAM, Stream.CATEGORY_USERS);
		StreamDTO createdStream = service.getStream(STREAM, Stream.CATEGORY_USERS);
		StreamMessageDTO message = service.addMessage(USERNAME,createdStream.getId());
		assertNotNull(message);
		assertEquals(USERNAME, message.message);
		assertEquals(service.getMessages().size(),1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddNullMessage() {
		service.addMessage(null,0);
	}

	@Test
	public void testGetNullMessages2() {
		assertEquals(0, service.getMessages().size());
	}
	
	@Test
	public void testGetAllStreams() {
		service.createStream("test0001", "none");
		service.createStream("test0002", "none");
		service.createStream("test0003", "none");
		assertEquals(service.getAllStreams().size(),3);
		assertNotNull(service.getStream("test0001", "none"));
		assertNotNull(service.getStream("test0002", "none"));
		assertNotNull(service.getStream("test0003", "none"));
	}
	
	@Test
	public void testStreamLock() {
		service.createStream("testLockStream", "none");
		StreamDTO stream = service.getStream("testLockStream", "none");
		assertEquals(false, stream.isLocked());
		service.lockStream(stream.getId());
		stream = service.getStream("testLockStream", "none");
		assertEquals(true, stream.isLocked());
	}
	
	@Test
	public void testStreamUnlock() {
		service.createStream("testUnlockStream", "none");
		StreamDTO stream = service.getStream("testUnlockStream", "none");
		assertEquals(false, stream.isLocked());
		service.lockStream(stream.getId());
		service.unlockStream(stream.getId());
		stream = service.getStream("testUnlockStream", "none");
		assertEquals(false, stream.isLocked());
	}

	@Test
	public void testNoMessages() {
		assertEquals(0, service.getMessages().size());
	}
	
	@Test
	public void testHandlingEvent() {
		bus.register(handler);
		bus.post(new UserCreationEvent(1, USERNAME));
		
		StreamDTO dto = service.getStream("Stream of "+USERNAME, Stream.CATEGORY_USERS);
		assertNotNull(dto);
	}

}
