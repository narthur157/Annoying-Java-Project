package pippin.instructions;
import pippin.DataAccessException;
import pippin.Instruction;
import pippin.Memory;
import pippin.MemoryMoveException;
import pippin.Processor;


public class MVDN extends Instruction {

	public MVDN(Processor cpu, Memory memory) {
		super(cpu, memory);
	}

	@Override
	public void execute(int arg, boolean immediate, boolean indirect)
			throws DataAccessException, MemoryMoveException {
		int A1;
		int A2;
		int N;
		if (indirect) {
			A1=memory.getData(memory.getData(arg));
			A2 = memory.getData(memory.getData(arg+1));
			N = memory.getData(memory.getData(arg+2));
		}
		else {
			A1 = memory.getData(arg);
			A2 = memory.getData(arg+1);
			N = memory.getData(arg+2);
		}
		if (A1 < A2) throw new MemoryMoveException("A1 must be > A2");
		if (N < 0) throw new MemoryMoveException("N must be > 0");
		for (int i=A2+N-1; i>=A2; i--) {
			memory.setData(i+N-1, memory.getData(A1+N-1));
			memory.setData(A1+N-1, 0);
		}
		cpu.incrementCounter();

	}

}
