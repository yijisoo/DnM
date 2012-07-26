/**
 * Dust & Magnet
 * 
 * Center for Interactive Systems Engineering (CISE)
 * 
 * http://cise.bme.gatech.edu
 * 
 * $Id: MagnetField.java 819 2012-07-26 18:42:36Z jyi $
 *  
 */

package edu.gatech.bme.cise;

import java.awt.Color;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.PFrame;

public class MagnetField extends PFrame implements MouseWheelListener,
		WindowFocusListener, java.io.Serializable {
	private static Logger log = Logger
			.getLogger("edu.gatech.isye.hcil.MagnetField");

	private static final long serialVersionUID = 1L;

	private ControlWindow controlWindow;

	private DetailWindow detailWindow;

	// Dust and Magnet

	private Dust[] dustList;

	private File fileImport;

	// Data

	private ArrayList magnetList;

	private JMenuBar mnuBar;

	private JMenu mnuDust;

	private JMenu mnuFile;

	private JMenu mnuHelp;

	private JMenuItem mnuitemAbout;

	private JMenuItem mnuitemAddSnapshot;

	private JMenuItem mnuitemAttractDust;

	// Auxiliary Windows

	private JMenuItem mnuitemCenterDust;

	private JMenuItem mnuitemEndExperiment;

	// Menu

	private JMenuItem mnuitemExit;

	private JMenuItem mnuitemImport;

	private JMenuItem mnuitemSpreadDust;

	private JMenuItem mnuitemStartExperiment;

	private JMenu mnuMagnet;

	private JMenu mnuSnapshot;

	final String strApplicationName = "Dust & Magnet";

	private String strSubjectID;

	final String strVersion = "0.801 - DnM_ext experiment";

	private ArrayList values;

	private ArrayList values_max;

	private ArrayList values_min;

	private ArrayList var_descriptions;

	private ArrayList var_labels;

	private ArrayList var_measures;

	// File

	private ArrayList var_names;

	private ArrayList var_types;

	public MagnetField(boolean bFullScreen, PCanvas aCanvas) {
		//super(bFullScreen, aCanvas);
		this.setVisible(false);

		this.addWindowFocusListener(this);
		getCanvas().addMouseWheelListener(this);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("magnet.jpg"));

		// The following smooth pannig feature comes from
		// edu.umd.cs.piccolo.examples.PanToExample
		// getCanvas().getCamera().addInputEventListener(
		// new PBasicInputEventHandler() {
		// public void mousePressed(PInputEvent event) {
		// if (event.getPickedNode() instanceof PCamera) {
		// } else {
		// event.setHandled(true);
		// getCanvas()
		// .getCamera()
		// .animateViewToPanToBounds(
		// event.getPickedNode()
		// .getGlobalFullBounds(), 500);
		// }
		// }
		// });
	}

	protected Magnet addMagnet(final int index) {
		Magnet aMagnet = new Magnet((String) var_names.get(index),
				(String) var_types.get(index),
				(String) var_measures.get(index), (String) var_descriptions
						.get(index), index);
		aMagnet.setPaint(Color.black);
		aMagnet.setOffset(100, 100);
		getCanvas().getLayer().addChild(aMagnet);

		magnetList.add(aMagnet);

		aMagnet.addInputEventListener(new PBasicInputEventHandler() {
			public void mouseDragged(PInputEvent aEvent) {
				Dimension2D delta = aEvent.getDeltaRelativeTo(aEvent
						.getPickedNode());
				Magnet magnet = (Magnet) aEvent.getPickedNode();
				magnet.hideToolTip();
				magnet.translate(delta.getWidth(), delta.getHeight());
				for (int i = 0; i < dustList.length; i++) {
					// Filtered Dusts are not affected by movements
					if (dustList[i].getFiltered())
						continue;

					for (int j = 0; j < magnetList.size(); j++) {
						Magnet m = (Magnet) magnetList.get(j);
						dustList[i].moveToward(m);
					}
				}

				aEvent.setHandled(true);
			}

			public void mouseEntered(PInputEvent aEvent) {
				Magnet magnet = (Magnet) aEvent.getPickedNode();
				magnet.showToolTip();
			}

			public void mouseExited(PInputEvent aEvent) {
				Magnet magnet = (Magnet) aEvent.getPickedNode();
				magnet.hideToolTip();
			}

			public void mousePressed(PInputEvent aEvent) {
				Magnet magnet = (Magnet) aEvent.getPickedNode();
				magnet.hideToolTip();
				controlWindow.selectMagnet(magnet.getMagnetName());
			}
		});

		controlWindow.refresh();

		return aMagnet;
	}

	/**
	 * 
	 */
	private void addMagnetMenu(final int index) {
		JCheckBoxMenuItem mnuitemMagnet;
		mnuitemMagnet = new JCheckBoxMenuItem(((String) var_names.get(index)));
		mnuitemMagnet.setToolTipText((String) var_descriptions.get(index));
		mnuitemMagnet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem menu = (JCheckBoxMenuItem) e.getSource();
				if (menu.getState() == true) {
					log.severe("Magnet/add Magnet [" + menu.getText() + "]");
					addMagnet(index);
				} else {
					log.severe("Magnet/remove Magnet [" + menu.getText() + "]");
					removeMagnet(index);
				}
			}
		});
		mnuMagnet.add(mnuitemMagnet);
	}

	protected void addSnapshot() {
		boolean isDuplicate;
		String strSnapshot;

		do {
			strSnapshot = JOptionPane.showInputDialog(this,
					"Please enter a name for this snapshot:",
					"Dust & Magnet - Snapshot", JOptionPane.QUESTION_MESSAGE);

			if (strSnapshot == null)
				return;

			isDuplicate = false;

			for (int i = 0; i < mnuSnapshot.getItemCount(); i++) {
				JMenuItem m = mnuSnapshot.getItem(i);
				if (m == null)
					continue;

				if (strSnapshot.equalsIgnoreCase(m.getText()) == true) {
					isDuplicate = true;
					break;
				}
			}

			if (isDuplicate == true) {
				JOptionPane
						.showMessageDialog(
								this,
								"Snapshot (\""
										+ strSnapshot
										+ "\") already exist. Please try another name for a snapshot.",
								"Dust & Magnet - Snapshot",
								JOptionPane.WARNING_MESSAGE);
			}
		} while (isDuplicate == true);

		addSnapshot(strSnapshot);
	}

	protected void addSnapshot(String strSnapshot) {

		// Check out duplication of snapshot names

		boolean isDuplicate = false;

		for (int i = 0; i < mnuSnapshot.getItemCount(); i++) {
			JMenuItem m = mnuSnapshot.getItem(i);
			if (m == null)
				continue;

			if (strSnapshot.equalsIgnoreCase(m.getText()) == true) {
				isDuplicate = true;
				break;
			}
		}

		if (isDuplicate == true)
			return;

		// 

		String strSnapshotFilename = fileImport.getName() + "-" + strSubjectID
				+ "-" + strSnapshot + ".ss";

		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					strSnapshotFilename));

			// Camera information

			Point2D point = getCanvas().getCamera().getViewBounds()
					.getCenter2D();
			double scale = getCanvas().getCamera().getViewScale();

			out.writeDouble(point.getX());
			out.writeDouble(point.getY());
			out.writeDouble(scale);

			// Dust

			out.writeInt(dustList.length);

			for (int i = 0; i < dustList.length; i++) {
				out.writeDouble(dustList[i].getGlobalBounds().x);
				out.writeDouble(dustList[i].getGlobalBounds().y);
			}

			// Magnet

			out.writeInt(magnetList.size());

			for (int i = 0; i < magnetList.size(); i++) {
				Magnet m = (Magnet) magnetList.get(i);
				out.writeInt(m.getVarIndex());
				out.writeDouble(m.getGlobalBounds().x);
				out.writeDouble(m.getGlobalBounds().y);
				out.writeInt(m.getMagnitude());
				out.writeDouble(m.getThreshold());
				// m.getRepelList(); TODO
			}

			controlWindow.saveSnapshot(out);

			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Add Menu for this

		JMenuItem mnuitemASnapshot = new JMenuItem(strSnapshot);
		mnuitemASnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JMenuItem m = (JMenuItem) e.getSource();
				log.severe("Snapshot/Recover Snapshot [" + m.getText() + "]");
				loadSnapshot(m.getText());
			}
		});

		mnuSnapshot.add(mnuitemASnapshot);
	}

	private void attractDust() {
		for (int i = 0; i < dustList.length; i++) {

			// Filtered Dusts are not affected by attraction
			if (dustList[i].getFiltered())
				continue;

			for (int j = 0; j < magnetList.size(); j++) {
				Magnet magnet = (Magnet) magnetList.get(j);
				dustList[i].moveToward(magnet, 10.0);
			}
		}
	}

	private void centerDust() {
		int width = getCanvas().getWidth();
		int height = getCanvas().getHeight();

		for (int i = 0; i < dustList.length; i++) {
			dustList[i].setOffset(width / 2, height / 2);
		}
	}

	protected void experimentEnd() {
		int nVisibleDust = 0;
		
		for (int i = 0; i < dustList.length; i++) {
			if (!dustList[i].getFiltered())
				nVisibleDust += 1; 
		}
		
		String strMagnetList = "";		
		for (int i = 0; i < magnetList.size() ; i++) {
			Magnet m = (Magnet) magnetList.get(i);
			strMagnetList += (String) var_names.get(m.getVarIndex()) + ",";
		}
				
		log.severe("File/End Experiment: Dust (" + nVisibleDust + "), Magnets [" + strMagnetList + "]");
		
		addSnapshot("__endofexperiment__");
		
		mnuitemStartExperiment.setEnabled(true);
		mnuitemEndExperiment.setEnabled(false);
	}

	protected void experimentStart() {
		boolean isDuplicate = false;

		do {
			strSubjectID = JOptionPane.showInputDialog(this,
					"Please enter your participant ID:",
					"Dust & Magnet - Start Experiment",
					JOptionPane.QUESTION_MESSAGE);

			if (strSubjectID == null)
				return;

			isDuplicate = (new File(fileImport.getName() + "-" + strSubjectID
					+ ".log")).exists();

			if (isDuplicate == true) {
				int answer = JOptionPane
						.showConfirmDialog(
								this,
								"Participant ID (\""
										+ fileImport.getName()
										+ "-"
										+ strSubjectID
										+ "\") already exists. Are you still sure to use this participant ID?",
								"Dust & Magnet - Start Experiment",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (answer == 0)
					isDuplicate = false;
				else
					return;
			}
		} while (isDuplicate == true);

		/**
		 * Logger setting
		 */
		FileHandler fh;

		try {
			fh = new FileHandler(fileImport.getName() + "-" + strSubjectID
					+ ".log");
			fh.setFormatter(new UserbehaviorFormatter());

			Logger.getLogger("").setLevel(Level.SEVERE);
			Logger.getLogger("").addHandler(fh);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/**
		 * Start the experiment
		 */

		log.severe("File/Start Experiment - Data set[" + fileImport.getName()
				+ "], Subject ID[" + strSubjectID + "]");

		/**
		 * Disable Start Experiment menu, and enable End Experiment menu
		 */

		mnuitemStartExperiment.setEnabled(false);
		mnuitemEndExperiment.setEnabled(true);
	}

	protected void importFile(File file) {
		if (!file.exists() || !file.canRead()) {
			log.warning("Can't read " + file);
			return;
		}

		if (file.isDirectory()) {
			log.warning(file + " is a directory.");
			return;
		}

		initDustnMagnet();

		fileImport = file;
		setTitle(strApplicationName + " - " + fileImport.getName());

		int numLine = 0;
		int numColumn = 0;

		try {
			FileReader fr = new FileReader(file);
			BufferedReader in = new BufferedReader(fr);
			var_names = new ArrayList();
			var_types = new ArrayList();
			var_measures = new ArrayList();
			var_labels = new ArrayList();
			var_descriptions = new ArrayList();
			values = new ArrayList();

			String line;

			while ((line = in.readLine()) != null) {
				numLine++;
				StringTokenizer st = new StringTokenizer(line, "\t\n\r\f");
				if (numLine == 1) { // header
					numColumn = st.countTokens();
					while (st.hasMoreTokens()) {
						var_names.add(st.nextToken().replaceAll("\"", ""));
					}
					log.info("var_names.size() == " + var_names.size());
				} else if (numLine == 2) {
					// type : 'S'tring, 'D'ouble, 'I'nteger
					while (st.hasMoreTokens()) {
						String strType = st.nextToken().replaceAll("\"", "");
						if (strType.equalsIgnoreCase("S") // String
								|| strType.equalsIgnoreCase("D") // Double
								|| strType.equalsIgnoreCase("I")) // Integer
							var_types.add(strType.toUpperCase());
						else
							log.warning("unknown var_types");
					}
					log.info("var_types.size() == " + var_types.size());
					assert var_names.size() == var_types.size();
				} else if (numLine == 3) {
					// measure: 'N'ominal, 'O'rdinal, 'Q'uantitative
					while (st.hasMoreTokens()) {
						String strMeasure = st.nextToken().replaceAll("\"", "");
						if (strMeasure.equalsIgnoreCase("N") // Nominal
								|| strMeasure.equalsIgnoreCase("O") // Ordinal
								|| strMeasure.equalsIgnoreCase("Q")) // Quantitative
							var_measures.add(strMeasure.toUpperCase());
						else
							assert false;
					}
					log.info("var_measures.size() == " + var_measures.size());
					assert var_names.size() == var_measures.size();

				} else if (numLine == 4) { // data labels
					/**
					 * Check out the types and measures are correct.
					 */
					for (int i = 0; i < var_types.size() /* numColumns */; i++) {
						String strType = (String) var_types.get(i);
						String strMeasure = (String) var_measures.get(i);

						assert !(strType.equals("D") && strMeasure.equals("N"));
						assert !(strType.equals("I") && strMeasure.equals("N"));
						assert !(strType.equals("D") && strMeasure.equals("O"));
						assert !(strType.equals("I") && strMeasure.equals("O"));
						assert !(strType.equals("S") && strMeasure.equals("Q"));
					}

					while (st.hasMoreTokens()) {
						String strLabels = st.nextToken();
						var_labels.add(new DataLabel(strLabels));
					}
					log.info("var_labels.size() == " + var_labels.size());
					assert var_names.size() == var_labels.size();

				} else if (numLine == 5) { // description of each attribute
					while (st.hasMoreTokens()) {
						String strDesc = st.nextToken().replaceAll("\"", "");
						var_descriptions.add(strDesc);
					}
					log.info("var_descriptions.size() = "
							+ var_descriptions.size());
					assert var_names.size() == var_descriptions.size();
				} else if (numLine > 5) { // data
					ArrayList aRow = new ArrayList();
					for (int i = 0; i < numColumn; i++) {
						String strMeasure = (String) var_measures.get(i);
						String strType = (String) var_types.get(i);
						String token = st.nextToken().replaceAll("\"", "")
								.trim();

						if (strType.equals("S") && strMeasure.equals("N"))
							aRow.add(token);
						else if (strType.equals("I") && strMeasure.equals("N"))
							aRow.add(token);
						else if (strType.equals("S") && strMeasure.equals("O")) {
							if (token.equals("NA"))
								aRow.add(null);
							else
								aRow.add(Integer.valueOf(token));
						} else if (strType.equals("D")
								&& strMeasure.equals("Q")) {
							if (token.equals("NA"))
								aRow.add(null);
							else
								aRow.add(Double.valueOf(token));
						} else if (strType.equals("I")
								&& strMeasure.equals("Q")) {
							if (token.equals("NA"))
								aRow.add(null);
							else
								aRow.add(Integer.valueOf(token));
						} else
							assert false;
					}
					values.add(aRow);
				}
			}
		} catch (FileNotFoundException e) {
			log.warning(file + " is not found.");
		} catch (IOException e) {
			log.warning("IO Exception");
			e.printStackTrace();
		}

		log.info("importFile is completely read.");

		initDust();

		initMinMaxValues(numColumn);

		for (int i = 0; i < numColumn; i++) {
			String strMeasure = (String) var_measures.get(i);
			if (strMeasure.equals("O") || strMeasure.equals("Q")) {
				addMagnetMenu(i);
			}
		}

		initAuxiliaryWindows();

		mnuitemStartExperiment.setEnabled(true);

		// comment out experiment mode.
		// experimentStart();

		addSnapshot("__start__");
		
		// TODO This part is only for DnM extension experiment. After the experiment, PLEASE fix this part.
		if (fileImport.getName().equals("nursing home_q2.txt") || fileImport.getName().equals("nursing home_q3.txt"))
			Magnet.isFlippedAttraction = true;
		else
			Magnet.isFlippedAttraction = false;

		return;
	}

	private void initAuxiliaryWindows() {
		log.info("");
		initControlWindow();
		initDetailWindow();
	}

	// private void initControlWindow() {
	// if (controlWindow != null) {
	// controlWindow.dispose();
	// controlWindow = null;
	// }
	//
	// showControlWindow();
	// }

	// private void initDetailWindow() {
	// if (detailWindow != null) {
	// detailWindow.dispose();
	// detailWindow = null;
	// }
	//
	// showDetailWindow();
	// }

	private void initControlWindow() {
		assert (var_names != null);
		assert (var_types != null);
		assert (var_measures != null);
		assert (values_max != null);
		assert (values_min != null);

		if (controlWindow != null) {
			controlWindow.dispose();
			controlWindow = null;
		}

		controlWindow = new ControlWindow(var_names, var_types, var_measures,
				var_labels, values_max, values_min, dustList, magnetList);
		controlWindow.setSize(300, 350);
		controlWindow.setLocation(600, 0);
		controlWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"magnet.jpg"));

		controlWindow.setVisible(true);
	}

	private void initDetailWindow() {
		assert (var_names != null);
		assert (var_types != null);
		assert (var_measures != null);
		assert (values_max != null);
		assert (values_min != null);

		if (detailWindow != null) {
			detailWindow.dispose();
			detailWindow = null;
		}

		detailWindow = new DetailWindow(var_names, var_types, var_measures,
				var_labels);
		detailWindow.setSize(300, 250);
		detailWindow.setLocation(600, 350);
		detailWindow.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"magnet.jpg"));

		detailWindow.setVisible(true);
	}

	/**
	 * 
	 */
	private void initDust() {
		log.info("");

		assert (values != null);
		assert (fileImport != null);

		boolean bImageExist = false;

		getCanvas().getLayer().removeAllChildren();

		String strParent = fileImport.getParent();
		String strName = fileImport.getName();

		String strImageFolder = strParent + "\\"
				+ strName.substring(0, strName.lastIndexOf('.')) + "_images";
		File imageFolder = new File(strImageFolder);

		if (imageFolder.isDirectory())
			bImageExist = true;

		dustList = new Dust[values.size()];
		for (int i = 0; i < values.size(); i++) {
			dustList[i] = new Dust((ArrayList) values.get(i));

			if (bImageExist) {
				String files[] = imageFolder.list();
				for (int j = 0; j < files.length; j++) {
					String imageID = dustList[i].getImageID();
					if (files[j].indexOf(imageID) == 0) {
						dustList[i].addImage(Toolkit.getDefaultToolkit()
								.getImage(strImageFolder + "\\" + files[j]));
					}
				}
			}

			dustList[i].addInputEventListener(new PBasicInputEventHandler() {
				public void mouseClicked(PInputEvent aEvent) {
					int mods = aEvent.getModifiers();
					int clickcount = aEvent.getClickCount();

					Dust aDust = (Dust) aEvent.getPickedNode();

					if (clickcount == 2) {
						aDust.shuffleToNextImage();
					} else if ((mods & InputEvent.CTRL_MASK) != 0) {
						if (aDust.getSelected()) {
							aDust.setSelected(false);
							detailWindow.removeSelectedDust(aDust);
						} else {
							aDust.setSelected(true);
							detailWindow.addSelectedDust(aDust);
						}
					} else if ((mods & InputEvent.SHIFT_MASK) != 0) {
						if (aDust.getChosen()) {
							aDust.setChosen(false);
							detailWindow.removeChosenDust(aDust);
						} else {
							aDust.setChosen(true);
							detailWindow.addChosenDust(aDust);
						}
					} else {
						detailWindow.resetSelectedDustList();
						Dust.setUnselectedAll();

						aDust.setSelected(true);
						detailWindow.addSelectedDust(aDust);
					}
					aEvent.setHandled(true);
				}

				public void mouseDragged(PInputEvent aEvent) {
					Dimension2D delta = aEvent.getDeltaRelativeTo(aEvent
							.getPickedNode());
					aEvent.getPickedNode().translate(delta.getWidth(),
							delta.getHeight());
					aEvent.setHandled(true);
				}
			});
			getCanvas().getLayer().addChild(dustList[i]);
		}

		log.info("End of adding Dusts");

		centerDust();

		getCanvas().addInputEventListener(new PDragEventHandler());
	}

	protected void initDustnMagnet() {
		removeAllDust();
		removeAllMagnetMenu();
		removeAllMagnet();
		removeAllSnapshotMenu();
		values = null;
		values_max = null;
		values_min = null;
		var_descriptions = null;
		var_labels = null;
		var_measures = null;
		var_names = null;
		var_types = null;

		Point2D originalPoint = getCanvas().getCamera().getGlobalBounds()
				.getCenter2D();
		double scaleFactor = 1.0 / getCanvas().getCamera().getViewScale();
		getCanvas().getCamera().scaleViewAboutPoint(scaleFactor,
				originalPoint.getX(), originalPoint.getY());

	}

	public void initialize() {
		// rndGenerator = new Random();
		setTitle(strApplicationName);
		initMenu();
		setVisible(true);
		magnetList = new ArrayList();
	}

	private void initMenu() {

		/**
		 * mnuFile
		 */
		mnuFile = new JMenu("File");
		mnuFile.setMnemonic(KeyEvent.VK_F);

		mnuitemImport = new JMenuItem("Import Data File");
		mnuitemImport.setMnemonic(KeyEvent.VK_I);
		mnuitemImport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
				Event.CTRL_MASK));
		mnuitemImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.severe("File/Import Data File");
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(false);
				int option = chooser.showOpenDialog(MagnetField.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					File sf = chooser.getSelectedFile();
					String strFile = sf.getName();
					log.info("import file name: " + strFile);
					importFile(sf);
				}
			}
		});

		mnuitemExit = new JMenuItem("Exit");
		mnuitemExit.setMnemonic(KeyEvent.VK_X);
		mnuitemExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int answer = JOptionPane.showConfirmDialog(null,
						"Are you sure to exit Dust & Magnet?",
						"Dust & Magnet - Exit", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (answer == 0) {
					log.severe("File/Exit");
					System.exit(0);
				}
			}
		});

		mnuitemStartExperiment = new JMenuItem("Start Experiment");
		mnuitemStartExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				experimentStart();
			}
		});

		mnuitemEndExperiment = new JMenuItem("End Experiment");
		mnuitemEndExperiment.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				experimentEnd();
			}
		});

		mnuitemStartExperiment.setEnabled(false);
		mnuitemEndExperiment.setEnabled(false);

		mnuFile.add(mnuitemImport);
		mnuFile.addSeparator();
		mnuFile.add(mnuitemStartExperiment);
		mnuFile.add(mnuitemEndExperiment);
		mnuFile.addSeparator();
		mnuFile.add(mnuitemExit);

		/**
		 * mnuDust
		 */

		mnuDust = new JMenu("Dust");
		mnuDust.setMnemonic(KeyEvent.VK_D);

		mnuitemSpreadDust = new JMenuItem("Spread Dust");
		mnuitemSpreadDust.setMnemonic(KeyEvent.VK_S);
		mnuitemSpreadDust.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
				Event.CTRL_MASK));
		mnuitemSpreadDust.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.severe("Dust/Spread Dust");
				spreadDust();
			}
		});

		mnuitemCenterDust = new JMenuItem("Center Dust");
		mnuitemCenterDust.setMnemonic(KeyEvent.VK_E);
		mnuitemCenterDust.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
				Event.CTRL_MASK));
		mnuitemCenterDust.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.severe("Dust/Center Dust");
				centerDust();
			}
		});

		mnuitemAttractDust = new JMenuItem("Attract Dust");
		mnuitemAttractDust.setMnemonic(KeyEvent.VK_A);
		mnuitemAttractDust.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
				Event.CTRL_MASK));
		mnuitemAttractDust.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.severe("Dust/Attract Dust");
				attractDust();
			}
		});

		mnuDust.add(mnuitemSpreadDust);
		mnuDust.add(mnuitemCenterDust);
		mnuDust.add(mnuitemAttractDust);

		/**
		 * mnuManget
		 */

		mnuMagnet = new JMenu("Magnet");
		mnuMagnet.setMnemonic(KeyEvent.VK_M);

		/**
		 * mnuSnapshot
		 */

		mnuSnapshot = new JMenu("Snapshot");
		mnuSnapshot.setMnemonic(KeyEvent.VK_S);

		mnuitemAddSnapshot = new JMenuItem("Add a Snapshot");
		mnuitemAddSnapshot.setMnemonic(KeyEvent.VK_S);
		mnuitemAddSnapshot.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
				Event.CTRL_MASK));
		mnuitemAddSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.severe("Snapshot/Add a Snapshot");
				addSnapshot();
			}
		});

		mnuSnapshot.add(mnuitemAddSnapshot);
		mnuSnapshot.addSeparator();
		

		/**
		 * mnuHelp
		 */

		mnuHelp = new JMenu("Help");
		mnuHelp.setMnemonic(KeyEvent.VK_H);

		mnuitemAbout = new JMenuItem("About");
		mnuitemAbout.setMnemonic(KeyEvent.VK_A);
		mnuitemAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.severe("Help/About");
				showAbout();
			}
		});

		mnuHelp.add(mnuitemAbout);

		/**
		 * munBar
		 */

		mnuBar = new JMenuBar();
		mnuBar.add(mnuFile);
		mnuBar.add(mnuDust);
		mnuBar.add(mnuMagnet);
		mnuBar.add(mnuSnapshot);
		mnuBar.add(mnuHelp);

		setJMenuBar(mnuBar);
	}

	private void initMinMaxValues(int numColumn) {
		log.info("");
		values_max = new ArrayList();
		values_min = new ArrayList();

		for (int i = 0; i < numColumn; i++) {
			String strType = (String) var_types.get(i);
			String strMeasure = (String) var_measures.get(i);

			if (strType.equals("D") && strMeasure.equals("Q")) {
				double max = 0, min = 0;
				for (int j = 0; j < values.size(); j++) {
					Object obj = (((ArrayList) (values.get(j))).get(i));
					if (obj != null) {
						if (j == 0)
							max = min = ((Double) obj).doubleValue();
						else {
							try {
								double v = ((Double) obj).doubleValue();
								max = v > max ? v : max;
								min = v < min ? v : min;
							} catch (ClassCastException e) {
								log
										.info("ClassCastException in MagnetField.initMinMaxValues()");
							}
						}
					}
				}
				values_max.add(new Double(max));
				values_min.add(new Double(min));
			} else if ((strType.equals("I") && strMeasure.equals("Q"))
					|| (strType.equals("S") && strMeasure.equals("O"))) {
				int max = 0, min = 0;
				for (int j = 0; j < values.size(); j++) {
					Object obj = (((ArrayList) (values.get(j))).get(i));
					if (obj != null) {
						if (j == 0) {
							max = min = ((Integer) obj).intValue();
						} else {
							int v = ((Integer) obj).intValue();
							max = v > max ? v : max;
							min = v < min ? v : min;
						}
					}
				}
				values_max.add(new Integer(max));
				values_min.add(new Integer(min));
			} else if (strType.equals("S") && strMeasure.equals("N")) {
				values_max.add(new Double(0));
				values_min.add(new Double(0));
			} else {
				// Unknown data type & measure
				values_max.add(new Double(0));
				values_min.add(new Double(0));

				assert false;
			}
		}

		Dust.setVarTypes(var_types);
		Dust.setVarMeasures(var_measures);
		Dust.updateDistMatrix(-1);

		Magnet.setValuesMaxList(values_max);
		Magnet.setValuesMinList(values_min);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		log.info("mouseWheelMoved");

		int notches = e.getWheelRotation();
		double scaleFactor;

		if (notches < 0) {
			scaleFactor = 0.9;
			log.severe("Zoom out");
		} else {
			scaleFactor = 1.1;
			log.severe("Zoom in");
		}

		Point2D scalePoint = getCanvas().getCamera().getViewBounds()
				.getCenter2D();
		if (scaleFactor != 1) {
			getCanvas().getCamera().scaleViewAboutPoint(scaleFactor,
					scalePoint.getX(), scalePoint.getY());
		}

		e.consume();
	}

	protected void loadSnapshot(String strSnapshot) {
		log.info("strSnapshot = " + strSnapshot);
		String strSnapshotFilename = fileImport.getName() + "-" + strSubjectID
				+ "-" + strSnapshot + ".ss";

		try {
			DataInputStream in = new DataInputStream(new FileInputStream(
					strSnapshotFilename));

			// Camera information
			
			in.readDouble(); in.readDouble(); in.readDouble();
		
//			double x = in.readDouble();
//			double y = in.readDouble();
//			double scale = in.readDouble();

			// TODO I need to fully understand how scale things working...
//			getCanvas().getCamera().setViewScale(scale);
//			
//			PBounds viewBounds = getCanvas().getCamera().getViewBounds();
//			getCanvas().getCamera().translate(viewBounds.getCenterX() - x, viewBounds.getCenterY() - y);
			
			// Dust

			int nDustLength = in.readInt();
			assert nDustLength == dustList.length;

			for (int i = 0; i < nDustLength; i++) {
				double dX = in.readDouble();
				double dY = in.readDouble();
				dustList[i].setOffset(dX, dY);
			}

			// Magnet

			int nMagnetLength = in.readInt();

			removeAllMagnet();

			for (int i = 0; i < mnuMagnet.getItemCount(); i++) {
				JMenuItem m = mnuMagnet.getItem(i);
				m.setSelected(false);
			}

			for (int i = 0; i < nMagnetLength; i++) {
				int nVarIndex = in.readInt();
				double dX = in.readDouble();
				double dY = in.readDouble();
				int nMagnitude = in.readInt();
				double dThreshold = in.readDouble();

				Magnet aMagnet = addMagnet(nVarIndex);
				aMagnet.setOffset(dX, dY);
				aMagnet.setMagnitude(nMagnitude);
				aMagnet.setThreshold(dThreshold);

				for (int j = 0; j < mnuMagnet.getItemCount(); j++) {
					JMenuItem m = mnuMagnet.getItem(j);
					if (m.getText().equals(aMagnet.getMagnetName())) {
						m.setSelected(true);
						break;
					}
				}

				// m.setRepelList(); TODO
			}

			controlWindow.refresh();

			// Control Window

			controlWindow.loadSnapshot(in);

		} catch (EOFException e) {
			e.printStackTrace();
			assert false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void removeAllDust() {
		if (dustList == null)
			return;

		for (int i = 0; i < dustList.length; i++) {
			Dust d = (Dust) dustList[i];
			getCanvas().getLayer().removeChild(d);
		}

		dustList = null;
	}

	protected void removeAllMagnet() {
		for (int i = 0; i < magnetList.size(); i++) {
			Magnet m = (Magnet) magnetList.get(i);
			getCanvas().getLayer().removeChild(m);
		}

		magnetList.clear();

		if (controlWindow != null)
			controlWindow.refresh();
	}

	private void removeAllMagnetMenu() {
		mnuMagnet.removeAll();
	}

	private void removeAllSnapshotMenu() {
		while (mnuSnapshot.getItemCount() > 2) {
			mnuSnapshot.remove(mnuSnapshot.getItemCount() - 1);
		}
	}

	/**
	 * 
	 */
	protected void removeMagnet(int index) {
		for (int i = 0; i < magnetList.size(); i++) {
			Magnet aMagnet = (Magnet) magnetList.get(i);
			if (aMagnet.getVarIndex() == index) {
				getCanvas().getLayer().removeChild(aMagnet);
				magnetList.remove(i);
				break;
			}
		}

		if (controlWindow != null)
			controlWindow.refresh();
	}

	/**
	 * 
	 */
	protected void showAbout() {
		JOptionPane.showMessageDialog(this, strApplicationName + " "
				+ strVersion + "\n\n" + "Ji Soo Yi <jyi@isye.gatech.edu>\n"
				+ "(c) 2004, 2005 " + "Laboratory for HCI\n"
				+ "Georgia Institute of Technology\n" + "\n"
				+ "http://www.isye.gatech.edu/lhci\n", "About "
				+ strApplicationName + " " + strVersion,
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void spreadDust() {
		if (dustList == null)
			return;

		for (int i = 0; i < dustList.length; i++) {
			Dust.updateDistMatrix(i);
			dustList[i].doAvoidNeighbors(null);
		}
	}

	public void windowGainedFocus(WindowEvent e) {
		log.finer("windowGainedFocus");
		if (e.getOppositeWindow() != controlWindow
				&& e.getOppositeWindow() != detailWindow) {
			if (controlWindow != null)
				controlWindow.toFront();

			if (detailWindow != null)
				detailWindow.toFront();
		}
		this.toFront();
	}

	public void windowLostFocus(WindowEvent e) {
		// just add to implement WindowFocusListener
	}

	/**
	 * 
	 */
	protected void zoomOriginal() {
		double s = getCanvas().getCamera().getViewScale();
		log.info("(from) scale = " + Double.toString(s));
		getCanvas().getCamera().setViewScale(1.0);
		s = getCanvas().getCamera().getViewScale();
		log.info("(to) scale = " + Double.toString(s));
	}
}