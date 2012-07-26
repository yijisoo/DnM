/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: PaneSize.java 625 2006-05-23 03:53:03Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class PaneSize extends PaneData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneSize");

	protected JComboBox varCombo;

	protected PaneSizeSelect sizeselPane;

	public PaneSize() {
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

		// String[] strVarNames = new String[OPane.var_names.size()];
		// var_names.toArray(strVarNames);

		varCombo = new JComboBox(strVarNames);
		varCombo.setEditable(false);
		varCombo.setMaximumRowCount(4);

		varCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox box = (JComboBox) e.getSource();
				String varname = (String) box.getSelectedItem();
				int idx = sizeselPane.getIndexFromVarName(varname);
				sizeselPane.setCurrentVar(idx);
			}
		});

		// colorselPane

		sizeselPane = new PaneSizeSelect();

		GridBagConstraints constraints = new GridBagConstraints();

		// btnApply

		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				log.info("Apply button");
				sizeselPane.apply();
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
		add(sizeselPane, constraints);

		constraints.weightx = 1.0;
		constraints.weighty = 0.15;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(btnApply, constraints);

		varCombo.setSelectedIndex(-1);
	}

	public PaneSizeSelect getSizeSelectPane() {
		log.info("");
		return sizeselPane;
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		int n;
		n = in.readInt();
		log.info("in.readInt() = " + n);

		String varname = sizeselPane.getVarNameFromIndex(n);
		int nComboIndex = sizeselPane.getComboIndexFromVarName(varname);
		varCombo.setSelectedIndex(nComboIndex);

		sizeselPane.loadSnapshot(in);
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		sizeselPane.saveSnapshot(out);
	}
}

class PaneSizeSelect extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneSizeSelect");

	private int idx = -1;

	private String strMeasure;

	private String strType;

	private JLabel[] lblList;

	private JTextField[] textList;

	public PaneSizeSelect() {
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

	public String getVarNameFromIndex(int idx) {
		if (idx < 0 || idx >= PaneData.var_names.size())
			return null;

		return (String) PaneData.var_names.get(idx);
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

	public int getComboIndexFromVarName(String varName) {
		log.info("varName = " + varName);
		PaneSize pane = (PaneSize) getParent();

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

	public String getVarNameFromComboIndex(int idx) {
		log.info("idx = " + idx);

		PaneSize pane = (PaneSize) getParent();

		assert pane.varCombo != null;

		return (String) pane.varCombo.getItemAt(idx);
	}

	public int getCurrentVar() {
		log.info("");
		return idx;
	}

	// public JTextField[] getTextField() {
	// log.info("");
	// return textList;
	// }

	public void setCurrentVar(int idx) {
		log.info("idx = " + idx);

		assert (idx >= -1 && idx < PaneData.var_names.size());

		setVisible(false);

		this.removeAll();
		this.idx = idx;

		if (idx == -1) {
			log.severe("Size Pane - Variable unselected.");
			setVisible(true);
			apply();
			return;
		}

		GridBagConstraints constraints = new GridBagConstraints();
		setLayout(new GridBagLayout());
		constraints.fill = GridBagConstraints.BOTH;

		strMeasure = (String) PaneData.var_measures.get(idx);
		strType = (String) PaneData.var_types.get(idx);
		log.severe("Size Pane - [" + PaneData.var_names.get(idx)
				+ "] is selected.");

		if (strType.equals("S") && strMeasure.equals("O")) {
			int min = ((Integer) PaneData.values_min.get(idx)).intValue();
			int max = ((Integer) PaneData.values_max.get(idx)).intValue();

			lblList = new JLabel[max - min + 1];
			textList = new JTextField[max - min + 1];

			DataLabel label = (DataLabel) PaneData.var_labels.get(idx);

			for (int i = 0; i <= max - min; i++) {
				lblList[i] = new JLabel(label.get(min + i));
				lblList[i].setHorizontalAlignment(JTextField.LEFT);
				textList[i] = new JTextField(Double.toString(Dust.DUSTSIZE));
				textList[i].setHorizontalAlignment(JTextField.RIGHT);

				constraints.weighty = 1.0 / (max - min);
				constraints.weightx = 0.4;
				constraints.gridx = 0;
				constraints.gridy = i;
				add(lblList[i], constraints);

				constraints.weightx = 0.6;
				constraints.gridx = 1;
				constraints.gridy = i;
				add(textList[i], constraints);
			}
		} else if (strMeasure.equals("Q")) {
			Object min, max;
			if (strType.equals("I")) {
				min = (Integer) PaneData.values_min.get(idx);
				max = (Integer) PaneData.values_max.get(idx);
			} else if (strType.equals("D")) {
				min = (Double) PaneData.values_min.get(idx);
				max = (Double) PaneData.values_max.get(idx);
			} else {
				assert false;
				min = new Integer(0);
				max = new Integer(0);
			}

			lblList = new JLabel[2];
			textList = new JTextField[2];

			for (int i = 0; i <= 1; i++) {
				lblList[i] = new JLabel((i == 0
						? "min: " + min.toString()
						: "max: " + max.toString()));
				lblList[i].setHorizontalAlignment(JLabel.CENTER);
				textList[i] = new JTextField(Double.toString(Dust.DUSTSIZE));
				textList[i].setHorizontalAlignment(JTextField.RIGHT);

				constraints.weightx = 0.5;
				constraints.weighty = 0.4;
				constraints.gridx = i;
				constraints.gridy = 0;
				add(lblList[i], constraints);

				constraints.weighty = 0.6;
				constraints.gridx = i;
				constraints.gridy = 1;
				add(textList[i], constraints);
			}
		} else
			assert false;

		this.setVisible(true);
		apply();
	}

	public void apply() {
		log.info("");
		if (idx == -1) {
			for (int i = 0; i < PaneData.dustList.length; i++) {
				Dust aDust = PaneData.dustList[i];
				aDust.setSize(8.0);
			}
		} else if (strType.equals("S") && strMeasure.equals("O")) {
			for (int i = 0; i < PaneData.dustList.length; i++) {
				Dust aDust = PaneData.dustList[i];
				ArrayList values = aDust.getValueList();
				Object obj = values.get(idx);
				if (obj != null) {
					int min = ((Integer) PaneData.values_min.get(idx))
							.intValue();
					String strValue = textList[((Integer) obj).intValue() - min]
							.getText();
					aDust.setSize(Double.parseDouble(strValue));
				}
			}

			String s = "";
			for (int i = 0; i < textList.length; i++) {
				s = s + "[" + lblList[i].getText() + "] " + textList[i].getText();
				if (i < textList.length - 1)
					s = s + ", ";
			}
			log.severe("Size Pane: Sizes[" + PaneData.var_names.get(idx)
					+ "] chagned to " + s);
		} else if (strMeasure.equals("Q")) {
			Double min, max;
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
			}

			double minSize = Double.parseDouble(textList[0].getText());
			double maxSize = Double.parseDouble(textList[1].getText());

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
					double size = minSize + (maxSize - minSize)
							* (value.doubleValue() - min.doubleValue())
							/ (max.doubleValue() - min.doubleValue());
					aDust.setSize(size);
				}
			}
			log.severe("Size Pane: Sizes[" + PaneData.var_names.get(idx)
					+ "] chagned to (" + minSize + "~" + maxSize + ")");
		} else
			assert false;
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		log.info("");

		int nTextLen = in.readInt();
		// assert nTextLen == textList.length;

		if (nTextLen > 0) {
			log.info("nTextLen == " + nTextLen);
			for (int i = 0; i < nTextLen; i++) {
				double d = in.readDouble();
				textList[i].setText(Double.toString(d));
			}
			apply();
		} else {
			log.info("nTextLen == " + nTextLen);
		}
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		out.writeInt(idx);

		int nTextField = 0;
		if (textList == null)
			nTextField = 0;
		else
			nTextField = textList.length;

		out.writeInt(nTextField);
		for (int i = 0; i < nTextField; i++) {
			String strValue = textList[i].getText();
			double d = (Double.valueOf(strValue)).doubleValue();
			out.writeDouble(d);
		}
	}
}
