package com.pocketsoap.salesforce.rest.chatter;

import javax.xml.bind.annotation.XmlElement;

/**
 * The base interface of Message Segments
 * @author nlipke-sfdc
 *
 */
public interface MessageSegment {
	
	/**
	 * Get the segment's type
	 * @return The type as a string
	 */
	@XmlElement
	String getType();

}
