/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: DetailWindow.java 593 2006-03-10 20:37:00Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

class DetailTableModel extends AbstractTableModel {
	static Logger log = Logger
			.getLogger("edu.gatech.isye.lhci.DetailTableModel");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList dustChosenList;
	private ArrayList dustSelectedList;
	private ArrayList var_labels;
	private ArrayList var_measures;
	private ArrayList var_names;
	private ArrayList var_types;

	public DetailTableModel() {
		super();
	}

	public DetailTableModel(ArrayList names, ArrayList types,
			ArrayList measures, ArrayList labels) {
		this();

		dustSelectedList = new ArrayList();
		dustChosenList = new ArrayList();

		var_names = names;
		var_types = types;
		var_measures = measures;
		var_labels = labels;
	}

	public void addChosenDust(Dust aDust) {
		for (int i = 0; i < dustChosenList.size(); i++) {
			if (aDust == (Dust) dustChosenList.get(i)) {
				log.info("Same Chosen Dust added.");
				return;
			}
		}

		if (dustChosenList.size() >= 5) {
			log.info("Dust list is full.");
			dustChosenList.remove(0);
		}

		dustChosenList.add(aDust);

		fireTableStructureChanged();
	}

	public void addSelectedDust(Dust aDust) {
		for (int i = 0; i < dustSelectedList.size(); i++) {
			if (aDust == (Dust) dustSelectedList.get(i)) {
				log.info("Same Dust added.");
				return;
			}
		}

		if (dustSelectedList.size() >= 15) {
			log.info("Dust list is full.");
			dustSelectedList.remove(0);
		}

		dustSelectedList.add(aDust);

		fireTableStructureChanged();
	}

	public int getColumnCount() {
		return dustSelectedList.size() + 1;
	}

	public String getColumnName(int c) {
		if (c == 0)
			return "Data";
		else
			return "" + (c - 1);
	}

	public int getRowCount() {
		return var_names.size();
	}

	public Object getValueAt(int r, int c) {
		// log.info(Integer.toString(r) + "," + Integer.toString(c));
		final String strNA = "N/A";
		if (c == 0) {
			return var_names.get(r);
		} else {
			if (dustSelectedList.size() < 1)
				return "";

			Dust dust = (Dust) dustSelectedList.get(c - 1);
			ArrayList values = dust.getValueList();

			String strType = (String) var_types.get(r);
			String strMeasure = (String) var_measures.get(r);

			if (values.get(r) == null)
				return strNA;
			else {
				if (strType.equals("S") && strMeasure.equals("O")) {
					DataLabel label = (DataLabel) var_labels.get(r);

					assert (values.get(r) instanceof Integer);
					int n = ((Integer) values.get(r)).intValue();
					return label.get(n);

				} else
					return values.get(r);
			}
		}
	}

	public void removeChosenDust(Dust aDust) {
		dustChosenList.remove(aDust);
		fireTableStructureChanged();
	}

	public void removeSelectedDust(Dust aDust) {
		dustSelectedList.remove(aDust);
		fireTableStructureChanged();
	}

	public void resetChosenDustList() {
		dustChosenList.clear();
		fireTableStructureChanged();
	}

	public void resetSelectedDustList() {
		dustSelectedList.clear();
		fireTableStructureChanged();
	}
}

/**
 * @author jyi Mar 19, 2004 9:29:23 PM
 */
public class DetailWindow extends JFrame {
	static Logger log = Logger.getLogger("edu.gatech.isye.lhci.DetailWindow");

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JScrollPane jsp;
	private JTable jt;
	private DetailTableModel tm;

	public DetailWindow() {
		super("Detail");
	}

	public DetailWindow(ArrayList names, ArrayList types, ArrayList measures,
			ArrayList labels) {
		this();

		tm = new DetailTableModel(names, types, measures, labels);
		jt = new JTable(tm);
		jt.setColumnSelectionAllowed(true);
		jt.setRowSelectionAllowed(false);
		jsp = new JScrollPane(jt);
		getContentPane().add(jsp, BorderLayout.CENTER);
	}

	public void addChosenDust(Dust aDust) {
		log.info("");
		tm.addChosenDust(aDust);
	}

	/**
	 * Add aDust into dustList of Detail Window
	 * 
	 * @param aDust
	 */
	public void addSelectedDust(Dust aDust) {
		log.info("");
		tm.addSelectedDust(aDust);
	}

	public void removeChosenDust(Dust aDust) {
		tm.removeChosenDust(aDust);
	}

	public void removeSelectedDust(Dust aDust) {
		tm.removeSelectedDust(aDust);
	}

	public void resetChosenDustList() {
		tm.resetChosenDustList();
	}

	public void resetSelectedDustList() {
		tm.resetSelectedDustList();
	}
}