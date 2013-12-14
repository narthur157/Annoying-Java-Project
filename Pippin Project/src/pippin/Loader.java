package pippin;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Loader {
	static public void load(Memory m, File f) throws IOException,
			CodeAccessException, DataAccessException {
		Scanner file = new Scanner(f);
		int index = 0;
		boolean inData = false;
		while (file.hasNextLine()) {
			String line = file.nextLine();

			if (line.equals("11111111111111111111111111111111")) {
				inData = true;
				if (file.hasNextLine())
					line = file.nextLine();
			}
			if (!inData) {
				long arg = 0;
				long opcode = Long.parseLong(line, 2);
				String o = null;
				o = Assembler.first(Assembler.opcode, (int) opcode / 4);
				System.out.print("Opcode: " + opcode + " Instruction : " + o
						+ "    ");
				if (!Assembler.noArgument.contains(o)) {
					String dline = file.nextLine();
					if (!(dline.charAt(0) == '1' && dline.length() == 32)) {
						arg = Long.parseLong(dline, 2);
					} else {
						StringBuilder str = new StringBuilder();
						for (int i = 0; i < dline.length(); i++) {
							if (dline.charAt(i) == '0')
								str.append('1');
							else
								str.append('0');
						}
						arg = Long.parseLong(str.toString(), 2) + 1;
						arg *= -1;
					}
				}
				m.setCode(index, (int) opcode, (int) arg);
			} else {
				int address = Integer.parseInt(line, 2);
				if (file.hasNextInt()) {
					System.out.println(address);
					int value = Integer.parseInt(file.nextLine(), 2);
					m.setData(address, value);
				}

			}
			index++;
		}
		file.close();

	}
}
