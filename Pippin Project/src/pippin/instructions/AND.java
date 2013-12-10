package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class AND extends Instruction {

	public AND(Processor cpu, Memory memory) {
		super(cpu, memory);

	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			if (cpu.getAccumulator() !=0 && arg != 0) cpu.setAccumulator(1);
			else cpu.setAccumulator(0);
			cpu.incrementCounter();
		}
		else if (indirect) {
			throw new DataAccessException();
		}
		else {
			if (cpu.getAccumulator() != 0 && memory.getData(arg) != 0) cpu.setAccumulator(1);
			else cpu.setAccumulator(0);
			cpu.incrementCounter();
		}

	}

}
