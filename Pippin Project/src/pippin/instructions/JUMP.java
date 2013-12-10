package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class JUMP extends Instruction {

	public JUMP(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			
		}
		else if (indirect) {
			cpu.setProgramCounter(memory.getData(arg));
		}
		else {
			cpu.setProgramCounter(arg);
		}

	}

}
