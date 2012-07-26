/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: PaneColor.java 625 2006-05-23 03:53:03Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Color Tab
 * 
 * @author jyi
 * 
 */

class PaneColor extends PaneData {
	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneColor");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private PaneColorSelect colorselPane;

	protected JComboBox varCombo;

	public PaneColor() {
		log.info("");

		// varCombo

		ArrayList listVar = new ArrayList();
		for (int i = 0; i < PaneData.var_names.size(); i++) {
			String strMeasure = (String) PaneData.var_measures.get(i);
			if (strMeasure.equals("O") || strMeasure.equals("Q"))
				listVar.add(PaneData.var_names.get(i));
		}

		String[] strVarNames = new String[listVar.size()];
		listVar.toArray(strVarNames);

		varCombo = new JComboBox(strVarNames);
		varCombo.setEditable(false);
		varCombo.setMaximumRowCount(4);

		varCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				String varname = (String) box.getSelectedItem();
				int i = colorselPane.getIndexFromVarName(varname);
				colorselPane.setCurrentVar(i);
			}
		});

		// colorselPane

		colorselPane = new PaneColorSelect();
		GridBagConstraints constraints = new GridBagConstraints();

		// btnApply

		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				log.info("Apply button.");
				colorselPane.apply();
			}
		});

		setLayout(new GridBagLayout());

		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 1.0;
		constraints.weighty = 0.15;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(varCombo, constraints);

		constraints.weightx = 1.0;
		constraints.weighty = 0.70;
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(colorselPane, constraints);

		constraints.weightx = 1.0;
		constraints.weighty = 0.15;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(btnApply, constraints);

		varCombo.setSelectedIndex(-1);
	}

	public PaneColorSelect getColorSelectPane() {
		log.info("");
		return colorselPane;
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		int n;
		n = in.readInt();
		log.info("in.readInt() = " + n);

		String varname = colorselPane.getVarNameFromIndex(n);
		int nComboIndex = colorselPane.getComboIndexFromVarName(varname);
		varCombo.setSelectedIndex(nComboIndex);

		// colorselPane.setCurrentVar(idx);

		colorselPane.loadSnapshot(in);
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		colorselPane.saveSnapshot(out);
	}
}

class PaneColorSelect extends JPanel {
	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneColorSelect");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JButton[] btnList;

	final Color[] defColors = {Color.red, Color.green, Color.blue, Color.pink,
			Color.magenta, Color.cyan, Color.orange, Color.yellow};

	private int idx = -1;

	private String strMeasure;

	private String strType;

	public PaneColorSelect() {
		super();
		log.info("");
	}

	// public void setCurrentVar(String varName) {
	// log.info("varName = " + varName);
	// for (int i = 0; i < OPane.var_names.size(); i++) {
	// String strName = (String) OPane.var_names.get(i);
	// if (strName.equals(varName))
	// setCurrentVar(i);
	// }
	// }

	public void apply() {
		log.info("");

		if (idx == -1) {
			for (int i = 0; i < PaneData.dustList.length; i++) {
				Dust aDust = PaneData.dustList[i];
				aDust.setPaint(Color.black);
			}
		} else if (strType.equals("S") && strMeasure.equals("O")) {
			for (int i = 0; i < PaneData.dustList.length; i++) {
				Dust aDust = PaneData.dustList[i];
				ArrayList values = aDust.getValueList();
				Object obj = values.get(idx);
				if (obj != null) {
					int min = ((Integer) PaneData.values_min.get(idx))
							.intValue();
					aDust.setPaint(btnList[((Integer) obj).intValue() - min]
							.getBackground());
				}
			}
		} else if (strMeasure.equals("Q")) {
			Double min, max;

			Color minColor = btnList[0].getBackground();
			Color maxColor = btnList[1].getBackground();
			if (strType.equals("I")) {
				min = new Double(((Integer) PaneData.values_min.get(idx))
						.doubleValue());
				max = new Double(((Integer) PaneData.values_max.get(idx))
						.doubleValue());
			} else if (strType.equals("D")) {
				min = (Double) PaneData.values_min.get(idx);
				max = (Double) PaneData.values_max.get(idx);
			} else {
				min = new Double(0.0);
				max = new Double(0.0);
				assert false;
			}

			for (int i = 0; i < PaneData.dustList.length; i++) {
				Dust aDust = PaneData.dustList[i];
				ArrayList values = aDust.getValueList();
				Double value;
				Object obj = values.get(idx);
				if (obj != null) {
					if (strType.equals("I")) {
						value = new Double(((Integer) obj).doubleValue());
					} else if (strType.equals("D")) {
						value = (Double) obj;
					} else {
						value = new Double(0.0);
					}
					aDust.setPaint(getGradientColor(minColor, maxColor, min,
							max, value));
				}
			}
		} else
			assert false;
	}

	public int getComboIndexFromVarName(String varName) {
		log.info("varName = " + varName);
		PaneColor pane = (PaneColor) getParent();

		assert pane.varCombo != null;

		if (varName == null)
			return -1;

		for (int i = 0; i < pane.varCombo.getItemCount(); i++) {
			String strName = (String) pane.varCombo.getItemAt(i);
			if (strName.equals(varName))
				return i;
		}

		return -1;
	}

	public int getCurrentVar() {
		log.info("");
		return idx;
	}

	private Color getGradientColor(Color minColor, Color maxColor, Double min,
			Double max, Double value) {
		int r1 = minColor.getRed();
		int g1 = minColor.getGreen();
		int b1 = minColor.getBlue();

		int rd = maxColor.getRed() - r1;
		int gd = maxColor.getGreen() - g1;
		int bd = maxColor.getBlue() - b1;

		double m = min.doubleValue();
		double d = max.doubleValue() - m;
		double v = value.doubleValue();

		int r = (int) (r1 + (v - m) * rd / d);
		int g = (int) (g1 + (v - m) * gd / d);
		int b = (int) (b1 + (v - m) * bd / d);

		return new Color(r, g, b);
	}

	public int getIndexFromVarName(String varName) {
		log.info("varName = " + varName);
		if (varName == null)
			return -1;

		for (int i = 0; i < PaneData.var_names.size(); i++) {
			String strName = (String) PaneData.var_names.get(i);
			if (strName.equals(varName))
				return i;
		}
		return -1;
	}

	public String getVarNameFromComboIndex(int idx) {
		log.info("idx = " + idx);

		PaneColor pane = (PaneColor) getParent();

		assert pane.varCombo != null;

		return (String) pane.varCombo.getItemAt(idx);
	}

	public String getVarNameFromIndex(int idx) {
		if (idx < 0 || idx >= PaneData.var_names.size())
			return null;

		return (String) PaneData.var_names.get(idx);
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		log.info("");

		int nBtnLen = in.readInt();

		if (nBtnLen > 0) {
			log.info("nBtnLen == " + nBtnLen);
			assert (btnList != null && btnList.length > 0);

			for (int i = 0; i < nBtnLen; i++) {
				int r = in.readInt();
				int g = in.readInt();
				int b = in.readInt();

				btnList[i].setBackground(new Color(r, g, b));
			}

		} else {
			log.info("nBtnLen == " + nBtnLen);
		}
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		out.writeInt(idx);

		int nBtnLen;
		if (btnList == null)
			nBtnLen = 0;
		else
			nBtnLen = btnList.length;

		out.writeInt(nBtnLen);
		for (int i = 0; i < nBtnLen; i++) {
			Color c = btnList[i].getBackground();
			int r = c.getRed();
			int g = c.getGreen();
			int b = c.getBlue();

			out.writeInt(r);
			out.writeInt(g);
			out.writeInt(b);
		}
	}

	public void setCurrentVar(int idx) {
		log.info("idx = " + idx);

		assert (idx >= -1 && idx < PaneData.var_names.size());

		setVisible(false);

		this.removeAll();
		this.idx = idx;

		if (idx == -1) {
			log.severe("Color Pane - Variable unselected.");
			setVisible(true);
			apply();
			return;
		}

		strMeasure = (String) PaneData.var_measures.get(idx);
		strType = (String) PaneData.var_types.get(idx);

		log.severe("Color Pane - [" + PaneData.var_names.get(idx)
				+ "] is selected.");

		if (strType.equals("S") && strMeasure.equals("O")) {
			assert (PaneData.values_min.get(idx) instanceof Integer);
			assert (PaneData.values_max.get(idx) instanceof Integer);

			int min = ((Integer) PaneData.values_min.get(idx)).intValue();
			int max = ((Integer) PaneData.values_max.get(idx)).intValue();

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			btnList = new JButton[max - min + 1];

			for (int i = 0; i <= max - min; i++) {
				DataLabel label = (DataLabel) PaneData.var_labels.get(idx);
				btnList[i] = new JButton(label.get(min + i));

				if (i >= defColors.length)
					btnList[i].setBackground(Color.darkGray);
				else
					btnList[i].setBackground(defColors[i]);

				btnList[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						JButton btn = (JButton) ev.getSource();
						PaneColorSelect pcs = (PaneColorSelect) btn.getParent();
						
						Color c = JColorChooser.showDialog(null, "Color", btn
								.getBackground());
						
						log.severe("Color Pane - Color changed ["
								+ PaneData.var_names.get(pcs.idx) + "] from ["
								+ btn.getBackground().toString() + "] to ["
								+ c.toString() + "]");
						
						btn.setBackground(c);
						pcs.apply();
					}
				});
				add(btnList[i]);
				repaint();
			}
		} else if (strMeasure.equals("Q")) {
			Object min, max;
			if (strType.equalsIgnoreCase("I")) {
				min = (Integer) PaneData.values_min.get(idx);
				max = (Integer) PaneData.values_max.get(idx);
			} else if (strType.equalsIgnoreCase("D")) {
				min = (Double) PaneData.values_min.get(idx);
				max = (Double) PaneData.values_max.get(idx);
			} else {
				// assert false;
				min = new Integer(0);
				max = new Integer(0);
			}

			setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;

			btnList = new JButton[2];
			JLabel[] lblList = new JLabel[2];

			for (int i = 0; i <= 1; i++) {
				btnList[i] = new JButton("");
				if (i == 0)
					btnList[i].setBackground(Color.lightGray);
				else
					btnList[i].setBackground(Color.black);

				btnList[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ev) {
						JButton btn = (JButton) ev.getSource();
						PaneColorSelect pcs = (PaneColorSelect) btn.getParent();
						
						Color c = JColorChooser.showDialog(null, "Color", btn
								.getBackground());
						
						log.severe("Color Pane - Color changed ["
								+ PaneData.var_names.get(pcs.idx) + "] from ["
								+ btn.getBackground().toString() + "] to ["
								+ c.toString() + "]");

						btn.setBackground(c);
						pcs.apply();
					}
				});

				lblList[i] = new JLabel((i == 0)
						? ("min: " + min.toString())
						: ("max: " + max.toString()));
				lblList[i].setHorizontalAlignment(JLabel.CENTER);

				constraints.weightx = 0.5;
				constraints.weighty = 0.4;
				constraints.gridx = i;
				constraints.gridy = 0;
				add(lblList[i], constraints);

				constraints.weightx = 0.5;
				constraints.weighty = 0.6;
				constraints.gridx = i;
				constraints.gridy = 1;
				add(btnList[i], constraints);
			}
		} else
			assert false;

		this.setVisible(true);
		apply();
	}
}
