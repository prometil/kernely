/**
 * Copyright 2011 Prometil SARL
 *
 * This file is part of Kernely.
 *
 * Kernely is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Kernely is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public
 * License along with Kernely.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package org.kernely.core.service.mail.builder;

import java.util.List;
import java.util.Map;


/**
 * @author g.breton
 *
 */
public interface MailBuilder {
	public MailBuilder subject(String pSubject);

	public MailBuilder to(String addresses);
	
	public MailBuilder to(List<String> addresses);

	public MailBuilder cc(String addresses);
	
	public MailBuilder cc(List<String> addresses);

	public MailBuilder with(Map<String, Object> content);
	
	public MailBuilder with(String key, String value);
	
	public void registerMail();
}
