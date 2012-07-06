package org.kernely.holiday.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.shiro.authz.UnauthorizedException;
import org.joda.time.DateTime;
import org.kernely.core.model.User;
import org.kernely.core.service.UserService;
import org.kernely.holiday.dto.HolidayDonationDTO;
import org.kernely.holiday.model.HolidayDonation;
import org.kernely.holiday.model.HolidayTypeInstance;
import org.kernely.service.AbstractService;

import com.google.inject.Inject;

/**
 * Holiday Donation Service.
 */
public class HolidayDonationService extends AbstractService {
	@Inject
	private UserService userService;

	@Inject
	private HolidayBalanceService balanceService;

	private static final float HALF_DAY = 0.5F;
	private static final float RANGE = 0.0001F;

	/**
	 * Creates a new donation based on the DTO which contains all informations
	 * 
	 * @param donationRequest
	 *            The DTO on which the creation will be based
	 * @return A DTO according to the new donation created
	 */
	public HolidayDonationDTO createDonation(HolidayDonationDTO donationRequest) {
		if (!userService.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}

		float days = donationRequest.amount;
		
		if(days > 10){
			throw new IllegalArgumentException("You can't give more than 10 days");
		}

		// Can only add days or half days.
		int entire = (int) (days / HALF_DAY);
		if (!(Math.abs(((float) entire) * HALF_DAY - days) < RANGE)) {
			throw new IllegalArgumentException("Can only add days or half days. " + days + " is not a multiple of half day");
		}

		HolidayDonation donation = new HolidayDonation();
		donation.setAmount(donationRequest.amount);
		donation.setComment(donationRequest.comment);
		HolidayTypeInstance instance = em.get().find(HolidayTypeInstance.class, donationRequest.typeInstanceId);
		if (instance == null) {
			throw new IllegalArgumentException("The given instance with id " + donationRequest.typeInstanceId + " doesn't exist !");
		}
		donation.setHolidayTypeInstance(instance);

		donation.setManager(this.getAuthenticatedUserModel());

		User receiver = em.get().find(User.class, donationRequest.receiverId);
		if (receiver == null) {
			throw new IllegalArgumentException("The receiver with id " + donationRequest.receiverId + " doesn't exist !");
		}
		donation.setReceiver(receiver);
		
		donation.setDate(DateTime.now().toDate());

		em.get().persist(donation);

		// Once the donation is created we have to increase the balance
		// associated to the given type for the given user.
		balanceService.addDaysInAvailableFromRequest(donationRequest.typeInstanceId, receiver.getId(), days);

		return new HolidayDonationDTO(donation);
	}
	
	@SuppressWarnings("unchecked")
	public List<HolidayDonationDTO> getAllDonationForCurrentManager(){
		if (!userService.isManager(this.getAuthenticatedUserModel().getUsername())) {
			throw new UnauthorizedException("Only managers can access to this functionality !");
		}
		
		Query query = em.get().createQuery("SELECT d FROM HolidayDonation d WHERE manager = :manager");
		query.setParameter("manager", this.getAuthenticatedUserModel());
		List<HolidayDonation> donations = (List<HolidayDonation>)query.getResultList();
		List<HolidayDonationDTO> donationsDTO = new ArrayList<HolidayDonationDTO>();
		for(HolidayDonation d : donations){
			donationsDTO.add(new HolidayDonationDTO(d));
		}
		Collections.sort(donationsDTO);
		Collections.reverse(donationsDTO);
		return donationsDTO;
	}
}
