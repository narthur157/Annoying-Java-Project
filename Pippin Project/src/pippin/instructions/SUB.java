package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.Processor;


public class SUB extends Instruction {

	public SUB(Processor cpu, Memory memory) {
		super(cpu, memory);
	}
	@Override
	public void execute(int arg, boolean immediate, boolean indirect) throws DataAccessException {
	    if (immediate) {
	        cpu.setAccumulator(cpu.getAccumulator() - arg);
	    } else if (indirect) {
	        int arg1 = memory.getData(arg);
	        cpu.setAccumulator(cpu.getAccumulator() - memory.getData(arg1));                    
	    } else {
	        cpu.setAccumulator(cpu.getAccumulator() - memory.getData(arg));         
	    }
	    cpu.incrementCounter();     
	}
}
