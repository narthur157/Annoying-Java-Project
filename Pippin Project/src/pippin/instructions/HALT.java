package pippin.instructions;
import pippin.*;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class HALT extends Instruction {
	private Machine machine;
	private GUIMachine gmachine;
	public HALT(Processor cpu, Memory memory) {
		super(cpu, memory);
		
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (machine != null) {
            machine.halt();
        }
        if (gmachine != null) {
            gmachine.halt();
        }

	}
	public void setMachine(Machine mach) {
		machine=mach;
	}
	public void setGmachine(GUIMachine machine) {
		gmachine=machine;
	}
	public GUIMachine getGmachine() {
		return gmachine;
	}

}
