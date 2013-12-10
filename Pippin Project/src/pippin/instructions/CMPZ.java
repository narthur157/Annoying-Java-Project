package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class CMPZ extends Instruction {

	public CMPZ(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			//throw new DataAccessException();
		}
		else if (indirect) {

		}
		else {
			if (memory.getData(arg)==0) cpu.setAccumulator(1);
			else cpu.setAccumulator(0);
			cpu.incrementCounter();
			//throw new DataAccessException();
		}
	}

}
