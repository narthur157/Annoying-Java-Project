package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class STO extends Instruction {

	public STO(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException {
		if (immediate) {
			throw new DataAccessException();
		}
		else if (indirect) {
			memory.setData(memory.getData(arg), cpu.getAccumulator());
			cpu.incrementCounter();
		}
		else {
			memory.setData(arg, cpu.getAccumulator());
			cpu.incrementCounter();
		}
	}

}
