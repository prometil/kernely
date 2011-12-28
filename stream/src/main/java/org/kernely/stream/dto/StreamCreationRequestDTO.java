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
package org.kernely.stream.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The stream creation request DTO
 * @author b.grandperret
 *
 */
@XmlRootElement
public class StreamCreationRequestDTO {

	/**
	 * The id of the stream
	 */
	public int id;
	
	/**
	 * The name of the stream
	 */
	public String name;
	
	/**
	 * The category of the stream
	 */
	public String category;

	/**
	 * Default Constructor
	 */
	public StreamCreationRequestDTO() {

	}

	/**
	 * Creates StreamCreationRequestDTO
	 * 
	 * @param id
	 *            Id of the stream
	 * @param name
	 *            Name of the Stream
	 * @param category
	 *            Category of the Stream
	 */
	public StreamCreationRequestDTO(int id, String name, String category) {
		this.id = id;
		this.name = name;
		this.category = category;
	}

}
