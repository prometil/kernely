package org.kernely.project.service;

import org.kernely.core.service.AbstractService;
import org.kernely.core.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * The service for project pages
 * 
 */
public class ProjectService extends AbstractService {
	@Inject
	UserService userService;

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

}
