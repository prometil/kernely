package org.kernely.holiday.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;

/**
 * Dto for holiday request 
 * @author b.grandperret
 *
 */
@XmlRootElement
public class HolidayRequestDTO {

	/**
	 * The id of the holiday request DTO 
	 */
	public int id;
	
	/**
	 * The begin date of holiday
	 */
	public Date beginDate;
	
	/**
	 * The end date of holiday
	 */
	public Date endDate;
	
	/**
	 * The status of the request
	 */
	public int status;
	
	/**
	 * The comment of the request
	 */
	public String requesterComment;
	
	/**
	 * The username of the requester
	 */
	public String user;
	
	/**
	 * The comment of the manager
	 */
	public String managerComment;
	
	/**
	 * The list of holiday details
	 */
	public List<HolidayDetailDTO> details = new ArrayList<HolidayDetailDTO>();
	
	/**
	 * Default constructor 
	 */
	public HolidayRequestDTO(){
		
	}
	
	/**
	 * The constructor 
	 * @param request the model of HolidayRequest
	 */
	public HolidayRequestDTO(HolidayRequest request){
		this.id = request.getId();
		this.beginDate = request.getBeginDate();
		this.endDate = request.getEndDate();
		this.status = request.getStatus();
		this.requesterComment = request.getRequesterComment();
		this.user = request.getUser().getUsername();
		this.managerComment = request.getManagerComment(); 
		for(HolidayRequestDetail hd : request.getDetails()){
			this.details.add(new HolidayDetailDTO(hd));
		}
	}
}
