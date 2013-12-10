package pippin;

public class Processor {	
	private int accumulator, programCounter;
	public int getAccumulator() {
		return accumulator;
	}
	public void setAccumulator(int a) {
		accumulator = a;
	}
	public int getProgramCounter() {
		return programCounter;
	}
	public void setProgramCounter(int pc) {
		programCounter = pc;
	}
	public void incrementCounter() {
		programCounter++;
	}
}
