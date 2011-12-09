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

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
@XmlRootElement
public class StreamCommentCreationRequestDTO {

	/**
	 * Creates a StreamCommentCreationRequestDTO
	 * 
	 * @param message
	 *            The comment's content
	 * @param idStream
	 *            Id of the stream
	 * @param idMessageParent
	 *            Id of parent message
	 */
	public StreamCommentCreationRequestDTO(String message, long idStream, long idMessageParent) {
		this.message = message;
		this.idStream = idStream;
		this.idMessageParent = idMessageParent;
	}

	/**
	 * Default Constructor
	 */
	public StreamCommentCreationRequestDTO() {

	}

	public String message;
	public long idStream;
	public long idMessageParent;
}
