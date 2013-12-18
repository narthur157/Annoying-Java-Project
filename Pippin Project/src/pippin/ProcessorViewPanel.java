package pippin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ProcessorViewPanel implements Observer {
	Processor proc;
	JTextField accField = new JTextField();
	JTextField pcField = new JTextField();
	public ProcessorViewPanel(GUIMachine machine) {
		proc = machine.getProcessor();
		accField.setEditable(false);
		pcField.setEditable(false);
		machine.addObserver(this);
	}
	
	public JComponent createProcessorDisplay() {
		JPanel retVal = new JPanel();
		retVal.setLayout(new GridLayout(1,0));
		retVal.add(new JLabel("Accumulator: "), BorderLayout.LINE_END);
		retVal.add(accField);
		retVal.add(new JLabel("Program Counter: "), BorderLayout.LINE_END);
		retVal.add(pcField);
		return retVal;
	}
	@Override
	public void update(Observable o, Object arg) {
		accField.setText(""+proc.getAccumulator());
		pcField.setText(""+proc.getProgramCounter());
	}
	
}
