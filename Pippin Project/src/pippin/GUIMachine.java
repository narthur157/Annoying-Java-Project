package pippin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import pippin.instructions.ADD;
import pippin.instructions.AND;
import pippin.instructions.CMPL;
import pippin.instructions.CMPZ;
import pippin.instructions.DIV;
import pippin.instructions.HALT;
import pippin.instructions.JMPZ;
import pippin.instructions.JUMP;
import pippin.instructions.LOD;
import pippin.instructions.MUL;
import pippin.instructions.NOP;
import pippin.instructions.NOT;
import pippin.instructions.STO;
import pippin.instructions.SUB;

/**
 * CLASS GUIMachine
 * 
 * @author Nick Arthur
 * @author Sri Edara
 * @author Tori Hallett
 * @author Alex Strong
 */

public class GUIMachine extends Observable {
	public final Instruction[] INSTRUCTION_SET = new Instruction[16];
	private States state;
	private JFrame frame;
	private Processor proc = new Processor();
	private Memory memory = new Memory(this);
	private boolean running = false;
	private boolean autoStepOn = false;
	private File currentlyExecutingFile = null;
	private static final int TICK = 250; // 1/4 second
	private String sourceDir;
	private String executableDir;
	private String eclipseDir;
	private Properties properties = null;

	private CodeViewPanel codeViewPanel;

	private DataViewPanel dataViewPanel;
	private ControlPanel controlPanel;
	private ProcessorViewPanel processorPanel;
	private MenuBarBuilder menuBuilder;

	public GUIMachine() {
		INSTRUCTION_SET[0] = new NOP(proc, memory);
		INSTRUCTION_SET[1] = new LOD(proc, memory);
		INSTRUCTION_SET[2] = new STO(proc, memory);
		INSTRUCTION_SET[3] = new ADD(proc, memory);
		INSTRUCTION_SET[4] = new SUB(proc, memory);
		INSTRUCTION_SET[5] = new MUL(proc, memory);
		INSTRUCTION_SET[6] = new DIV(proc, memory);
		INSTRUCTION_SET[7] = new AND(proc, memory);
		INSTRUCTION_SET[8] = new NOT(proc, memory);
		INSTRUCTION_SET[9] = new CMPZ(proc, memory);
		INSTRUCTION_SET[10] = new CMPL(proc, memory);
		INSTRUCTION_SET[11] = new JUMP(proc, memory);
		INSTRUCTION_SET[12] = new JMPZ(proc, memory);
		INSTRUCTION_SET[15] = new HALT(proc, memory);
		((HALT) INSTRUCTION_SET[15]).setGmachine(this);
		// copy the others over from Machine
		((HALT) INSTRUCTION_SET[15]).setGmachine(this);
		// a lot more code to come here...
		setUpDirectories();
		createAndShowGUI();
		state = States.NOTHING_LOADED;
		state.enter();
		setChanged();
		notifyObservers();
		javax.swing.Timer timer = new javax.swing.Timer(TICK,
				new TimerListener());
		timer.start();
	}

	private void setUpDirectories() {
		File temp = new File("propertyfile.txt");
		if (!temp.exists()) {
			PrintWriter out;
			try {
				out = new PrintWriter(temp);
				out.close();
				eclipseDir = temp.getAbsolutePath();
				temp.delete();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			eclipseDir = temp.getAbsolutePath();
		}
		// change to forward slashes
		eclipseDir = eclipseDir.replace('\\', '/');
		int lastSlash = eclipseDir.lastIndexOf('/');
		eclipseDir = eclipseDir.substring(0, lastSlash + 1);
		System.out.println(eclipseDir);
		try { // load properties file "propertyfile.txt", if it exists
			properties = new Properties();
			properties.load(new FileInputStream("propertyfile.txt"));
			sourceDir = properties.getProperty("SourceDirectory");
			executableDir = properties.getProperty("ExecutableDirectory");
			// CLEAN UP ANY ERRORS IN WHAT IS STORED:
			if (sourceDir == null || sourceDir.length() == 0
					|| !new File(sourceDir).exists()) {
				sourceDir = eclipseDir;
			}
			if (executableDir == null || executableDir.length() == 0
					|| !new File(executableDir).exists()) {
				executableDir = eclipseDir;
			}
		} catch (Exception e) {
			// PROPERTIES FILE DID NOT EXIST SO USE DEFAULT DIRECTORIES
			sourceDir = eclipseDir;
			executableDir = eclipseDir;
		}
	}

	public void halt() {
		running = false;
	}

	private void createAndShowGUI() {
		frame = new JFrame("Pippin Simulator");
		Container content = frame.getContentPane();
		content.setLayout(new BorderLayout(1, 1));
		content.setBackground(Color.BLACK);
		frame.setSize(800, 600);

		menuBuilder = new MenuBarBuilder(this);
		codeViewPanel = new CodeViewPanel(this);
		processorPanel = new ProcessorViewPanel(this);
		dataViewPanel = new DataViewPanel(this);
		controlPanel = new ControlPanel(this);
		JMenuBar bar = new JMenuBar();
		frame.setJMenuBar(bar);
		frame.add(dataViewPanel.createDataDisplay(), BorderLayout.LINE_END);
		frame.add(codeViewPanel.createCodeDisplay(), BorderLayout.CENTER);
		frame.add(controlPanel.createControlDisplay(), BorderLayout.PAGE_END);
		bar.add(menuBuilder.createMenu1());
		bar.add(menuBuilder.createMenu2());

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new ExitAdapter());
		state = States.NOTHING_LOADED;
		state.enter();
		setChanged();
		notifyObservers();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	public void step() {
		int pc = 0;
		int arg = 0;
		try {
			pc = proc.getProgramCounter();
			int opcode = memory.getOpcode(pc);
			Instruction in = INSTRUCTION_SET[opcode / 4];
			arg = memory.getArg(pc);
			boolean immediate = opcode % 2 == 1;
			boolean indirect = (opcode / 2) % 2 == 1;
			in.execute(arg, immediate, indirect);
		} catch (CodeAccessException e) {
			JOptionPane.showMessageDialog(frame,
					"Code Access Exception for program counter " + pc,
					"Warning", JOptionPane.WARNING_MESSAGE);

		} catch (DataAccessException e) {
			JOptionPane.showMessageDialog(frame,
					"Data Access Exception for argument " + arg, "Warning",
					JOptionPane.WARNING_MESSAGE);

		}
		setChanged();
		notifyObservers();
	}

	public void clearAll() {
		state = States.NOTHING_LOADED;
		state.enter();
		setAutoStepOn(false);
		memory.clearMemory();
		proc.setAccumulator(0);
		proc.setProgramCounter(0);
		setChanged();
		notifyObservers();
	}

	public void reload() {
		clearAll();
		finalLoadOrReloadStep();
	}

	public Memory getMemory() {
		return memory;
	}

	public Processor getProcessor() {
		return proc;
	}

	public int getProgramCounter() {
		return proc.getProgramCounter();
	}

	public int getAccumulator() {
		return proc.getAccumulator();
	}

	public boolean isAutoStepOn() {
		return autoStepOn;
	}

	public void setAutoStepOn(boolean b) {
		autoStepOn = b;
	}

	public States getState() {
		return state;
	}

	public void setState(States state) {
		this.state = state;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean b) {
		running = b;
		if (running) {
			state = States.PROGRAM_LOADED_NOT_AUTOSTEPPING;
			state.enter();
			setChanged();
			notifyObservers("New Program");
		} else {
			autoStepOn = false;
			state = States.PROGRAM_HALTED;
			state.enter();
			setChanged();
			notifyObservers();
		}
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new GUIMachine();
			}
		});
	}

	public void exit() { // method executed when user exits the program
		int decision = JOptionPane.showConfirmDialog(frame,
				"Do you really wish to exit?", "Confirmation",
				JOptionPane.YES_NO_OPTION);
		if (decision == JOptionPane.YES_OPTION) {
			System.exit(0);
		}
	}

	public void assembleFile() {
		File source = null;
		File outputExe = null;
		JFileChooser chooser = new JFileChooser(sourceDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Source Files", "pasm");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);
		if (openOK == JFileChooser.APPROVE_OPTION) {
			source = chooser.getSelectedFile();
		}
		if (source != null && source.exists()) {
			// CODE TO REMEMBER WHICH DIRECTORY HAS THE pasm FILES
			// WHICH WE WILL ALLOW TO BE DIFFERENT
			sourceDir = source.getAbsolutePath();
			sourceDir = sourceDir.replace('\\', '/');
			int lastDot = sourceDir.lastIndexOf('.');
			String outName = sourceDir.substring(0, lastDot + 1) + "pexe";
			int lastSlash = sourceDir.lastIndexOf('/');
			sourceDir = sourceDir.substring(0, lastSlash + 1);
			outName = outName.substring(lastSlash + 1);
			filter = new FileNameExtensionFilter("Pippin Executable Files",
					"pexe");
			if (executableDir.equals(eclipseDir)) {
				chooser = new JFileChooser(sourceDir);
			} else {
				chooser = new JFileChooser(executableDir);
			}
			chooser.setFileFilter(filter);
			chooser.setSelectedFile(new File(outName));
			int saveOK = chooser.showSaveDialog(null);
			if (saveOK == JFileChooser.APPROVE_OPTION) {
				outputExe = chooser.getSelectedFile();
			}
			if (outputExe != null) {
				executableDir = outputExe.getAbsolutePath();
				executableDir = executableDir.replace('\\', '/');
				lastSlash = executableDir.lastIndexOf('/');
				executableDir = executableDir.substring(0, lastSlash + 1);
				try {
					properties.setProperty("SourceDirectory", sourceDir);
					properties
							.setProperty("ExecutableDirectory", executableDir);
					properties.store(new FileOutputStream("propertyfile.txt"),
							"File locations");
				} catch (Exception e) {
					System.out.println("Error writing properties file");
				}
				try {
					boolean assembled = Assembler.assemble(source, outputExe);
					if (assembled) {
						JOptionPane.showMessageDialog(frame,
								"The source was assembled to an executable",
								"Success", JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(frame,
								"The selected file has problems.\n"
										+ "Cannot assemble the program", "Warning",
								JOptionPane.WARNING_MESSAGE);
					}
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(frame,
							"The selected file has problems.\n"
							+ "Cannot assemble the program", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(frame,
						"The selected output file was not created", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(frame,
					"The input file was not selected or found", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public void loadFile() {
		JFileChooser chooser = new JFileChooser(executableDir);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Pippin Executable Files", "pexe");
		chooser.setFileFilter(filter);
		// CODE TO LOAD DESIRED FILE
		int openOK = chooser.showOpenDialog(null);
		if (openOK == JFileChooser.APPROVE_OPTION) {
			currentlyExecutingFile = chooser.getSelectedFile();
		}
		if (currentlyExecutingFile != null && currentlyExecutingFile.exists()) {
			// CODE TO REMEMBER WHICH DIRECTORY HAS THE pexe FILES
			executableDir = currentlyExecutingFile.getAbsolutePath();
			executableDir = executableDir.replace('\\', '/');
			int lastSlash = executableDir.lastIndexOf('/');
			executableDir = executableDir.substring(0, lastSlash + 1);
			try {
				properties.setProperty("SourceDirectory", sourceDir);
				properties.setProperty("ExecutableDirectory", executableDir);
				properties.store(new FileOutputStream("propertyfile.txt"),
						"File locations");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(frame,
						"The properties file cannot be written", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
			finalLoadOrReloadStep();
		} else {
			JOptionPane.showMessageDialog(frame, "The file cannot be written",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void finalLoadOrReloadStep() {
		try {
			clearAll();
			try {
				Loader.load(memory, currentlyExecutingFile);
			}
			catch(CodeAccessException e) {
	        	halt();
	        	running=false;
	            JOptionPane.showMessageDialog(
	                    frame,
	                    "Code Access Exception",
	                    "Warning",
	                    JOptionPane.WARNING_MESSAGE);
			}
			catch (DataAccessException e) {
				halt();
				running = false;
				JOptionPane.showMessageDialog(frame,
						"Data Access Exception ", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
			setRunning(true);
			setAutoStepOn(false);
			proc.setAccumulator(0);
			proc.setProgramCounter(0);
			state = States.PROGRAM_LOADED_NOT_AUTOSTEPPING;
			state.enter();
			setChanged();
			notifyObservers();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame,
					"The file being selected has problems.\n"
							+ "Cannot load the program", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	public void execute() {
		int pc = 0;
		int arg = 0;
		try {
			while (running) {
				pc = proc.getProgramCounter();
				int opcode = memory.getOpcode(pc);
				Instruction in = INSTRUCTION_SET[opcode / 4];
				arg = memory.getArg(pc);
				boolean immediate = opcode % 2 == 1;
				boolean indirect = (opcode / 2) % 2 == 1;
				in.execute(arg, immediate, indirect);
			}
		} catch (CodeAccessException e) {
			halt();
			running = false;
			JOptionPane.showMessageDialog(frame,
					"Code Access Exception for program counter " + pc,
					"Warning", JOptionPane.WARNING_MESSAGE);

		} catch (DivideByZeroException e) {
			halt();
			running = false;
			JOptionPane.showMessageDialog(frame,
					"Divide By Zero Exception for program counter " + pc,
					"Warning", JOptionPane.WARNING_MESSAGE);
		} catch (DataAccessException e) {
			halt();
			running = false;
			JOptionPane.showMessageDialog(frame,
					"Data Access Exception for argument " + arg, "Warning",
					JOptionPane.WARNING_MESSAGE);

		}
		setChanged();
		notifyObservers();
	}

	private class ExitAdapter extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent arg0) {
			exit();
		}
	}

	private class TimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (autoStepOn)
				step();
		}
	}
}