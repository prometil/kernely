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
package org.kernely.core.migrations.migrator;

/**
 * A version
 * 
 * @author g.breton
 */
public class Version implements Comparable<Version> {

	/**
	 * The wrapped version
	 */
	private String version;

	/**
	 * Constructor that wrap a version
	 * 
	 * @param pVersion
	 *            a version
	 */
	public Version(String pVersion) {
		version = pVersion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Version other) {
		return compareTo(other.getVersion());
	}

	/**
	 * Compares a version with a string
	 * 
	 * @param otherVersion
	 *            the string version e.g 0.1.1
	 * @return -1, 0, 1 if version is lesser, equals or greater to other version
	 */
	public int compareTo(String otherVersion) {
		String[] otherDigits = otherVersion.split("\\.");
		String[] digits = getVersion().split("\\.");
		int result = 0;
		int min = Math.min(otherDigits.length, digits.length);
		for (int i = 0; i < min; i++) {
			int digit = Integer.parseInt(digits[i]);
			int otherDigit = Integer.parseInt(otherDigits[i]);

			if (digit > otherDigit) {
				result = 1;
			} else if (digit < otherDigit) {
				result = -1;
			}
		}
		if (digits.length < otherDigits.length) {
			result = -1;
		} else if (digits.length > otherDigits.length) {
			result = 1;
		}
		return result;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return version;
	}
}
