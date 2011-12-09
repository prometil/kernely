package org.kernely.stream.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StreamRightsUpdateRequestDTO {

	/**
	 * Default Constructor
	 */
	public StreamRightsUpdateRequestDTO() {

	}

	/**
	 * Creates a StreStreamRightsUpdateRequestDTO
	 * 
	 * @param streamid
	 *            Id of the concerned stream
	 * @param rights
	 *            rights associated to the stream
	 */
	public StreamRightsUpdateRequestDTO(int streamid, List<RightOnStreamDTO> rights) {
		this.streamid = streamid;
		this.rights = rights;
	}

	public int streamid;
	public List<RightOnStreamDTO> rights;
}
