package org.kernely.stream.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class StreamRightsUpdateRequestDTO {
	public StreamRightsUpdateRequestDTO(){
		
	}
	
	public StreamRightsUpdateRequestDTO(int streamid, List<RightOnStreamDTO> rights){
		this.streamid = streamid;
		this.rights = rights;
	}
	
	public int streamid;
	public List<RightOnStreamDTO> rights;
}
