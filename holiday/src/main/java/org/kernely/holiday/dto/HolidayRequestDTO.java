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
	 * The manager who post the comment
	 */
	public String manager;
	
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
		if (request.getManager() != null){
			this.manager = request.getManager().getUsername();
		}
		this.managerComment = request.getManagerComment(); 
		for(HolidayRequestDetail hd : request.getDetails()){
			this.details.add(new HolidayDetailDTO(hd));
		}
	}
	
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		return prime * result + id;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		HolidayRequestDTO other = (HolidayRequestDTO) obj;
		if (id != other.id){
			return false;
		}
		return true;
	}
}
