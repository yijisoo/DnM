/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: ControlWindow.java 818 2012-07-26 18:22:31Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;


/**
 * Control Window
 * 
 * @author jyi
 * 
 */
public class ControlWindow extends JFrame implements KeyListener {
	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.ControlWindow");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PaneColor colorPane;

	private PaneFilter filterPane;

	private JTabbedPane jtp;

	private PaneMagnet magnetPane;

	private PaneSize sizePane;

	public ControlWindow(ArrayList names, ArrayList types, ArrayList measures,
			ArrayList labels, ArrayList maxs, ArrayList mins, Dust[] dust,
			ArrayList magnets) {

		super("Control");

		PaneData.initVariables(names, types, measures, labels, maxs, mins, dust,
				magnets);

		Container contents = getContentPane();
		jtp = new JTabbedPane();

		// Color Pane
		colorPane = new PaneColor();

		// Size Pane
		sizePane = new PaneSize();

		// Filter Pane
		filterPane = new PaneFilter();

		// Magner Pane
		magnetPane = new PaneMagnet();

		jtp.addTab("Color", null, colorPane, "Adjust Color encodings");
		jtp.addTab("Size", null, sizePane, "Adjust Size encodings");
		jtp.addTab("Filter", null, filterPane, "Adjust Filter Options");
		jtp.addTab("Magnet", null, magnetPane, "Adjust Magnet Behaviors");

		contents.add(jtp);

		addKeyListener(this);

		setVisible(true);
	}

	public PaneColorSelect getColorSelectPane() {
		return colorPane.getColorSelectPane();
	}

	public void keyPressed(KeyEvent e) {
		log.info("");
	}

	public void keyReleased(KeyEvent e) {
		log.info("");
	}

	public void keyTyped(KeyEvent e) {
		log.info("");
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		jtp.setSelectedIndex(0);
		colorPane.loadSnapshot(in);
		jtp.setSelectedIndex(1);
		sizePane.loadSnapshot(in);
		jtp.setSelectedIndex(2);
		filterPane.loadSnapShot(in);
		jtp.setSelectedIndex(3);
	}

	public void refresh() {
		jtp.setSelectedIndex(3);
		magnetPane.refresh();
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		colorPane.saveSnapshot(out);
		sizePane.saveSnapshot(out);
		filterPane.saveSnapshot(out);
	}

	public void selectMagnet(String strMagnet) {
		if (jtp.getSelectedComponent() == magnetPane)
			magnetPane.selectMagnet(strMagnet);
	}

//	/**
//	 * @param magnetList
//	 */
//	public void setMagnetList(ArrayList magnetList) {
//		PaneData.setMagnetList(magnetList);
//	}
}