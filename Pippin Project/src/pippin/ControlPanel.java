package pippin;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class ControlPanel implements Observer {
	private GUIMachine machine;
	private JButton stepButton = new JButton("step");
	private JButton clearButton = new JButton("Clear");
	private JButton runButton = new JButton("Run/Pause");
	private JButton reloadButton = new JButton("Reload");
	
	public ControlPanel(GUIMachine machine) {
		this.machine = machine;
		machine.addObserver(this);
	}
	
	public void checkEnabledButtons() {
		runButton.setEnabled(machine.getState().getRunSuspendActive());
		stepButton.setEnabled(machine.getState().getStepActive());
		clearButton.setEnabled(machine.getState().getClearActive());
		reloadButton.setEnabled(machine.getState().getReloadActive());
	}
	
	public JComponent createControlDisplay() {
		JPanel retVal = new JPanel();
		retVal.setLayout(new GridLayout(1,0));
		runButton.setBackground(Color.WHITE);
		stepButton.setBackground(Color.WHITE);
		clearButton.setBackground(Color.WHITE);
		reloadButton.setBackground(Color.WHITE);
		reloadButton.addActionListener(new ReloadListener());
		stepButton.addActionListener(new StepListener());
		runButton.addActionListener(new RunPauseListener());
		clearButton.addActionListener(new ClearListener());
		retVal.add(runButton);
		retVal.add(stepButton);
		retVal.add(clearButton);
		retVal.add(reloadButton);
		return retVal;
	}
	@Override
	public void update(Observable arg0, Object arg1) {
		checkEnabledButtons();
	}
	private class ReloadListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			machine.reload();
		}
	}
	private class StepListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			machine.step();
		}
	}
	private class ClearListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			machine.clearAll();
		}
	}
	private class RunPauseListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (machine.isAutoStepOn()) {
				machine.setAutoStepOn(false);
			} else {
				machine.setAutoStepOn(true);
			}
		}
	}
}
