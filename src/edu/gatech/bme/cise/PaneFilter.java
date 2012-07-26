/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: PaneFilter.java 625 2006-05-23 03:53:03Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.Component;
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.ac.gla.dcs.DoubleSlider;
import uk.ac.gla.dcs.DoubleSliderAdjustmentListener;

/**
 * Filter Tab
 * 
 * @author jyi
 * 
 */

class PaneFilterSelect extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneFilterSelect");

	private int idx = -1;

	private String strMeasure;

	private String strType;

	private JCheckBox[] checkboxList;

	private DoubleSlider doubleSlider;

	private JLabel[] lblList;

	public PaneFilterSelect() {
		super();
		for (int i = 0; i < PaneData.var_names.size(); i++) {
			strMeasure = (String) PaneData.var_measures.get(i);
			strType = (String) PaneData.var_types.get(i);
			if ((strType.equals("S") && strMeasure.equals("O"))
					|| strMeasure.equals("Q")) {
				setCurrentVar(i);
				break;
			}
		}
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		out.writeInt(idx);

		out.writeChar((int) strType.charAt(0));
		out.writeChar((int) strMeasure.charAt(0));

		if (strType.equals("S") && strMeasure.equals("O")) {
			out.writeInt(checkboxList.length);
			for (int i = 0; i < checkboxList.length; i++) {
				boolean bSelected = checkboxList[i].isSelected();
				out.writeBoolean(bSelected);
			}
		} else if (strMeasure.equals("Q")) {
			double selMin = doubleSlider.getSelectedMinimum();
			double selMax = doubleSlider.getSelectedMaximum();
			out.writeDouble(selMin);
			out.writeDouble(selMax);
		} else
			assert false;
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		int n = in.readInt();
		setCurrentVar(n);

		PaneFilterItem f = (PaneFilterItem) getParent();
		f.varCombo
				.setSelectedIndex(getComboIndexFromVarName(getVarNameFromIndex(n)));

		strType = in.readChar() + "";
		strMeasure = in.readChar() + "";

		if (strType.equals("S") && strMeasure.equals("O")) {
			int nCheckList = in.readInt();
			assert (nCheckList == checkboxList.length);
			for (int i = 0; i < nCheckList; i++) {
				boolean bSelected = in.readBoolean();
				checkboxList[i].setSelected(bSelected);
			}
		} else if (strMeasure.equals("Q")) {
			double selMin = in.readDouble();
			double selMax = in.readDouble();
			doubleSlider.setSelectedMinimum(selMin);
			doubleSlider.setSelectedMaximum(selMax);
		} else
			assert false;

		applyFilter();
	}

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
		PaneFilterItem pane = (PaneFilterItem) getParent();

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

		PaneFilterItem pane = (PaneFilterItem) getParent();

		assert pane.varCombo != null;

		return (String) pane.varCombo.getItemAt(idx);
	}

	public void setCurrentVar(int idx) {
		assert (idx >= -1 && idx < PaneData.var_measures.size());

		if (this.idx == idx)
			return;

		setVisible(false);

		this.removeAll();
		this.idx = idx;

		if (idx == -1) {
			log.severe("Filter Pane - Variable unselected.");
			setVisible(true);
			applyFilter();
			return;
		}

		strMeasure = (String) PaneData.var_measures.get(idx);
		strType = (String) PaneData.var_types.get(idx);

		log.severe("Filter Pane - [" + PaneData.var_names.get(idx)
				+ "] is selected.");

		if (strType.equals("S") && strMeasure.equals("O")) {
			assert (PaneData.values_min.get(idx) instanceof Integer);
			assert (PaneData.values_max.get(idx) instanceof Integer);

			int min = ((Integer) PaneData.values_min.get(idx)).intValue();
			int max = ((Integer) PaneData.values_max.get(idx)).intValue();

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			checkboxList = new JCheckBox[max - min + 1];

			for (int i = 0; i <= max - min; i++) {
				DataLabel label = (DataLabel) PaneData.var_labels.get(idx);
				checkboxList[i] = new JCheckBox(label.get(min + i));
				checkboxList[i].setSelected(true);
				checkboxList[i].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JCheckBox c = (JCheckBox) e.getSource();
						PaneFilterSelect pfs = (PaneFilterSelect) (c
								.getParent());
						pfs.applyFilter();

						String s = "";
						for (int i = 0; i < pfs.checkboxList.length; i++) {
							s = s + "[" + pfs.checkboxList[i].getText() + "] "
									+ pfs.checkboxList[i].isSelected();
							if (i < pfs.checkboxList.length - 1)
								s = s + ", ";
						}
						log.severe("Filter Pane: Filter setting ["
								+ PaneData.var_names.get(pfs.idx)
								+ "] chagned to " + s);
					}
				});
				add(checkboxList[i]);
				repaint();
			}
		} else if (strMeasure.equals("Q")) {
			Object min, max;
			if (strType.equals("I")) {
				min = (Integer) PaneData.values_min.get(idx);
				max = (Integer) PaneData.values_max.get(idx);
				doubleSlider = new DoubleSlider(DoubleSlider.HORIZONTAL,
						((Integer) min).doubleValue(), ((Integer) max)
								.doubleValue());
			} else if (strType.equals("D")) {
				min = (Double) PaneData.values_min.get(idx);
				max = (Double) PaneData.values_max.get(idx);
				doubleSlider = new DoubleSlider(DoubleSlider.HORIZONTAL,
						((Double) min).doubleValue(), ((Double) max)
								.doubleValue());
			} else {
				assert false;
				min = new Integer(0);
				max = new Integer(0);
			}

			setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;

			lblList = new JLabel[2];

			lblList[0] = new JLabel(min.toString());
			lblList[0].setHorizontalAlignment(JLabel.LEFT);
			lblList[1] = new JLabel(max.toString());
			lblList[1].setHorizontalAlignment(JLabel.RIGHT);

			doubleSlider
					.addAdjustmentListener(new DoubleSliderAdjustmentListener() {
						public void adjustmentValueChanged(DoubleSlider slider) {
							PaneFilterSelect pfs = (PaneFilterSelect) (slider
									.getParent());
							pfs.applyFilter();

							double selMin = slider.getSelectedMinimum();
							double selMax = slider.getSelectedMaximum();
							log.severe("Filter Pane - The filter for ["
									+ PaneData.var_names.get(pfs.idx)
									+ "]is changed to [" + selMin + ", "
									+ selMax + "]");
						}
					});

			constraints.weightx = 0.1;
			constraints.weighty = 1;
			constraints.gridx = 0;
			constraints.gridy = 0;
			add(lblList[0], constraints);

			constraints.weightx = 0.8;
			constraints.weighty = 1;
			constraints.gridx = 1;
			constraints.gridy = 0;
			add(doubleSlider, constraints);

			constraints.weightx = 0.1;
			constraints.weighty = 1;
			constraints.gridx = 2;
			constraints.gridy = 0;
			add(lblList[1], constraints);
		} else
			assert false;

		this.setVisible(true);
	}

	public void applyFilter() {
		((PaneFilterItems) getParent().getParent()).applyFilter();
	}

	public boolean isFiltered(Dust aDust) {
		if (idx == -1) {
			return false;
		} else if (strType.equals("S") && strMeasure.equals("O")) {
			ArrayList values = aDust.getValueList();
			int min = ((Integer) PaneData.values_min.get(idx)).intValue();

			return !checkboxList[((Integer) values.get(idx)).intValue() - min]
					.isSelected();
		} else if (strMeasure.equals("Q")) {
			double selMin = doubleSlider.getSelectedMinimum();
			double selMax = doubleSlider.getSelectedMaximum();

			if (strType.equals("I")) {
				lblList[0].setText((int) selMin + "");
				lblList[1].setText((int) selMax + "");
			} else if (strType.equals("D")) {
				lblList[0].setText(((double) Math.round(selMin * 100)) / 100
						+ "");
				lblList[1].setText(((double) Math.round(selMax * 100)) / 100
						+ "");
			} else {
				assert false;
			}

			ArrayList values = aDust.getValueList();
			Double value;

			Object obj = values.get(idx);
			if (obj == null) {
				return false;
			} else {
				if (strType.equals("I")) {
					value = new Double(((Integer) values.get(idx))
							.doubleValue());
				} else if (strType.equals("D")) {
					value = (Double) values.get(idx);
				} else {
					value = new Double(0.0);
				}

				if (value.doubleValue() < selMin
						|| value.doubleValue() > selMax) {
					return true;
				} else
					return false;
			}
		}

		assert false;
		return false;
	}
}

class PaneFilterItem extends JPanel {
	/**
	 * 
	 */
	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneFilterItem");

	private static final long serialVersionUID = 1L;

	protected JComboBox varCombo;

	private PaneFilterSelect filterselPane;

	public PaneFilterItem() {
		super();

		// filterselPane

		filterselPane = new PaneFilterSelect();

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
				int idx = filterselPane.getIndexFromVarName(varname);
				filterselPane.setCurrentVar(idx);
			}
		});

		// Remove Button

		JButton btnX = new JButton("X");
		btnX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				JButton btn = (JButton) ev.getSource();
				PaneFilterItem filteritm = (PaneFilterItem) btn.getParent();
				log.severe("Filter Pane - Filter ["
						+ filteritm.varCombo.getSelectedItem()
						+ "] is removed.");
				removeFilterItem();
			}
		});

		GridBagConstraints constraints = new GridBagConstraints();
		setLayout(new GridBagLayout());
		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 0.95;
		constraints.weighty = 0.1;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(varCombo, constraints);

		constraints.weightx = 0.05;
		constraints.weighty = 0.1;
		constraints.gridx = 1;
		constraints.gridy = 0;
		add(btnX, constraints);

		constraints.weightx = 1.0;
		constraints.weighty = 0.9;
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(filterselPane, constraints);

		setVisible(true);
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		filterselPane.saveSnapshot(out);
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		filterselPane.loadSnapshot(in);
	}

	public void removeFilterItem() {
		((PaneFilterItems) getParent()).removeFilterItem(this);
	}

	public boolean isFiltered(Dust aDust) {
		return filterselPane.isFiltered(aDust);
	}
}

class PaneFilterItems extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneFilterItems");

	private ArrayList filterItemList;

	public PaneFilterItems() {
		super();
		filterItemList = new ArrayList();
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		log.info("");

		int nFilterItemList = 0;
		if (filterItemList != null)
			nFilterItemList = filterItemList.size();

		out.writeInt(nFilterItemList);

		for (int i = 0; i < nFilterItemList; i++) {
			PaneFilterItem f = (PaneFilterItem) filterItemList.get(i);
			f.saveSnapshot(out);
		}
	}

	public void loadSnapshot(DataInputStream in) throws IOException {
		int nFilterItemList = in.readInt();

		filterItemList.clear();

		for (int i = 0; i < nFilterItemList; i++) {
			PaneFilterItem f = new PaneFilterItem();
			filterItemList.add(f);
		}

		redrawFilterItems();

		for (int i = 0; i < nFilterItemList; i++) {
			PaneFilterItem f = (PaneFilterItem) filterItemList.get(i);
			f.loadSnapshot(in);
		}
	}

	public void redrawFilterItems() {
		setVisible(false);

		removeAll();

		if (filterItemList.size() > 0) {
			GridBagConstraints constraints = new GridBagConstraints();
			setLayout(new GridBagLayout());
			constraints.fill = GridBagConstraints.BOTH;

			for (int i = 0; i < filterItemList.size(); i++) {
				constraints.weightx = 1.0;
				constraints.weighty = 1.0 / filterItemList.size();
				constraints.gridx = 0;
				constraints.gridy = i;
				add((Component) filterItemList.get(i), constraints);
			}
		}

		setVisible(true);
	}

	public void addFilterItem() {
		filterItemList.add(new PaneFilterItem());
		redrawFilterItems();
	}

	public void removeFilterItem(PaneFilterItem aFilter) {
		filterItemList.remove(aFilter);
		redrawFilterItems();
		applyFilter();
	}

	public void applyFilter() {
		for (int i = 0; i < PaneData.dustList.length; i++) {
			boolean isFiltered = false;
			Dust aDust = PaneData.dustList[i];
			for (int j = 0; j < filterItemList.size(); j++) {

				isFiltered = isFiltered
						|| ((PaneFilterItem) filterItemList.get(j))
								.isFiltered(aDust);
			}
			aDust.setFiltered(isFiltered);
		}
	}
}

class PaneFilter extends PaneData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneFilter");

	PaneFilterItems filteritemsPane;

	public PaneFilter() {
		log.info("");

		filteritemsPane = new PaneFilterItems();

		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				addFilterItem();
			}
		});

		/*
		 * JButton btnApply = new JButton("Apply");
		 * btnApply.addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent ev) { applyFilter(); } });
		 */

		GridBagConstraints constraints = new GridBagConstraints();
		setLayout(new GridBagLayout());
		constraints.fill = GridBagConstraints.BOTH;

		constraints.weightx = 1.0;
		constraints.weighty = 0.9;
		constraints.gridx = 0;
		constraints.gridy = 0;
		add(filteritemsPane, constraints);

		constraints.weightx = 1.0;
		constraints.weighty = 0.1;
		constraints.gridx = 0;
		constraints.gridy = 1;
		add(btnAdd, constraints);
	}

	public void addFilterItem() {
		filteritemsPane.addFilterItem();
	}

	public void saveSnapshot(DataOutputStream out) throws IOException {
		filteritemsPane.saveSnapshot(out);
	}

	public void loadSnapShot(DataInputStream in) throws IOException {
		filteritemsPane.loadSnapshot(in);
	}
}
