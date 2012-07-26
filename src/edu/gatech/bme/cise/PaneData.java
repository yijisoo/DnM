/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id$
 *  
 */

package edu.gatech.bme.cise;

import java.util.ArrayList;

import javax.swing.JPanel;

public class PaneData extends JPanel {
	static protected Dust[] dustList;

	static protected ArrayList magnetList;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static protected ArrayList values_max;

	static protected ArrayList values_min;

	static protected ArrayList var_labels;

	static protected ArrayList var_measures;

	static protected ArrayList var_names;

	static protected ArrayList var_types;

	public static void initVariables(ArrayList n, ArrayList t, ArrayList m,
			ArrayList l, ArrayList max, ArrayList min, Dust[] dust,
			ArrayList magnets) {
		var_names = n;
		var_types = t;
		var_measures = m;
		var_labels = l;
		values_max = max;
		values_min = min;
		dustList = dust;
		magnetList = magnets;
	}

//	/**
//	 * @param magnetList
//	 */
//	public static void setMagnetList(ArrayList magnets) {
//	}

	public PaneData() {
		super();
	}
}