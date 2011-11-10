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

import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.stream.model.Stream;

@XmlRootElement
public class StreamDTO {

	/**
	 * The stream constructor
	 */
	public StreamDTO() {

	}
	
	/**
	 * The stream 
	 * @param title the title
	 */
	public StreamDTO(Stream stream) {
		this.title = stream.getTitle();
		this.id = stream.getId();
		this.category = stream.getCategory();
	}

	/**
	 * Get the database id of the steam.
	 * @return the id of the stream.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Set the id of the stream
	 * @param id id of the stream
	 */
	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<StreamMessageDTO> getMessages() {
		return messages;
	}

	public void setMessages(List<StreamMessageDTO> list) {
		this.messages = list;
	}

	
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}



	//the stream title
	private String title;
	
	//the id in database
	private long id;
	
	//messages contained by the stream
	private List<StreamMessageDTO> messages;
	
	//the category of the stream
	private String category;

}
