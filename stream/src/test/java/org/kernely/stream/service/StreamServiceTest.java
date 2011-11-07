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
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.kernely.core.test.StreamTestModule;
import org.kernely.stream.dto.StreamMessageDTO;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;


public class StreamServiceTest {

	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(StreamTestModule.class);
	
	@Inject
	private StreamService service;

	@Test
	public void testGetNullMessages() {
		assertEquals(0, service.getMessages().size());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddVoidMessage(){
		service.addMessage(""); 
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddNullMessage(){
		service.addMessage(null); 
	}
	
	@Test
	public void testAddMessage() {
		StreamMessageDTO message = service.addMessage("test");
		assertEquals("test", message.message);
	}	


	@Test
	public void testGetMessages(){
		service.addMessage("test");
		StreamMessageDTO message =service.getMessages().get(0);
		assertEquals("test", message.message );		
	}
	
	@Test
	public void testGetNullMessages2() {
		assertEquals(0, service.getMessages().size());
	}
	


}  
