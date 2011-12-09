package org.kernely.stream.dto;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StreamCreationRequestDTO {

	public int id;
	public String name;
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
