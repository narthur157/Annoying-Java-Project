package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class MUL extends Instruction {

	public MUL(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			cpu.setAccumulator(cpu.getAccumulator()*arg);
		}
		else if (indirect) {
			cpu.setAccumulator(cpu.getAccumulator()*memory.getData(memory.getData(arg)));
		}
		else {
			cpu.setAccumulator(cpu.getAccumulator()*memory.getData(arg));
		}
		cpu.incrementCounter();

	}

}
