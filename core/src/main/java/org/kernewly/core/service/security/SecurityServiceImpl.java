package org.kernewly.core.service.security;

import org.apache.shiro.SecurityUtils;

public class SecurityServiceImpl implements SecurityService{

	@Override
	public void logout() {
		SecurityUtils.getSubject().logout();
	}

	@Override
	public String getCurrentUserName() {
		return (String)SecurityUtils.getSubject().getPrincipal();
	}

}
