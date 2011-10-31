/**
 * 
 */
package org.kernely.stream.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.stream.model.StreamMessage;

/**
 * @author yak
 * 
 */
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
	public StreamMessageDTO(StreamMessage pMessage) {
		message = pMessage.getMessage();
		date = pMessage.getDate();
	}

	//the stream message DTO
	public String message;

	// the message date
	public Date date;
}
