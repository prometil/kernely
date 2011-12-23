package org.kernely.holiday.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.kernely.holiday.model.HolidayRequest;
import org.kernely.holiday.model.HolidayRequestDetail;

@XmlRootElement
public class HolidayRequestDTO {

	public int id;
	public Date beginDate;
	public Date endDate;
	public int status;
	public String requesterComment;
	public List<HolidayDetailDTO> details = new ArrayList<HolidayDetailDTO>();
	
	public HolidayRequestDTO(){
		
	}
	
	public HolidayRequestDTO(HolidayRequest request){
		this.id = request.getId();
		this.beginDate = request.getBeginDate();
		this.endDate = request.getEndDate();
		this.status = request.getStatus();
		this.requesterComment = request.getRequesterComment();
		for(HolidayRequestDetail hd : request.getDetails()){
			this.details.add(new HolidayDetailDTO(hd));
		}
	}
}
