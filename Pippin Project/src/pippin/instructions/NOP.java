package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class NOP extends Instruction {

	public NOP(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			
		}
		else if (indirect) {
			
		}
		else {
			cpu.incrementCounter();
		}
	}

}
