/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: JSlider2.java 611 2006-03-12 22:43:49Z jyi $
 *  
 */
package edu.gatech.bme.cise;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.plaf.UIResource;

/**
 * @author jyi
 * 
 */
public class JSlider2 extends JSlider {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected int digitsunderperiod = 0;
	protected String digitspattern = "#";

	public JSlider2(int orientation, double dmin, double dmax, double dvalue,
			int digitsunderperiod) {
		int min = (int) Math.floor(dmin * Math.pow(10.0, digitsunderperiod));
		int max = (int) Math.ceil(dmax * Math.pow(10.0, digitsunderperiod));
		int value = (int) (dvalue * Math.pow(10.0, digitsunderperiod));
		this.digitsunderperiod = digitsunderperiod;
		
		digitspattern = "#";
		
		for (; dmax >= 1; dmax = dmax / 10)
			digitspattern += "#";
		
		digitspattern += ".";
		
		for (int i = 0; i < digitsunderperiod; i++)
			digitspattern += "#";

		checkOrientation(orientation);
		this.orientation = orientation;
		sliderModel = new DefaultBoundedRangeModel(value, 0, min, max);
		sliderModel.addChangeListener(changeListener);
		updateUI();
	}

	public JSlider2(int orientation, int min, int max, int value) {
		super(orientation, min, max, value);
	}

	public void setMinorTickSpacing(double d) {
		int n = (int) (d * Math.pow(10.0, digitsunderperiod));
		setMinorTickSpacing(n);
	}

	public void setMajorTickSpacing(double d) {
		int n = (int) (d * Math.pow(10.0, digitsunderperiod));
		setMajorTickSpacing(n);
	}

	public double getDoubleValue() {
		int n = getModel().getValue();
		return (n * Math.pow(0.1, digitsunderperiod));
	}

	public void setDoubleValue(double d) {
		int n = (int) (d * Math.pow(10.0, digitsunderperiod));
		setValue(n);
	}

	private void checkOrientation(int orientation) {
		switch (orientation) {
			case VERTICAL :
			case HORIZONTAL :
				break;
			default :
				throw new IllegalArgumentException(
						"orientation must be one of: VERTICAL, HORIZONTAL");
		}
	}

	public Hashtable createStandardLabels(int increment, int start) {
		if (start > getMaximum() || start < getMinimum()) {
			throw new IllegalArgumentException(
					"Slider label start point out of range.");
		}

		if (increment <= 0) {
			throw new IllegalArgumentException("Label incremement must be > 0");
		}

		class SmartHashtable extends Hashtable
				implements
					PropertyChangeListener {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			int increment = 0;
			int start = 0;
			boolean startAtMin = false;

			class LabelUIResource extends JLabel implements UIResource {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;
				public LabelUIResource(String text, int alignment) {
					super(text, alignment);
					setName("Slider.label");
				}
			}

			public SmartHashtable(int increment, int start) {
				super();
				this.increment = increment;
				this.start = start;
				startAtMin = start == getMinimum();
				createLabels();
			}

			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName().equals("minimum") && startAtMin) {
					start = getMinimum();
				}

				if (e.getPropertyName().equals("minimum")
						|| e.getPropertyName().equals("maximum")) {

					Enumeration keys = getLabelTable().keys();
					Object key = null;
					Hashtable hashtable = new Hashtable();

					// Save the labels that were added by the developer
					while (keys.hasMoreElements()) {
						key = keys.nextElement();
						Object value = getLabelTable().get(key);
						if (!(value instanceof LabelUIResource)) {
							hashtable.put(key, value);
						}
					}

					clear();
					createLabels();

					// Add the saved labels
					keys = hashtable.keys();
					while (keys.hasMoreElements()) {
						key = keys.nextElement();
						put(key, hashtable.get(key));
					}

					((JSlider) e.getSource()).setLabelTable(this);
				}
			}

			void createLabels() {
				for (int labelIndex = start; labelIndex <= getMaximum(); labelIndex += increment) {
					if (digitsunderperiod != 0) {
						Double dlabelIndex = new Double (labelIndex * Math.pow(0.1, digitsunderperiod));						
						NumberFormat formatter = new DecimalFormat(digitspattern);
					    String strLabel = formatter.format(dlabelIndex);
					    put(new Integer(labelIndex), new LabelUIResource(strLabel, JLabel.CENTER));
					} else {
						put(new Integer(labelIndex), new LabelUIResource(""
								+ labelIndex, JLabel.CENTER));
					}
				}
			}
		}

		SmartHashtable table = new SmartHashtable(increment, start);

		if (getLabelTable() != null
				&& (getLabelTable() instanceof PropertyChangeListener)) {
			removePropertyChangeListener((PropertyChangeListener) getLabelTable());
		}

		addPropertyChangeListener(table);

		return table;
	}
}

class JSliderLabels extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JSliderLabels() {
		setLocation(400, 200);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JSlider2 slider = new JSlider2(JSlider2.HORIZONTAL, 0.0, 0.98, 0.1, 2);
		slider.setMinorTickSpacing((0.9 - 0.0) / 10);
		slider.setMajorTickSpacing((0.9 - 0.0) / 10);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		JPanel jp = new JPanel();
		jp.add(slider);
		getContentPane().add(jp);
		pack();
	}

	public static void main(String[] args) {
		new JSliderLabels().setVisible(true);
	}
}