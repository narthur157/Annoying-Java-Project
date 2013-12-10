package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class JMPZ extends Instruction {

	public JMPZ(Processor cpu, Memory memory) {
		super(cpu, memory);

	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			
		}
		else if (indirect) {
			if (cpu.getAccumulator()==0) {
				cpu.setProgramCounter(memory.getData(arg));
			}
			else {
				cpu.incrementCounter();
			}
		}
		else {
			if (cpu.getAccumulator()==0) {
				cpu.setProgramCounter(arg);
			}
			else {
				cpu.incrementCounter();
			}
		}

	}

}
