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
 * Interface of mail bilder
 */
public interface MailBuilder {
	/**
	 * The subject of the mail
	 * @param pSubject
	 * @return
	 */
	MailBuilder subject(String pSubject);

	/**
	 * The destinations of the mail
	 * @param addresses String
	 * @return
	 */
	MailBuilder to(String addresses);
	
	/**
	 * The destinations of the mail
	 * @param addresses list of String
	 * @return
	 */
	MailBuilder to(List<String> addresses);

	/**
	 * The list of addresses in copy 
	 * @param addresses String
	 * @return
	 */
	MailBuilder cc(String addresses);
	
	/**
	 * The list of addresses in copy
	 * @param addresses list of string
	 * @return
	 */
	MailBuilder cc(List<String> addresses);

	/**
	 * Attachment of the mail
	 * @param content a map of String and Object
	 * @return
	 */
	MailBuilder with(Map<String, Object> content);
	
	/**
	 * Attachment of the mail
	 * @param key the key of the attachment
	 * @param value the value
	 * @return
	 */
	MailBuilder with(String key, String value);
	
	/**
	 * Is the mail registered ?
	 * @return a boolean 
	 */
	boolean registerMail();
}
