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
package org.kernely.core.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Plugin DTO
 * @author b.grandperret
 *
 */
@XmlRootElement
public class PluginDTO {

	public List<AdminPageDTO> adminPages;
	public String name;
	public String path;
	public String img;
	public String email;

	/**
	 * Default Constructor
	 */
	public PluginDTO() {

	}

	/**
	 * PluginDTO Constructor
	 * 
	 * @param name
	 *            Name of the current plugin
	 * @param path
	 *            Path of the current plugin
	 * @param img
	 *            Image associated to the current plugin
	 * @param adminPages
	 *            Administration pages associated to the current plugin
	 */
	public PluginDTO(String name, String path, String img, List<AdminPageDTO> adminPages) {
		this.name = name;
		this.path = path;
		this.img = img;
		this.adminPages = adminPages;
	}

}
