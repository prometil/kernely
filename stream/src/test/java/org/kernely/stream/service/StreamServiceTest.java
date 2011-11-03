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

import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsNull;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.kernely.core.test.StreamTestModule;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;


public class StreamServiceTest {

	@Rule
	public final GuiceBerryRule guiceBerry = new GuiceBerryRule(StreamTestModule.class);
	
	@Inject
	private StreamService service;

	
	
	@Test
	public void testAddMessage() {
		service.addMessage("test");
		assertNotNull(service);
	}
	
	@Test
	public void testAddNullMessage(){
		service.addMessage(null);
		Assume.assumeNotNull(service); 
		
	}

	@Test
	public void testGetMessages(){
		service.addMessage("test");
		assertNotNull(service.getMessages());
		
	}
	
	//not return even when there is no message because
	//jquery must handle the data in the stream.js
	@Test
	public void testGetNullMessages() {
		assertNotNull(service.getMessages());
	}
	


} 
