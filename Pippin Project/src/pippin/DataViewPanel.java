package pippin;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class DataViewPanel implements Observer {
	private Memory memory;
	private JScrollPane scroller;

	public DataViewPanel(GUIMachine machine) {
		memory = machine.getMemory();
		machine.addObserver(this);
	}

	public JComponent createDataDisplay() {
		JPanel returnPanel = new JPanel();
		returnPanel.setLayout(new BorderLayout());
		returnPanel.add(new JLabel("Data Memory View", JLabel.CENTER),
				BorderLayout.PAGE_START);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JPanel numPanel = new JPanel();
		JPanel intPanel = new JPanel();
		JPanel hexPanel = new JPanel();
		numPanel.setLayout(new GridLayout(0, 1));
		intPanel.setLayout(new GridLayout(0, 1));
		hexPanel.setLayout(new GridLayout(0, 1));
		panel.add(numPanel, BorderLayout.LINE_START);
		panel.add(intPanel, BorderLayout.CENTER);
		panel.add(hexPanel, BorderLayout.LINE_END);
		for (int i = 0; i < Memory.DATA_SIZE; i++) {
			numPanel.add(memory.getDataLabel(i));
			intPanel.add(memory.getDataValue(i));
			hexPanel.add(memory.getDataHex(i));
		}
		scroller = new JScrollPane(panel);
		returnPanel.add(scroller);
		return returnPanel;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (scroller != null && memory != null
				&& memory.getDataHighlight() != null) {
			JScrollBar bar = scroller.getVerticalScrollBar();
			Rectangle bounds = memory.getDataHighlight();
			bar.setValue(Math.max(0, bounds.y - 15 * bounds.height));
		}
	}
}
