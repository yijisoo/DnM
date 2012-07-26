/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: UserbehaviorFormatter.java 611 2006-03-12 22:43:49Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class UserbehaviorFormatter extends Formatter {
	long startMillis;
	
	public UserbehaviorFormatter() {
		super();
	}
	
	public String format(LogRecord record) {
		if (record.getSourceMethodName().equals("experimentStart")) {
			startMillis = record.getMillis();
		}
		
		return (record.getMillis() - startMillis) + "^" + record.getSourceClassName() + "^"
				+ record.getSourceMethodName() + "^" + record.getMessage()
				+ "\r\n";
	}
}
