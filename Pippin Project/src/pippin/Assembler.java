package pippin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * CLASS GUIMachine
 * 
 * @author Nick Arthur
 * @author Sri Edara
 * @author Tori Hallett
 * @author Alex Strong
 */

public class Assembler {
	static int lineCounter = 0;
	static boolean inCode = true;
	static boolean goodProgram = true;
	static boolean blankLineHit = false;
	static final String keyError = "ERROR: Not a valid code at line: ";

	static Set<String> allowsImmediate = new HashSet<String>(),
			allowsDirect = new HashSet<String>(),
			allowsIndirect = new HashSet<String>(),
			noArgument = new HashSet<String>();
	static Map<String, Integer> opcode = new HashMap<String, Integer>();
	static {
		allowsImmediate.add("LOD");
		allowsDirect.add("LOD");
		allowsIndirect.add("LOD");
		allowsDirect.add("STO");
		allowsIndirect.add("STO");
		allowsDirect.add("JUMP");
		allowsIndirect.add("JUMP");
		allowsDirect.add("JMPZ");
		allowsIndirect.add("JMPZ");
		noArgument.add("NOP");
		noArgument.add("HALT");
		allowsImmediate.add("ADD");
		allowsDirect.add("ADD");
		allowsIndirect.add("ADD");
		allowsImmediate.add("SUB");
		allowsDirect.add("SUB");
		allowsIndirect.add("SUB");
		allowsImmediate.add("MUL");
		allowsDirect.add("MUL");
		allowsIndirect.add("MUL");
		allowsImmediate.add("DIV");
		allowsDirect.add("DIV");
		allowsIndirect.add("DIV");
		allowsImmediate.add("AND");
		allowsDirect.add("AND");
		noArgument.add("NOT");
		allowsDirect.add("CMPZ");
		allowsDirect.add("CMPL");
		opcode.put("NOP", 0x0);
		opcode.put("LOD", 0x1);
		opcode.put("STO", 0x2);
		opcode.put("ADD", 0x3);
		opcode.put("SUB", 0x4);
		opcode.put("MUL", 0x5);
		opcode.put("DIV", 0x6);
		opcode.put("AND", 0x7);
		opcode.put("NOT", 0x8);
		opcode.put("CMPZ", 0x9);
		opcode.put("CMPL", 0xA);
		opcode.put("JUMP", 0xB);
		opcode.put("JMPZ", 0xC);
		opcode.put("HALT", 0xF);
	}

	public static boolean hasWhiteSpace(String[] spl) {
		for (String s : spl) {
			if (s.contains("\\s+") || s.contains("\n") || spl.length == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Assembles a File input containing Pippin assembly onto a File output
	 * containing the binary executable 
	 * Immediate mode is specified by #, indirect mode is specified by & 
	 * DATA specifies the end of code and the beginning of memory, which are explicitly separated 
	 * DATA lines are assembled into two lines each, the first hex number is the address of the data
	 * and the second number is the data (both assembled to binary in the .pexe file)
	 * & specifies an indirect instruction which uses the argument (a memory location) as a
	 * pointer to another memory location so that LOD& X puts memory[memory[X]]
	 * in the accumulator 
	 * # specifies an immediate instruction and uses a given number (i.e. ADD# 5 adds 5 to the accumulator contents)
	 * as the argument
	 * direct instruction which takes a value from the argument memory location (i.e. LOD X puts mem[X] in the accumulator)
	 * 
	 * Example factorial (8!) program 
	 * LOD 0 
	 * STO 1 
	 * LOD 0 
	 * SUB# 1 
	 * STO 0 
	 * CMPZ 0 
	 * SUB# 1
	 * JMPZ B 
	 * LOD 0 
	 * MUL 1 
	 * JUMP 1 
	 * HALT 
	 * DATA 
	 * 0 8
	 * Factorial (7!) indirect example
	 * LOD& 0 
	 * STO& 1 
	 * LOD& 0 
	 * SUB# 1 
	 * STO& 0 
	 * LOD& 0 
	 * STO 5 
	 * CMPZ 5 
	 * SUB# 1 
	 * JMPZ& 3
	 * LOD& 0 
	 * MUL& 1 
	 * JUMP& 2
	 * HALT 
	 * DATA 
	 * 0 10 
	 * 1 20 
	 * 2 1 
	 * 3 D 
	 * 10 7
	 * 
	 * 
	 * @param input the source file containing assembly instructions
	 * @param output the file that binary code should be written to
	 * @throws IOException if the files input or output cannot be opened
	 * @throws NumberFormatException if parseInt encounters something bad
	 * @return True if the input File contained valid code
	 */
	public static boolean assemble(File input, File output) throws IOException,
			NumberFormatException, ArrayIndexOutOfBoundsException {
		blankLineHit = false;
		goodProgram = true;
		inCode = true;
		lineCounter = 0;
		Scanner inp = new Scanner(input);
		PrintWriter outp = new PrintWriter(output);

		while (inp.hasNextLine() && goodProgram) {
			lineCounter++;
			String str = inp.nextLine();
			String trimmed = str.trim();
			String[] spl = trimmed.split("\\s+");
			// check for whitespace at the beginning
			if (!(str.charAt(0) == trimmed.charAt(0))) {
				goodProgram = false;
			} else if (hasWhiteSpace(spl)) {
				blankLineHit = true;
				goodProgram = false;
			} else if (spl.length > 2) {
				goodProgram = false;
			}
			if (goodProgram) {
				if (inCode) {
					if (spl[0].equals("DATA")) {
						outp.write("11111111111111111111111111111111\n");
						inCode = false;
					} else {
						String c = getCodes(spl);
						outp.write(c);
					}
				} else {
					String d = getData(spl);
					outp.write(d);
				}
			}
		}
		inp.close();
		outp.close();
		if (!goodProgram && output != null && output.exists()) {
			output.delete();
		}
		return goodProgram;
	}

	public static String getData(String[] spl) {
		String s = "";
		s = (spl[0].length() > 0 ? Integer.toBinaryString(Integer.parseInt(
				spl[0], 16)) : "")
				+ "\n"
				+ (spl[0].length() > 0 ? Integer.toBinaryString(Integer
						.parseInt(spl[1], 16)) : "") + "\n";
		return s;
	}

	public static String getCodes(String[] spl) {
		int code = 0;
		boolean direct = false;
		boolean immediate = false;
		boolean indirect = false;
		String s = "";
		if (noArgument.contains(spl[0])) {
			code = opcode.get(spl[0]) * 4;
			s += Integer.toBinaryString(code) + "\n";
			if (spl.length > 1) goodProgram=false;
			return s;
		}
		if (spl.length == 2) {
			if (Character.isAlphabetic(spl[1].charAt(0))) goodProgram=false;
			char c = spl[0].charAt(spl[0].length() - 1);
			immediate = (c == '#');
			indirect = (c == '&');
			if (!(immediate || indirect))
				direct = true;
		}
		return makeCodeString(direct, immediate, indirect, spl);
	}

	public static String makeCodeString(boolean direct, boolean immediate,
			boolean indirect, String[] spl) {
		int code = 0;
		int n = 0;
		try {
			n = Integer.parseInt(spl[1], 16);
		}
		catch (ArrayIndexOutOfBoundsException e){
			goodProgram=false;
			return "";
		}
		String s = "";
		String k = spl[0]; // INSTRUCTION CODE

		if (immediate || indirect) {
			k = spl[0].substring(0, spl[0].length() - 1);
		}
		if (opcode.containsKey(k))
			code = opcode.get(k);
		else
			goodProgram = false;

		if (direct) {
			if (allowsDirect.contains(k))
				code = code * 4;
			else
				goodProgram = false;
		} else if (indirect) {
			if (allowsIndirect.contains(k))
				code = code * 4 + 2;
			else
				goodProgram = false;
		} else if (immediate) {
			if (allowsImmediate.contains(k))
				code = code * 4 + 1;
			else
				goodProgram = false;
		}
		s += makeBinaryCodeString(code, n) + "\n";
		if (goodProgram)
			return s;
		else {
			return "";
		}
	}

	public static String makeBinaryCodeString(int code, int n) {
		return (Integer.toBinaryString(code) + "\n" + Integer.toBinaryString(n));
	}

	public static String first(Map<String, Integer> m, int i) {
		String ret = "";
		if (m.containsValue(i)) {
			for (String str : m.keySet()) {
				if (m.get(str) == i) {
					ret = str;
					break;
				}
			}
		}
		return ret;
	}
}