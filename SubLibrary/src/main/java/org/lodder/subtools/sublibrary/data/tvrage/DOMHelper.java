package org.lodder.subtools.sublibrary.data.tvrage;

/*
 * Copyright (c) 2004-2013 Stuart Boston
 * 
 * This file is part of the TVRage API.
 * 
 * TVRage API is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * TVRage API is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with TVRage API. If not,
 * see <http://www.gnu.org/licenses/>.
 */

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Generic set of routines to process the DOM model data
 *
 * @author Stuart.Boston
 *
 */
public class DOMHelper {

	// Hide the constructor
	protected DOMHelper() {
		// prevents calls from subclass
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the string value of the tag element name passed
	 *
	 * @param element
	 * @param tagName
	 * @return
	 */
	public static String getValueFromElement(Element element, String tagName) {
		NodeList elementNodeList = element.getElementsByTagName(tagName);
		Element tagElement = (Element) elementNodeList.item(0);
		if (tagElement == null) {
			return "";
		}

		NodeList tagNodeList = tagElement.getChildNodes();
		if (tagNodeList == null || tagNodeList.getLength() == 0) {
			return "";
		}
		return tagNodeList.item(0).getNodeValue();
	}
}
