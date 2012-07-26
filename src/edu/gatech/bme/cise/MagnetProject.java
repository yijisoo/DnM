/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: MagnetProject.java 593 2006-03-10 20:37:00Z jyi $
 *  
 */

/**
 * This java file only contains to-do list
 */

/*
 * Level A
 */

// TODONE Update repellent feature for small double values
// TODONE Make the log file comprehensive
// TODO Snapshot has the zoom and location information
// TODO Draw tooltip text in multiple lines
// TODO Paint the background of tooltip text node
// TODO Add some comments about the data file format
// TODO Debug heap overflow problem after multiple importFile()
// TODONE Log system check the start time, so that additional calc should not be
// 		necessary
// TODONE Update cereal/old/new car data sets according to this new data format
// TODONE Solve broken view of Filter and Magnet control menu
// TODONE Zoom in/out at the correct position
// TODONE Check PulseExample and figure out animation
// TODONE Speed up the attraction

/*
 * Level B
 */

// TODO Do not allow Dust particles to go underneath Magnets -> back/forth, or
// blocked by Magnets
// TODO deselect Dust when it is filtered out.

/*
 * Level C
 */

// TODO Add preview dialogbox for the imported data
// TODO Excel Import : http://www.andykhan.com/jexcelapi/index.html
// TODO bi-pole pseduo color icon
// TODO Two pole magnets: (+) attracts high value, (-) attract low value
// 		This should not be perceived as good or bad, which could be confusing
// 		+/- concept would be much easier to understand

package edu.gatech.bme.cise;

public class MagnetProject {
	public static void main(String[] args) {
		/**
		 * Start MagnetField
		 */
		MagnetField magnetField = new MagnetField(false, null);
		magnetField.setSize(600, 600);
		magnetField.setLocation(0, 0);
		magnetField.setVisible(true);
	}
}