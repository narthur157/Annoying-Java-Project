package pippin;

abstract public class Instruction { 
    protected Processor cpu;
    protected Memory memory;

    public Instruction(Processor cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    /** 
     * Method to return the name of this instruction, e.g. "NOP" "LOD" 
     * @return the name of the instruction 
     */ 
    @Override
    public String toString() { 
        return getClass().getSimpleName();
    } 

    /** 
     * Method to execute this instruction for the given argument. The details 
     * are explained in list of instructions for the Pippin computer. 
     * NOTE: If the instruction does not use an argument, then the argument 
     * is passed as 0 
     * @param arg the argument passed to the instruction
     * @param immediate indicates if the addressing mode is immediate
     * @param indirect indicates if the addressing mode is indirect
     * @throws DataAccessException if access to the data in Memory throws an 
     * exception
     */
    public abstract void execute(int arg, boolean immediate, boolean indirect) 
            throws DataAccessException;

} 