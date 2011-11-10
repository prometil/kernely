/**
 * 
 */
package org.kernely.core.job;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author yak
 *
 */
public class GuiceSchedulerFactory implements JobFactory {

	 private final Injector guice;
	 
	    @Inject
	    public GuiceSchedulerFactory(final Injector guice)
	    {
	        this.guice = guice;
	    }
	 
	   

		@SuppressWarnings("unchecked")
		@Override
		public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
			 JobDetail jobDetail = bundle.getJobDetail();
				Class jobClass = jobDetail.getJobClass();
			        return (Job) guice.getInstance(jobClass);
		}


}
