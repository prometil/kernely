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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.stream.model.Stream;

/**
 * The stream DTO 
 * @author b.grandperret
 *
 */
@XmlRootElement
public class StreamDTO {

	/**
	 * The stream constructor
	 */
	public StreamDTO() {

	}

	/**
	 * The stream
	 * 
	 * @param title
	 *            the title
	 */
	public StreamDTO(Stream stream) {
		this.title = stream.getTitle();
		this.id = stream.getId();
		this.category = stream.getCategory();
		this.locked = stream.isLocked();
	}

	// the stream title
	public String title;

	// the id in database
	public int id;

	// messages contained by the stream
	public List<StreamMessageDTO> messages;

	// the category of the stream
	public String category;

	// is the stream locked ?
	public boolean locked;

}
