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

import org.junit.Assume;
import org.junit.Test;
import org.kernely.core.common.AbstractServiceTest;
import org.kernely.stream.dto.StreamMessageDTO;

import com.google.inject.Inject;

/**
 * 
 * @author g.breton
 *
 */
public class StreamServiceTest extends AbstractServiceTest {

	private static final String USERNAME = "USERNAME";
	@Inject
	private StreamService service;
	
	@Test
	public void testAddMessage() {
		StreamMessageDTO message = service.addMessage(USERNAME);
		assertNotNull(message);
		assertEquals(USERNAME, message.message);
		assertEquals(1, service.getMessages().size());
	}
	
	
	@Test
	public void testAddNullMessage(){
		service.addMessage(null);
		Assume.assumeNotNull(service); 
		
	}

	@Test
	public void testGetMessages(){
		service.addMessage(USERNAME);
		assertNotNull(service.getMessages());
		
	}
	
	@Test
	public void testGetNullMessages() {
		assertNotNull(service.getMessages());
	}
	


} 
