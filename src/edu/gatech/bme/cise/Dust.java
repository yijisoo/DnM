/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: Dust.java 593 2006-03-10 20:37:00Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * @author Ji Soo Yi
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author jyi Mar 25, 2004 1:04:43 PM
 */
public class Dust extends PImage {
	private static ArrayList chosenList;
	private static double[][] distMatrix; // distance matrix between dusts.
	private static ArrayList dustList;
	// final static String[] strRun = {"rh", "rl", "wh", "wl"};
	final static double DUSTSIZE = 8.0;

	private static ArrayList filteredList;

	final static double IMAGEHEIGHT = 6.65;
	final static double IMAGEWIDTH = 10.0;
	static Logger log = Logger.getLogger("edu.gatech.isye.hcil.Dust");
	private static Random rndGenerator = new Random();
	private static ArrayList selectedList;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static double sizeDust;
	private static ArrayList var_measures;
	private static ArrayList var_types;

	private static void addChosenDust(Dust aDust) {
		if (chosenList == null)
			chosenList = new ArrayList();

		chosenList.add(aDust);
	}
	private static int addDustToList(Dust aDust) {
		if (dustList == null) {
			dustList = new ArrayList();
		}

		dustList.add(aDust);

		// The following two if's are logically correct,
		// but not sure when it happens.
		if (aDust.bFiltered) {
			filteredList.add(aDust);
			assert false;
		}

		if (aDust.bSelected) {
			selectedList.add(aDust);
			assert false;
		}

		return dustList.size() - 1;
	}
	private static void addFilteredDust(Dust aDust) {
		if (filteredList == null)
			filteredList = new ArrayList();

		filteredList.add(aDust);
	}
	private static void addSelectedDust(Dust aDust) {
		if (selectedList == null)
			selectedList = new ArrayList();

		selectedList.add(aDust);
	}

	private static ArrayList getNeighborDusts(Dust aDust) {
		assert dustList.size() > 0;

		ArrayList neighborList = new ArrayList();
		int idx = dustList.indexOf(aDust);

		for (int i = 0; i < idx; i++)
			if (distMatrix[idx][i] < aDust.getWidth())
				neighborList.add(dustList.get(i));

		for (int i = idx + 1; i < dustList.size(); i++)
			if (distMatrix[i][idx] < aDust.getWidth())
				neighborList.add(dustList.get(i));

		return neighborList;
	}
	private static void removeChosenDust(Dust aDust) {
		if (chosenList == null)
			return;

		chosenList.remove(aDust);
	}

	private static void removeFilteredDust(Dust aDust) {
		if (filteredList == null)
			return;

		filteredList.remove(aDust);
	}
	private static void removeSelectedDust(Dust aDust) {
		if (selectedList == null)
			return;

		selectedList.remove(aDust);
	}
	static public void setUnchosenAll() {
		if (chosenList == null)
			return;

		for (int i = chosenList.size() - 1; i >= 0; i--) {
			Dust dust = (Dust) chosenList.get(i);
			dust.setChosen(false);
		}
	}
	static public void setUnfilteredAll() {
		if (filteredList == null)
			return;

		for (int i = filteredList.size() - 1; i >= 0; i--) {
			Dust dust = (Dust) filteredList.get(i);
			dust.setFiltered(false);
		}
	}
	static public void setUnselectedAll() {
		if (selectedList == null)
			return;

		for (int i = selectedList.size() - 1; i >= 0; i--) {
			Dust dust = (Dust) selectedList.get(i);
			dust.setSelected(false);
		}
	}
	public static void setVarMeasures(ArrayList measures) {
		Dust.var_measures = measures;
	}

	public static void setVarTypes(ArrayList types) {
		Dust.var_types = types;
	}

	/**
	 * update distance matrix among dusts.
	 * 
	 * @param idx
	 *            If idx == -1, update the distance matrix completely.
	 *            Otherwise, update the distances from dust(id == idx).
	 */
	static public void updateDistMatrix(int idx) {
		int n = dustList.size();

		assert (idx >= -1 && idx < n);
		assert (n > 0);

		if (distMatrix == null || idx == -1) {
			distMatrix = new double[n][n];

			for (int i = 0; i < n; i++) {
				Dust di = (Dust) dustList.get(i);
				if (di.getFiltered())
					continue;
				Point2D pi = di.getOffset();
				for (int j = 0; j < i; j++) {
					Dust dj = (Dust) dustList.get(j);
					if (dj.getFiltered())
						continue;
					Point2D pj = dj.getOffset();
					distMatrix[i][j] = pi.distance(pj);
				}
			}
		} else {
			Dust di = (Dust) dustList.get(idx);
			Point2D pi = di.getOffset();
			for (int j = 0; j < n; j++) {
				if (j == idx)
					continue;

				Dust dj = (Dust) dustList.get(j);
				if (dj.getFiltered())
					continue;
				Point2D pj = dj.getOffset();
				if (j < idx)
					distMatrix[idx][j] = pi.distance(pj);
				else
					distMatrix[j][idx] = pi.distance(pj);
			}
		}
	}

	private int accelLength;

	// because it is just selected
	private boolean bChosen; // Chosen for final review

	private boolean bFiltered;

	private boolean bImageMode;

	private boolean bIncludeImages;

	private boolean bSelected; // Just selected for detail view

	private boolean bSelectedRedraw; // Notify that it should be drawn

	private Ellipse2D ellipse;

	private int imageIndex;

	private ArrayList imageList;

	private int intID;

	private PText pText;

	// public void setSelected() {
	// isSelected = true;
	// Dust.addSelectedDust(this);
	// }
	//
	// public void setUnselected() {
	// isSelected = false;
	// Dust.removeSelectedDust(this);
	// }

	private ArrayList valueList;

	public Dust(ArrayList aValueList) {
		bSelected = false;
		bSelectedRedraw = false;
		bChosen = false;
		bFiltered = false;
		bIncludeImages = false;

		setValueList(aValueList);
		setPaint(Color.black);
		sizeDust = DUSTSIZE;
		ellipse = new Ellipse2D.Double(0.0, 0.0, sizeDust, sizeDust);
		super.setBounds(0.0, 0.0, sizeDust, sizeDust);
		intID = Dust.addDustToList(this);

		imageIndex = 0;
	}

	/**
	 * @param image
	 */
	public void addImage(Image image) {
		if (imageList == null) {
			imageList = new ArrayList();
			imageList.add(image);
			setImage(image);
			// PBounds p = getBounds();
			// setBounds(p.x, p.y, IMAGEWIDTH, IMAGEHEIGHT);

			bIncludeImages = true;
			bImageMode = true;

		} else
			imageList.add(image);
	}

	public void doAvoidNeighbors(Point2D direction) {
		ArrayList neighbors = Dust.getNeighborDusts(this);

		double dist;
		double distX;
		double distY;

		Point2D vector = new Point2D.Double();

		for (int i = 0; i < neighbors.size(); i++) {
			Dust aNeighbor = (Dust) neighbors.get(i);
			Point2D me = this.getOffset();
			Point2D other = aNeighbor.getOffset();

			dist = me.distance(other);
			if (dist > this.getWidth())
				continue;
			dist = (dist == 0) ? 0.1 : dist;
			distX = me.getX() - other.getX();
			distX = (distX == 0) ? (rndGenerator.nextDouble() - 0.5) : distX;
			distY = me.getY() - other.getY();
			distY = (distY == 0) ? (rndGenerator.nextDouble() - 0.5) : distY;

			if (!this.intersects(aNeighbor.getBounds().getBounds2D()))
				continue;

			if (direction != null) {
				vector.setLocation(5.0 - (me.getX() - other.getX())
						+ direction.getX(), 5.0 - (me.getY() - other.getY())
						+ direction.getY());
			} else {
				vector.setLocation((distX) / dist, (distY) / dist);
			}

			this.setOffset(this.getOffset().getX() + vector.getX(), this
					.getOffset().getY()
					+ vector.getY());
		}
	}

	/**
	 * @return
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (!(arg0 instanceof Dust))
			return false;

		Dust aDust = (Dust) arg0;

		return this.intID == aDust.intID;
	}

	public int getAccelLength() {
		return accelLength;
	}

	// public void setFiltered() {
	// if (isFiltered == true)
	// return;
	// else {
	// isFiltered = true;
	// repaint();
	// }
	//		
	// Dust.addFilteredDust(this);
	// }
	//
	// public void setUnfiltered() {
	// if (isFiltered == false)
	// isFiltered = false;
	// Dust.removeFilteredDust(this);
	// }

	public boolean getChosen() {
		return bChosen;
	}

	/**
	 * This nodes uses an internal Ellipse2D to define its shape.
	 */
	public Ellipse2D getEllipse() {
		if (ellipse == null)
			ellipse = new Ellipse2D.Double();
		return ellipse;
	}

	public boolean getFiltered() {
		return bFiltered;
	}

	/**
	 * @return
	 */
	public String getImageID() {
		return valueList.get(1) + "";
	}

	public boolean getSelected() {
		return bSelected;
	}

	public ArrayList getValueList() {
		return valueList;
	}

	// Non rectangular subclasses need to override this method so
	// that they will be picked correctly and will receive the
	// correct mouse events.
	public boolean intersects(Rectangle2D aBounds) {
		return getEllipse().intersects(aBounds);
	}

	public void moveToward(Magnet magnet) {
		moveToward(magnet, 1.0);
	}

	public void moveToward(Magnet magnet, double acceleration) {
		Point2D pointCurr = this.getOffset();
		Point2D pointAttract = magnet.getOffset();
		int var_idx = magnet.getVarIndex();
		Double value;

		String strType = (String) var_types.get(var_idx);
		String strMeasure = (String) var_measures.get(var_idx);

		Object obj = valueList.get(var_idx);
		Point2D direction;

		if (obj == null) {
			// If the value is not exist ("N/A"), the Dust point should not move
			direction = new Point2D.Double(0.0, 0.0);
		} else {
			if (strType.equals("D") && strMeasure.equals("Q")) {
				assert obj instanceof Double;
				value = (Double) obj;
			} else if ((strType.equals("I") && strMeasure.equals("Q"))
					|| (strType.equals("S") && strMeasure.equals("O"))) {
				assert obj instanceof Integer;
				value = new Double(((Integer) obj).doubleValue());
			} else {
				assert false;
				value = new Double(0.0);
			}

			direction = new Point2D.Double((pointAttract.getX() - pointCurr
					.getX())
					/ pointAttract.distance(pointCurr)
					* magnet.getAttraction(value.doubleValue()) / 10,
					(pointAttract.getY() - pointCurr.getY())
							/ pointAttract.distance(pointCurr)
							* magnet.getAttraction(value.doubleValue()) / 10);
		}

		double xNew, yNew;

		xNew = direction.getX() * acceleration + pointCurr.getX();
		yNew = direction.getY() * acceleration + pointCurr.getY();

		this.setOffset(xNew, yNew);

		// TODO Animation?
		// long currentTime = System.currentTimeMillis();
		// PActivity a1 = this.animateToBounds(xNew, yNew, this.getWidth(),
		// this.getHeight(), 1000);
		// a1.setStartTime(currentTime);
	}

	// Nodes that override the visual representation of their super
	// class need to override a paint method.
	public void paint(PPaintContext aPaintContext) {
		// Do not paint the filtered Dusts
		if (bFiltered)
			return;

		double s = aPaintContext.getScale();

		Graphics2D g2 = aPaintContext.getGraphics();

		if (s >= 2 && bIncludeImages) {
			if (bImageMode == false) {
				PBounds p = getBounds();
				setBounds(p.x, p.y, IMAGEWIDTH, IMAGEHEIGHT);

				if (bSelected) {
					PBounds tp = pText.getBounds();
					pText.setBounds(0, IMAGEHEIGHT, tp.width, tp.height);
					pText.setFont(new Font("Arial", Font.PLAIN, 3));

					bSelectedRedraw = false;
				}

				bImageMode = true;
			}

			if (bSelectedRedraw) {
				PBounds tp = pText.getBounds();
				pText.setBounds(0, IMAGEHEIGHT, tp.width, tp.height);
				pText.setFont(new Font("Arial", Font.PLAIN, 3));

				if (pText.getVisible() == false)
					pText.setVisible(true);

				bSelectedRedraw = false;
			}

			super.paint(aPaintContext); // draw pictures
		} else {
			if (bImageMode == true) {
				PBounds p = getBounds();
				setBounds(p.x, p.y, sizeDust, sizeDust);

				if (bSelected) {
					PBounds tp = pText.getBounds();
					pText.setBounds(0, 0, tp.width, tp.height);
					pText.setFont(new Font("Arial", Font.BOLD, 11));

					bSelectedRedraw = false;
				}

				bImageMode = false;
			}

			if (bSelectedRedraw) {
				PBounds tp = pText.getBounds();
				pText.setBounds(0, 0, tp.width, tp.height);
				pText.setFont(new Font("Arial", Font.BOLD, 11));

				if (pText.getVisible() == false)
					pText.setVisible(true);

				bSelectedRedraw = false;
			}

			g2.setPaint(getPaint());
			g2.fill(getEllipse());
		}

		if (bChosen) {
			g2.setPaint(Color.green);
			g2.draw(this.getBounds());
		}
	}

	/**
	 * This method is important to override so that the geometry of the ellipse
	 * stays consistent with the bounds geometry.
	 */
	public boolean setBounds(double x, double y, double width, double height) {
		if (super.setBounds(x, y, width, height)) {
			ellipse.setFrame(x, y, width, height);
			return true;
		}
		return false;
	}

	public void setChosen(boolean bChoose) {
		if (bChosen == bChoose)
			return;
		else {
			bChosen = bChoose;

			if (bChosen == true)
				Dust.addChosenDust(this);
			else
				Dust.removeChosenDust(this);

			repaint();
		}
	}

	public void setFiltered(boolean bFilter) {
		if (bFiltered == bFilter)
			return;
		else {
			bFiltered = bFilter;

			if (bFilter == true)
				Dust.addFilteredDust(this);
			else
				Dust.removeFilteredDust(this);

			repaint();
		}
	}

	public void setSelected(boolean bSelect) {
		if (bSelected == bSelect)
			return;
		else {
			bSelected = bSelect;

			if (bSelect == true) {
				String id = "" + valueList.get(0);
				pText = new PText(id);
				pText.setTextPaint(Color.red);
				pText.setVisible(false);
				this.addChild(pText);
				this.setChildrenPickable(false);
				Dust.addSelectedDust(this);

				bSelectedRedraw = true;
			} else {
				Dust.removeSelectedDust(this);
				this.removeChild(pText);

				bSelectedRedraw = true;
			}
			repaint();
		}
	}

	public void setSize(double size) {
		sizeDust = size;
		PBounds p = getBounds();
		setBounds(p.x, p.y, sizeDust, sizeDust);
		Rectangle2D r = ellipse.getFrame();
		ellipse.setFrame(r.getX(), r.getY(), sizeDust, sizeDust);
	}

	public void setValueList(ArrayList valueList) {
		this.valueList = valueList;
	}

	public void shuffleToNextImage() {
		imageIndex += 1;
		if (imageIndex == imageList.size())
			imageIndex = 0;
		setImage((Image) imageList.get(imageIndex));
		PBounds p = getBounds();
		setBounds(p.x, p.y, IMAGEWIDTH, IMAGEHEIGHT);
	}
}