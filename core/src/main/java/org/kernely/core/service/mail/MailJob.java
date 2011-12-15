package org.kernely.core.service.mail;

import java.util.List;

import org.kernely.core.model.Mail;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class MailJob implements Job{
	private static final Logger log = LoggerFactory.getLogger(MailService.class);
	
	@Inject
	public Mailer mailService;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		log.debug("Retrieving waiting mails...");
		List<Mail> mails = mailService.getMailsToSend();
		
		for(Mail m : mails){
			log.debug("Sending mails with id {}", m.getId());
			mailService.send(m);
		}
	}
}
