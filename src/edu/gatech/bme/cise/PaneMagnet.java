/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: PaneMagnet.java 625 2006-05-23 03:53:03Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Magnet Tab
 * 
 * @author jyi
 * 
 */

class PaneMagnet extends PaneData {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneMagnet");

	JComboBox varCombo;

	PaneMagnetSelect magnetselPane;

	public PaneMagnet() {
		refresh();
	}

	public void refresh() {
		setVisible(false);
		removeAll();

		// varCombo

		String[] strMagnetNames = new String[PaneData.magnetList.size()];
		for (int i = 0; i < PaneData.magnetList.size(); i++) {
			strMagnetNames[i] = ((Magnet) PaneData.magnetList.get(i))
					.getMagnetName();
		}

		varCombo = new JComboBox(strMagnetNames);
		varCombo.setEditable(false);
		varCombo.setMaximumRowCount(4);

		varCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				magnetselPane.setCurrentVar(((JComboBox) e.getSource())
						.getSelectedIndex());
			}
		});

		// magnetselPane

		magnetselPane = new PaneMagnetSelect();

		GridBagConstraints constraints = new GridBagConstraints();

		// btnApply

		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				log.info("Apply button.");
				magnetselPane.apply();
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
		add(magnetselPane, constraints);

		constraints.weightx = 1.0;
		constraints.weighty = 0.15;
		constraints.gridx = 0;
		constraints.gridy = 2;
		add(btnApply, constraints);

		setVisible(true);
	}

	public void selectMagnet(String strMagnet) {
		String currSelection = (String) varCombo.getSelectedItem();
		if (!currSelection.equals(strMagnet))
			varCombo.setSelectedItem(strMagnet);

		magnetselPane.setCurrentVar(strMagnet);
	}
}

class PaneMagnetSelect extends JPanel {
	/**
	 * 
	 */
	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.PaneMagnetSelect");

	private static final long serialVersionUID = 1L;

	private int var_idx = -1;

	private int mag_idx = -1;

	private String strMeasure;

	private String strType;

	private JSlider sliderMag;

	private JSlider2 sliderRepel;

	private JCheckBox[] chkboxList;

	public PaneMagnetSelect() {
		super();
	}

	public void setCurrentVar(String strMag) {
		for (int i = 0; i < PaneData.magnetList.size(); i++) {
			Magnet magnet = (Magnet) PaneData.magnetList.get(i);
			String mag_name = magnet.getMagnetName();
			if (strMag.equals(mag_name))
				setCurrentVar(i);
		}
	}

	public void setCurrentVar(int mag_idx) {
		assert (mag_idx >= -1 && mag_idx < PaneData.magnetList.size());

		setVisible(false);

		this.removeAll();
		this.mag_idx = mag_idx;

		if (mag_idx == -1) {
			log.severe("Magnet Pane - Magnet unselected.");
			setVisible(true);
			return;
		}

		Magnet mag = (Magnet) PaneData.magnetList.get(mag_idx);
		String mag_name = mag.getMagnetName();
		
		for (int i = 0; i < PaneData.var_names.size(); i++) {
			String var_name = (String) PaneData.var_names.get(i);
			if (var_name.equalsIgnoreCase(mag_name)) {
				this.var_idx = i;
				break;
			}
		}

		log.severe("Magnet Pane - [" + PaneData.var_names.get(var_idx)
				+ "] is selected.");

		GridBagConstraints constraints = new GridBagConstraints();
		setLayout(new GridBagLayout());
		constraints.fill = GridBagConstraints.BOTH;

		strMeasure = (String) PaneData.var_measures.get(var_idx);
		strType = (String) PaneData.var_types.get(var_idx);
		if (strType.equals("S") && strMeasure.equals("O")) {
			JLabel label1 = new JLabel("Magnitude");
			constraints.weightx = 1.0;
			constraints.weighty = 0.1;
			constraints.gridx = 0;
			constraints.gridy = 0;
			add(label1, constraints);

			sliderMag = new JSlider(JSlider.HORIZONTAL, 0, 20, mag
					.getMagnitude());
			sliderMag.setMinorTickSpacing(1);
			sliderMag.setMajorTickSpacing(5);
			sliderMag.setPaintTicks(true);
			sliderMag.setPaintLabels(true);
			sliderMag.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider slider = (JSlider) e.getSource();
					PaneMagnetSelect pms = (PaneMagnetSelect) slider.getParent();
					pms.apply();
					log.severe("Magnet Pane - Magnitude ["
							+ PaneData.var_names.get(pms.var_idx)
							+ "] is changed to [" + slider.getValue() + "]");
				}
			});

			constraints.weightx = 1.0;
			constraints.weighty = 0.2;
			constraints.gridx = 0;
			constraints.gridy = 1;
			add(sliderMag, constraints);

			JLabel label2 = new JLabel("Repellent");
			constraints.weightx = 1.0;
			constraints.weighty = 0.1;
			constraints.gridx = 0;
			constraints.gridy = 2;
			add(label2, constraints);

			assert PaneData.values_min.get(var_idx) instanceof Integer;
			assert PaneData.values_max.get(var_idx) instanceof Integer;

			int min = ((Integer) PaneData.values_min.get(var_idx)).intValue();
			int max = ((Integer) PaneData.values_max.get(var_idx)).intValue();

			chkboxList = new JCheckBox[max - min + 1];

			ArrayList repelList = mag.getRepelList();

			for (int i = 0; i <= max - min; i++) {
				DataLabel label = (DataLabel) PaneData.var_labels.get(var_idx);
				chkboxList[i] = new JCheckBox(label.get(min + i) + " ("
						+ Integer.toString(min + i) + ")");
				if (repelList == null)
					chkboxList[i].setSelected(false);
				else
					chkboxList[i].setSelected(((Boolean) repelList.get(i))
							.booleanValue());

				constraints.weightx = 1.0;
				constraints.weighty = 0.05;
				constraints.gridx = 0;
				constraints.gridy = 3 + i;
				add(chkboxList[i], constraints);
			}
		} else if (strMeasure.equals("Q")) {
			JLabel label1 = new JLabel("Magnitude");
			constraints.weightx = 1.0;
			constraints.weighty = 0.1;
			constraints.gridx = 0;
			constraints.gridy = 0;
			add(label1, constraints);

			sliderMag = new JSlider(JSlider.HORIZONTAL, 0, 20, mag
					.getMagnitude());
			sliderMag.setMinorTickSpacing(1);
			sliderMag.setMajorTickSpacing(5);
			sliderMag.setPaintTicks(true);
			sliderMag.setPaintLabels(true);
			sliderMag.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider slider = (JSlider) e.getSource();
					PaneMagnetSelect pms = (PaneMagnetSelect) slider.getParent();
					pms.apply();
					log.severe("Magnet Pane - Magnitude ["
							+ PaneData.var_names.get(pms.var_idx)
							+ "] is changed to [" + slider.getValue() + "]");
				}
			});

			constraints.weightx = 1.0;
			constraints.weighty = 0.2;
			constraints.gridx = 0;
			constraints.gridy = 1;
			add(sliderMag, constraints);

			JLabel label2 = new JLabel("Repellent");
			constraints.weightx = 1.0;
			constraints.weighty = 0.1;
			constraints.gridx = 0;
			constraints.gridy = 2;
			add(label2, constraints);

			// TODO fix this part

			Double threshold = new Double(mag.getThreshold());
			if (strType.equals("I")) {
				int min, max;
				min = ((Integer) PaneData.values_min.get(var_idx)).intValue();
				max = ((Integer) PaneData.values_max.get(var_idx)).intValue();

				sliderRepel = new JSlider2(JSlider.HORIZONTAL, min, max,
						threshold.intValue());
				sliderRepel.setMinorTickSpacing((max - min) / 10);
				sliderRepel.setMajorTickSpacing((max - min) / 2);
				sliderRepel.setPaintTicks(true);
				sliderRepel.setPaintLabels(true);
				sliderRepel.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JSlider2 slider = (JSlider2) e.getSource();
						PaneMagnetSelect pms = (PaneMagnetSelect) slider.getParent();
						pms.apply();
						log.severe("Magnet Pane - Repellent ["
								+ PaneData.var_names.get(pms.var_idx)
								+ "] is changed to [" + slider.getValue() + "]");
					}
				});
			} else if (strType.equals("D")) {
				double dmin, dmax;
				dmin = ((Double) PaneData.values_min.get(var_idx))
						.doubleValue();
				dmax = ((Double) PaneData.values_max.get(var_idx))
						.doubleValue();
				sliderRepel = new JSlider2(JSlider.HORIZONTAL, dmin, dmax,
						threshold.doubleValue(), 1);
				sliderRepel.setMinorTickSpacing((dmax - dmin) / 10);
				sliderRepel.setMajorTickSpacing((dmax - dmin) / 2);
				sliderRepel.setPaintTicks(true);
				sliderRepel.setPaintLabels(true);
				sliderRepel.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						JSlider2 slider = (JSlider2) e.getSource();
						PaneMagnetSelect pms = (PaneMagnetSelect) slider.getParent();
						pms.apply();
						log.severe("Magnet Pane - Repellent ["
								+ PaneData.var_names.get(pms.var_idx)
								+ "] is changed to [" + slider.getDoubleValue() + "]");
					}
				});
			} else {
				assert false;
			}

			constraints.weightx = 1.0;
			constraints.weighty = 0.2;
			constraints.gridx = 0;
			constraints.gridy = 3;
			add(sliderRepel, constraints);
		} else
			assert false;

		this.setVisible(true);
	}

	public void apply() {
		if (mag_idx == -1)
			return;

		Magnet magnet = (Magnet) PaneData.magnetList.get(mag_idx);
		if (strType.equals("S") && strMeasure.equals("O")) {
			ArrayList repelList = new ArrayList();
			for (int i = 0; i < chkboxList.length; i++) {
				repelList.add(new Boolean(chkboxList[i].isSelected()));
			}
			magnet.setRepelList(repelList);
			magnet.setMagnitude(sliderMag.getValue());
		} else if (strMeasure.compareTo("Q") == 0) {
			magnet.setMagnitude(sliderMag.getValue());
			magnet.setThreshold(sliderRepel.getDoubleValue());
		} else
			assert false;
	}
}
