package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class NOT extends Instruction {

	public NOT(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			//if (cpu.getAccumulator() != arg) cpu.setAccumulator(1);
			//else cpu.setAccumulator(0);
		}
		else if (indirect) {
			//if (cpu.getAccumulator() != memory.getData(memory.getData(arg))) cpu.setAccumulator(1);
			//else cpu.setAccumulator(0);
		}
		else {
			if (cpu.getAccumulator() ==0) cpu.setAccumulator(1);
			else cpu.setAccumulator(0);
			cpu.incrementCounter();
		}
		

	}

}
