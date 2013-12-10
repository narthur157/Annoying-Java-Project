package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class LOD extends Instruction {

	public LOD(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			cpu.setAccumulator(arg);
		}
		else if (indirect) {
			cpu.setAccumulator(memory.getData(memory.getData(arg)));
		}
		else {
			cpu.setAccumulator(memory.getData(arg));
		}
		cpu.incrementCounter();

	}

}
