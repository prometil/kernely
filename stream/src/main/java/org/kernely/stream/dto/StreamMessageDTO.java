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
package org.kernely.stream.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.stream.model.Message;

@XmlRootElement
public class StreamMessageDTO {

	/**
	 * The message stream constructor
	 */
	public StreamMessageDTO() {

	}
	
	/**
	 * The message stream 
	 * @param pMessage the message
	 */
	public StreamMessageDTO(Message pMessage) {
		message = pMessage.getContent();
		date = pMessage.getDate();
		streamId = pMessage.getStream().getId();
	}

	//the id of the stream containing this message
	private long streamId;
	
	//the stream message DTO
	public String message;

	// the message date
	public Date date;
}
