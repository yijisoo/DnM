/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: Magnet.java 818 2012-07-26 18:22:31Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.logging.Logger;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author jyi Mar 25, 2004 1:02:24 PM
 */

public class Magnet extends PNode {
	private static final int ELLIPSE = 0;
	// TODO This part is only for DnM extension experiment. After the experiment, PLEASE fix this part.
	//private static final boolean isFlippedAttraction = true;
	public static boolean isFlippedAttraction = true;
	
	private static Logger log = Logger.getLogger("edu.gatech.isye.hcil.Magnet");
	private static final int RECTANGLE = 1;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static ArrayList valuesMaxList;
	private static ArrayList valuesMinList;
	
	public static void setValuesMaxList(ArrayList valuesMaxList) {
		Magnet.valuesMaxList = valuesMaxList;
	}
	public static void setValuesMinList(ArrayList valuesMinList) {
		Magnet.valuesMinList = valuesMinList;
	}
	private boolean bShowTooltip;
	private Ellipse2D ellipse;
	private int nMagnitude;
	private PText pMagnetDescription;
	private PText pMagnetName;
	private Rectangle2D rectangle;
	private ArrayList repelList;
	private double repelThreshold;
	private int shape;

	private String var_description;
	private int var_idx;

	private String var_measure;

	private String var_name;

	private String var_type;

	public Magnet(String var_name, String var_type, String var_measure,
			String var_description, int index) {
		super();
		this.var_idx = index;
		this.var_type = var_type;
		this.var_measure = var_measure;
		this.var_name = var_name;
		this.var_description = var_description;

		if (var_type.equals("I")) {
			this.repelThreshold = ((Integer) valuesMinList.get(index))
					.doubleValue();
		} else if (var_type.equals("D")) {
			this.repelThreshold = ((Double) valuesMinList.get(index))
					.doubleValue();
		} else if (var_type.equals("S") && var_measure.equals("O")) {
			this.repelThreshold = ((Integer) valuesMinList.get(index))
					.doubleValue();
		} else {
			assert (false);
		}

		shape = RECTANGLE;

		setMagnitude(10);

		/**
		 * Child node - Magnet name
		 */

		pMagnetName = new PText(this.var_name);
		Font font = new Font("Arial", Font.BOLD, 11);
		pMagnetName.setTextPaint(Color.black);
		pMagnetName.setFont(font);
		pMagnetName.setOffset((this.getWidth() - pMagnetName.getWidth()) / 2,
				(this.getHeight() - pMagnetName.getHeight()) / 2);

		this.addChild(pMagnetName);

		/**
		 * Child node - Magnet description
		 */

		pMagnetDescription = new PText(this.var_description);
		Font font2 = new Font("Arial", Font.PLAIN, 11);
		pMagnetDescription.setTextPaint(Color.black);
		pMagnetDescription.setFont(font2);
		pMagnetDescription.setOffset(pMagnetName.getX(), pMagnetName.getY()
				+ pMagnetName.getHeight() + pMagnetDescription.getHeight());
		bShowTooltip = false;

		this.setChildrenPickable(false);
	}

	public double getAttraction(double dOrgDustValue) {
		double attraction;
		int imin = 0, imax = 0, nDustValue = 0;
		double dmin = 0, dmax = 0, dDustValue = 0;

		if (var_type.equals("S") && var_measure.equals("O")) {
			imin = ((Integer) valuesMinList.get(var_idx)).intValue();
			imax = ((Integer) valuesMaxList.get(var_idx)).intValue();

			Double d = new Double(dOrgDustValue);
			nDustValue = d.intValue();
			
			nDustValue = (isFlippedAttraction) ? (imax - nDustValue + imin) : nDustValue;

			if (repelList != null) {
				if (((Boolean) repelList.get(nDustValue - imin)).booleanValue()) {
					attraction = -1.0 * nMagnitude * nDustValue / (imax - imin);
				} else {
					attraction = nMagnitude * nDustValue / (imax - imin);
				}
			} else
				attraction = nMagnitude * nDustValue / (imax - imin);
		} else if (var_measure.equals("Q")) {
			if (var_type.equals("I")) {
				dmin = ((Integer) valuesMinList.get(var_idx))
						.doubleValue();
				dmax = ((Integer) valuesMaxList.get(var_idx))
						.doubleValue();
				
				dDustValue = (isFlippedAttraction) ? (dmax - dOrgDustValue + dmin) : dOrgDustValue;
				
				attraction = (dDustValue - repelThreshold) * nMagnitude
						/ (dmax - dmin);
			} else if (var_type.equals("D")) {
				dmin = ((Double) valuesMinList.get(var_idx))
						.doubleValue();
				dmax = ((Double) valuesMaxList.get(var_idx))
						.doubleValue();
				
				dDustValue = (isFlippedAttraction) ? (dmax - dOrgDustValue + dmin) : dOrgDustValue;
				
				attraction = (dDustValue - repelThreshold) * nMagnitude
						/ (dmax - dmin);
			} else {
				log.info("invalid var type");
				assert false;
				attraction = 0.0;
			}
		} else {
			assert false;
			attraction = 0.0;
		}

		/*
		if (attraction < 0)
			log.info("negative attraction(" + dOrgDustValue + " : " + imin + ", " + imax + ", " + nDustValue + " : " + dmin + ", " + dmax + ", " + dDustValue +")");
		*/
		
		return attraction;
	}

	// TODO having two separate function for int and double would be more natural
	public double getAttraction(int nDustValue) {
		Double d = new Double(nDustValue);
		return getAttraction(d.doubleValue());
	}

	public Ellipse2D getEllipse() {
		if (shape != ELLIPSE)
			assert false;

		if (ellipse == null)
			ellipse = new Ellipse2D.Double();

		return ellipse;
	}

	public String getMagnetName() {
		return var_name;
	}

	public int getMagnitude() {
		return nMagnitude;
	}

	public Rectangle2D getRectangle() {
		if (shape != RECTANGLE)
			assert false;

		if (rectangle == null)
			rectangle = new Rectangle2D.Double();

		return rectangle;
	}

	public ArrayList getRepelList() {
		return repelList;
	}

	public double getThreshold() {
		return repelThreshold;
	}

	public int getVarIndex() {
		return var_idx;
	}

	public void hideToolTip() {
		if (bShowTooltip == true) {
			this.removeChild(pMagnetDescription);
			bShowTooltip = false;
		}
	}

	public boolean intersects(Rectangle2D aBounds) {
		if (shape == ELLIPSE)
			return getEllipse().intersects(aBounds);
		else if (shape == RECTANGLE)
			return getRectangle().intersects(aBounds);
		else {
			assert false;
			return false;
		}
	}

	public void paint(PPaintContext aPaintContext) {
		Graphics2D g2 = aPaintContext.getGraphics();

		if (nMagnitude == 0) {
			g2.setPaint(Color.gray);
		} else {
			g2.setPaint(new Color(193, 206, 220));
		}

		if (shape == ELLIPSE)
			g2.fill(getEllipse());
		else if (shape == RECTANGLE)
			g2.fill(getRectangle());
		else
			assert false;
	}

	public boolean setBounds(double x, double y, double width, double height) {
		if (super.setBounds(x, y, width, height)) {
			if (shape == ELLIPSE)
				ellipse.setFrame(x, y, width, height);
			else if (shape == RECTANGLE)
				rectangle.setFrame(x, y, width, height);
			else
				assert false;

			return true;
		}
		return false;
	}

	public void setIndex(int index) {
		this.var_idx = index;
	}

	public void setMagnitude(int nMagnitude) {
		this.nMagnitude = nMagnitude;
		double size = 20.0 + 2.0 * nMagnitude;

		if (shape == ELLIPSE) {
			if (ellipse == null)
				ellipse = new Ellipse2D.Double(0.0, 0.0, size, size);
		} else if (shape == RECTANGLE) {
			if (rectangle == null)
				rectangle = new Rectangle2D.Double(0.0, 0.0, size, size);
		} else
			assert false;

		setBounds(0.0, 0.0, size, size);

		if (pMagnetName != null)
			pMagnetName.setOffset(
					(this.getWidth() - pMagnetName.getWidth()) / 2, (this
							.getHeight() - pMagnetName.getHeight()) / 2);
	}

	public void setRepelList(ArrayList repelList) {
		this.repelList = repelList;
	}

	public void setRepelThreshold(double repelThreshold) {
		this.repelThreshold = repelThreshold;
	}

	public void setThreshold(double threshold) {
		repelThreshold = threshold;
	}

	public void showToolTip() {
		if (bShowTooltip == false) {
			this.addChild(pMagnetDescription);
			bShowTooltip = true;
		}
	}
}