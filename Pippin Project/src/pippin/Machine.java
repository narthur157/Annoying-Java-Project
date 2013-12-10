package pippin;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

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
//import pippin.instructions.NOP;
import pippin.instructions.NOT;
import pippin.instructions.STO;
import pippin.instructions.SUB;

public class Machine {
    public final Instruction[] INSTRUCTION_SET = new Instruction[16];
    private Processor proc = new Processor();
    private Memory memory = new Memory(new GUIMachine());
    private boolean running = false;

    public Machine() {
        INSTRUCTION_SET[0] = new NOP(proc, memory);
        // fill in the other elements
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
        ((HALT)INSTRUCTION_SET[15]).setMachine(this);
    }

    public void halt() {
        running = false;
    }
    public boolean getRunning() {
    	return running;
    }
    public void setRunning(Boolean bool) {
    	running=bool;
    }

    public static void main(String[] args) throws CodeAccessException, DataAccessException {
        Machine sim = new Machine();
        System.out.println("Name of file to assemble and run: ");
        Scanner keyboard = new Scanner(System.in);
        File asm = new File(keyboard.nextLine());
        if(asm.exists()) {
            File exe = new File("temp.pipex");
            Assembler.assemble(asm, exe);
            try {
            	Loader.load(sim.memory, exe);
            }
            catch (IOException e) {}
            sim.running = true;
            sim.proc.setProgramCounter(0);
            int pc = 0;
            while(sim.running) {
                pc = sim.proc.getProgramCounter();
                int opcode = sim.memory.getOpcode(pc);
                Instruction in = sim.INSTRUCTION_SET[opcode/4]; 
                int arg = sim.memory.getArg(pc);
                boolean immediate = opcode % 2 == 1;
                boolean indirect = (opcode/2) % 2 == 1;
                in.execute(arg, immediate, indirect);
            }
            System.out.println("Content of Data Memory after execution");
            for(int i = 0; i < 32; i++) {
                for(int j = 0; j < 16; j++) {
                    System.out.print(16*i+j + ":" + sim.memory.getData(16*i+j) + "\t|");
                }
                System.out.println();
            }
        } else {
            System.out.println("Sorry, cannot find file: " + asm.getAbsolutePath());
        }   
        keyboard.close();
    }
}