package pippin.instructions;
import pippin.DataAccessException;
import pippin.DivideByZeroException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class DIV extends Instruction {

	public DIV(Processor cpu, Memory memory) {
		super(cpu, memory);
		
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException, DivideByZeroException {
		if (immediate) {
			if (arg==0) throw new DivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator()/arg);
		}
		else if (indirect) {
			if (memory.getData(memory.getData(arg))==0) throw new DivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator()/memory.getData(memory.getData(arg)));
		}
		else {
			if (memory.getData(arg)==0) throw new DivideByZeroException();
			cpu.setAccumulator(cpu.getAccumulator()/memory.getData(arg));
		}
		cpu.incrementCounter();
	}

}
