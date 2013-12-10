package pippin;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class Memory {
	public static final int DATA_SIZE = 512;
	public static final int CODE_SIZE = 256;
	public static final Color HIGHLIGHT_COLOR = Color.YELLOW;

	private DataGUI[] dataComponents = new DataGUI[DATA_SIZE];
	private CodeGUI[] codeComponents = new CodeGUI[CODE_SIZE];
	private GUIMachine machine;
	private int currentCodeHighlight = 0;
	private int currentDataHighlight = 0;
	private Rectangle dataHighlight;
	private Rectangle codeHighlight;

	public Memory(GUIMachine m) {
		machine = m;
		for (int i = 0; i < CODE_SIZE; i++) {
			codeComponents[i] = new CodeGUI(i);
		}
		for (int i = 0; i < DATA_SIZE; i++) {
			dataComponents[i] = new DataGUI(i);
		}
	}

	public void setData(int index, int value) throws DataAccessException {
		if (index < 0 || index >= dataComponents.length) {
			throw new DataAccessException("ERROR: Cannot access data location "
					+ index);
		}
		dataComponents[index].setValue(value);
		dataComponents[currentDataHighlight].setColor(Color.WHITE);
		currentDataHighlight = index;
		dataComponents[currentDataHighlight].setColor(HIGHLIGHT_COLOR);
		dataHighlight = dataComponents[currentDataHighlight].rowIndex
				.getBounds();
	}

	public int getData(int index) throws DataAccessException {
		if (index < 0 || index >= dataComponents.length) {
			throw new DataAccessException("ERROR: Cannot access data location "
					+ index);
		}
		return dataComponents[index].value;
	}

	public void setCode(int index, int opcode, int arg)
			throws CodeAccessException {
		if (index < 0 || index >= codeComponents.length) {
			throw new CodeAccessException("ERROR: Cannot access code location "
					+ index);
		}
		codeComponents[index].setCode(opcode, arg);
	}

	public int getOp(int index) throws CodeAccessException {
		if (index < 0 || index >= codeComponents.length) {
			throw new CodeAccessException("ERROR: Cannot access code location "
					+ index);
		}
		codeComponents[currentCodeHighlight].setColor(Color.WHITE);
		currentCodeHighlight = machine.getProgramCounter();
		if (machine.getState() != States.NOTHING_LOADED) {
			codeComponents[currentCodeHighlight].setColor(HIGHLIGHT_COLOR);
		} else {
			dataComponents[currentDataHighlight].setColor(Color.WHITE);
		}
		codeHighlight = codeComponents[currentCodeHighlight].rowIndex
				.getBounds();
		return codeComponents[index].opcode;
	}
	public int getOpcode(int index) throws CodeAccessException {
		if (index < 0 || index >= codeComponents.length) {
			throw new CodeAccessException("ERROR: Cannot access code location "
					+ index);
		}
		codeComponents[currentCodeHighlight].setColor(Color.WHITE);
		currentCodeHighlight = machine.getProgramCounter();
		if (machine.getState() != States.NOTHING_LOADED) {
			codeComponents[currentCodeHighlight].setColor(HIGHLIGHT_COLOR);
		} else {
			dataComponents[currentDataHighlight].setColor(Color.WHITE);
		}
		codeHighlight = codeComponents[currentCodeHighlight].rowIndex
				.getBounds();
		return codeComponents[index].opcode;
	}
	public int getArg(int index) throws CodeAccessException {
		if (index < 0 || index >= codeComponents.length) {
			throw new CodeAccessException("ERROR: Cannot access code location "
					+ index);
		}
		return codeComponents[index].arg;
	}

	// Note package private -- just for JUnit test
	int[] getData() {
		int[] retVal = new int[DATA_SIZE];
		for (int i = 0; i < DATA_SIZE; i++)
			retVal[i] = dataComponents[i].value;
		return retVal;
	}

	public void clearMemory() {
		for (int i = 0; i < CODE_SIZE; i++) {
			codeComponents[i].arg = 0;
			codeComponents[i].opcode = 0;
			codeComponents[i].rowInstr.setText("");
			codeComponents[i].rowHex.setText("");
		}
		for (int i = 0; i < DATA_SIZE; i++) {
			dataComponents[i].value = 0;
			dataComponents[i].rowValue.setText("");
			dataComponents[i].rowHex.setText("");
		}
		codeComponents[currentCodeHighlight].setColor(Color.WHITE);
		currentCodeHighlight = 0;
		if (machine.getState() != States.NOTHING_LOADED) {
			codeComponents[currentCodeHighlight].setColor(HIGHLIGHT_COLOR);
		} else {
			dataComponents[currentDataHighlight].setColor(Color.WHITE);
		}
		codeHighlight = codeComponents[currentCodeHighlight].rowIndex
				.getBounds();
		dataComponents[currentDataHighlight].setColor(Color.WHITE);
		currentDataHighlight = 0;
		dataComponents[currentDataHighlight].setColor(HIGHLIGHT_COLOR);
	}

	public Rectangle getDataHighlight() {
		return dataHighlight;
	}

	public Rectangle getCodeHighlight() {
		return codeHighlight;
	}

	public JLabel getDataLabel(int index) {
		return dataComponents[index].rowIndex;
	}

	public JTextField getDataValue(int index) {
		return dataComponents[index].rowValue;
	}

	public JTextField getDataHex(int index) {
		return dataComponents[index].rowHex;
	}

	private class DataGUI {
		private int value;
		private JLabel rowIndex;
		private JTextField rowValue;
		private JTextField rowHex;

		public DataGUI(int loc) {
			rowIndex = new JLabel(loc + ": ", JLabel.RIGHT);
			rowValue = new JTextField(10);
			rowHex = new JTextField(10);
			rowIndex.setOpaque(true);
		}

		public void setValue(int value) {
			this.value = value;
			rowValue.setText("" + value);
			rowHex.setText("0x" + Integer.toHexString(value).toUpperCase());
		}

		public void setColor(Color color) {
			rowIndex.setBackground(color);
			rowValue.setBackground(color);
			rowHex.setBackground(color);
		}
	}

	public JLabel getCodeLabel(int index) {
		return codeComponents[index].rowIndex;
	}

	public JTextField getCodeText(int index) {
		return codeComponents[index].rowInstr;
	}

	public JTextField getCodeHex(int index) {
		return codeComponents[index].rowHex;
	}

	private class CodeGUI {
		private int opcode;
		private int arg;
		private JLabel rowIndex;
		private JTextField rowInstr;
		private JTextField rowHex;

		public CodeGUI(int loc) {
			rowIndex = new JLabel(loc + ": ", JLabel.RIGHT);
			rowInstr = new JTextField(10);
			rowHex = new JTextField(10);
			rowIndex.setOpaque(true);
		}

		public void setCode(int opcode, int arg) {
			this.opcode = opcode;
			this.arg = arg;
			System.out.println(opcode);
			Instruction in = machine.INSTRUCTION_SET[opcode / 4];
			boolean immediate = opcode % 2 == 1;
			boolean indirect = (opcode / 2) % 2 == 1;
			System.out.println(in);
			String inString = in.toString();
			if (immediate)
				inString += "#";
			else if (indirect)
				inString += "&";
			String s = "";
			String hexString = Integer.toHexString(opcode);
			if (hexString.length() == 1)
				hexString = "0" + hexString;
			hexString += Integer.toHexString(arg);
			rowHex.setText("0x" + hexString.toUpperCase());
			if (arg < 0) {
				arg*=-1;
				s = "-" + Integer.toHexString(arg);
			}
			else {
				s = Integer.toHexString(arg);
			}
			rowInstr.setText(inString + " "
					+ s.toUpperCase());
		}

		public void setColor(Color color) {
			rowIndex.setBackground(color);
			rowInstr.setBackground(color);
			rowHex.setBackground(color);
		}
	}

}
