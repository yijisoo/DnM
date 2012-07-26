/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: DataLabel.java 593 2006-03-10 20:37:00Z jyi $
 *  
 */

package edu.gatech.bme.cise;

/**
 * @author jyi
 * 
 * purpose:
 * 
 */
public class DataLabel {
	private String[] dataList;
	public DataLabel(String strOrg) {
		dataList = strOrg.split("/");
	}

	public String get(int idx) {
		assert (idx >= 1 && idx <= dataList.length);
		return (String) dataList[idx - 1];
	}
}