package pippin;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pippin.instructions.ADD;
import pippin.instructions.AND;
import pippin.instructions.CMPL;
import pippin.instructions.CMPZ;
import pippin.instructions.DIV;
import pippin.instructions.HALT;
import pippin.instructions.JMPZ;
import pippin.instructions.JUMP;
import pippin.instructions.LOD;
import pippin.instructions.MUL;
import pippin.instructions.NOP;
import pippin.instructions.NOT;
import pippin.instructions.STO;
import pippin.instructions.SUB;

public class InstructionTester {

    @Before
    public void setUp() throws Exception {
    }

    Processor cpu = new Processor();
    Memory memory = new Memory(new GUIMachine());
    int[] dataCopy = new int[Memory.DATA_SIZE];
    int accInit;
    int ipInit;

    @Before
    public void setup() {
        for (int i = 0; i < Memory.DATA_SIZE; i++) {
            dataCopy[i] = -5*Memory.DATA_SIZE + 10*i;
            try {
                memory.setData(i, -5*Memory.DATA_SIZE + 10*i);
            } catch (DataAccessException e) {
                e.printStackTrace();
            }
            // Initially the memory will contain a known spread
            // of different numbers: 
            // -2560, -2550, -2540, ..., 0, 10, 20, ..., 2550 
            // This allows us to check that the instructions do 
            // not corrupt memory unexpectedly.
            // 0 is at index 256
        }
        accInit = 0;
        ipInit = 0;
    }

    @Test
    public void testNOPbasics(){
        Instruction instr = new NOP(cpu, memory);
        assertEquals("Name is NOP", "NOP", instr.toString());
    }

    @Test
    public void testLODbasics(){
        Instruction instr = new LOD(cpu, memory);
        assertEquals("Name is LOD", "LOD", instr.toString());
    }

    @Test
    public void testSTObasics(){
        Instruction instr = new STO(cpu, memory);
        assertEquals("Name is STO", "STO", instr.toString());
    }

    @Test
    public void testADDbasics(){
        Instruction instr = new ADD(cpu, memory);
        assertEquals("Name is ADD", "ADD", instr.toString());
    }

    @Test
    public void testSUBbasics(){
        Instruction instr = new SUB(cpu, memory);
        assertEquals("Name is SUB", "SUB", instr.toString());
    }

    @Test
    public void testMULbasics(){
        Instruction instr = new MUL(cpu, memory);
        assertEquals("Name is MUL", "MUL", instr.toString());
    }

    @Test
    public void testDIVbasics(){
        Instruction instr = new DIV(cpu, memory);
        assertEquals("Name is DIV", "DIV", instr.toString());
    }

    @Test
    public void testANDbasics(){
        Instruction instr = new AND(cpu, memory);
        assertEquals("Name is AND", "AND", instr.toString());
    }

    @Test
    public void testNOTbasics(){
        Instruction instr = new NOT(cpu, memory);
        assertEquals("Name is NOT", "NOT", instr.toString());
    }

    @Test
    public void testCMPZbasics(){
        Instruction instr = new CMPZ(cpu, memory);
        assertEquals("Name is CMPZ", "CMPZ", instr.toString());
    }

    @Test
    public void testCMPLbasics(){
        Instruction instr = new CMPL(cpu, memory);
        assertEquals("Name is CMPL", "CMPL", instr.toString());
    }

    @Test
    public void testJUMPbasics(){
        Instruction instr = new JUMP(cpu, memory);
        assertEquals("Name is JUMP", "JUMP", instr.toString());
    }

    @Test
    public void testJMPZbasics(){
        Instruction instr = new JMPZ(cpu, memory);
        assertEquals("Name is JMPZ", "JMPZ", instr.toString());
    }

    @Test
    public void testHALTbasics(){
        Instruction instr = new HALT(cpu, memory);
        assertEquals("Name is HALT", "HALT", instr.toString());
    }

    @Test
    public void testNOP(){
        Instruction instr = new NOP(cpu, memory);
        //Test that execute impacts the machine appropriately
        try {
            instr.execute(0,false,false);
        } catch (Exception ex) {
            fail("Exception thrown: " + ex.getClass().getSimpleName());
        }
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData());
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit+1,
                cpu.getProgramCounter());
        //Test accumulator untouched
        assertEquals("Accumulator unchanged", accInit,
                cpu.getAccumulator());
    }

    @Test
    // Test whether load is correct with direct addressing
    public void testLOD(){
        Instruction instr = new LOD(cpu, memory);
        cpu.setAccumulator(27);
        int arg = 12;
        try {
            // should load -2560+120 into the accumulator
            instr.execute(arg, false, false);
        } catch(DataAccessException ex){
            fail("Should not throw DataAccessException");           
        } catch (Exception e) {
            fail("Should not throw any exceptions: " + 
                    e.getClass().getSimpleName());
        }
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData());
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit+1,
                cpu.getProgramCounter());
        //Test accumulator modified
        assertEquals("Accumulator changed", -2560+120,
                cpu.getAccumulator());
    }

    @Test
    // Test whether load is correct with immediate addressing
    public void testLODimmediate(){
        Instruction instr = new LOD(cpu, memory);
        cpu.setAccumulator(27);
        int arg = 12;
        try {
            // should load 12 into the accumulator
            instr.execute(arg, true, false);
        } catch(DataAccessException ex){
            fail("Should not throw DataAccessException");           
        } catch (Exception e) {
            fail("Should not throw any exceptions: " + 
                    e.getClass().getSimpleName());
        }
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData());
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit+1,
                cpu.getProgramCounter());
        //Test accumulator modified
        assertEquals("Accumulator changed", 12,
                cpu.getAccumulator());
    }

    @Test
    // Test whether load is correct with direct addressing
    public void testLODindirect() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory);
        cpu.setAccumulator(-1);
        int arg = 260;
        // should load data[-2560+2600] = data[40] = -2560 + 400
        // into the accumulator
        instr.execute(arg, false, true);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData());
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit+1,
                cpu.getProgramCounter());
        //Test accumulator modified
        assertEquals("Accumulator changed", -2560+400,
                cpu.getAccumulator());
    }   

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when direct addressing is used and the operand address 
    // is a negative index.
    public void testLODnegArg() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory); 
        int arg = -12;
        instr.execute(arg, false, false);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when direct addressing is used and the operand address 
    // is larger than the array size.
    public void testLODbigArg() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory); 
        int arg = Memory.DATA_SIZE+1;
        instr.execute(arg, false, false);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when direct addressing is used and the operand address 
    // is a negative index.
    public void testLODnegArgIndirect1() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory); 
        int arg = -12;
        instr.execute(arg, false, true);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when indirect addressing is used and the indirect address 
    // is a negative index.
    public void testLODnegArgIndirect2() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory); 
        int arg = 200; // address is -2560+2000 < 0
        instr.execute(arg, false, true);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when indirect addressing is used and the operand address 
    // is larger than the array size.
    public void testLODbigArgIndirect1() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory); 
        int arg = Memory.DATA_SIZE+1;
        instr.execute(arg, false, true);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when indirect addressing is used and the indirect address 
    // is larger than the array size.
    public void testLODbigArgIndirect2() throws DataAccessException{
        Instruction instr = new LOD(cpu, memory); 
        int arg = 400; // address is -2560+4000 > 1000 > 511
        instr.execute(arg, false, true);
    }

    @Test
    // Test whether store is correct with direct addressing
    public void testSTOdirect() throws DataAccessException{
        Instruction instr = new STO(cpu, memory);
        int arg = 12;
        cpu.setAccumulator(567);
        dataCopy[12] = 567;
        instr.execute(arg, false, false);
        //Test memory is changed correctly
        assertArrayEquals(dataCopy, memory.getData());
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit+1,
                cpu.getProgramCounter());
        //Test accumulator unchanged
        assertEquals("Accumulator unchanged", 567,
                cpu.getAccumulator());
    }

    @Test
    // Test whether store is correct with indirect addressing
    public void testSTOindirect() throws DataAccessException{
        Instruction instr = new STO(cpu, memory);
        int arg = 260; // -2560+2600 = 40
        cpu.setAccumulator(567);
        dataCopy[40] = 567;
        instr.execute(arg, false, true);
        //Test memory is changed correctly
        assertArrayEquals(dataCopy, memory.getData());
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit+1,
                cpu.getProgramCounter());
        //Test accumulator unchanged
        assertEquals("Accumulator unchanged", 567,
                cpu.getAccumulator());
    }

    @Test (expected=DataAccessException.class)
    // this test checks whether the DataAccessException is thrown 
    // when direct addressing is used and the operand address 
    // is a negative index.
    public void testSTOnegArg() throws DataAccessException{
        Instruction instr = new STO(cpu, memory); 
        int arg = -12;
        instr.execute(arg, false, false);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when direct addressing is used and the operand address 
    // is larger than the array size.
    public void testSTObigArg() throws DataAccessException{
        Instruction instr = new STO(cpu, memory); 
        int arg = Memory.DATA_SIZE+1;
        instr.execute(arg, false, false);
    }

    @Test (expected=DataAccessException.class)
    // this test checks whether the DataAccessException is thrown 
    // when direct addressing is used and the operand address 
    // is a negative index.
    public void testSTOnegArgIndirect1() throws DataAccessException{
        Instruction instr = new STO(cpu, memory); 
        int arg = -12;
        instr.execute(arg, false, false);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when indirect addressing is used and the indirect address 
    // is a negative index.
    public void testSTOnegArgIndirect2() throws DataAccessException{
        Instruction instr = new STO(cpu, memory); 
        int arg = 200; // address is -2560+2000 < 0
        instr.execute(arg, false, true);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when indirect addressing is used and the operand address 
    // is larger than the array size.
    public void testSTObigArgIndirect1() throws DataAccessException{
        Instruction instr = new STO(cpu, memory); 
        int arg = Memory.DATA_SIZE+1;
        instr.execute(arg, false, true);
    }

    @Test (expected=DataAccessException.class) 
    // this test checks whether the DataAccessException is thrown 
    // when indirect addressing is used and the indirect address 
    // is larger than the array size.
    public void testSTObigArgIndirect2() throws DataAccessException{
        Instruction instr = new STO(cpu, memory); 
        int arg = 400; // address is -2560+4000 > 1000 > 511
        instr.execute(arg, false, true);
    }

    @Test 
    // this test checks whether the add is done correctly, when
    // addressing is immediate
    public void testADDimmediate() throws DataAccessException{
        Instruction instr = new ADD(cpu, memory); 
        int arg = 12; 
        cpu.setAccumulator(200);
        instr.execute(arg, true, false); 
        // should have added 12 to accumulator
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200+12,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the add is done correctly, when
    // addressing is direct
    public void testADD() throws DataAccessException{
        Instruction instr = new ADD(cpu, memory); 
        int arg = 12; // we know that memory value is -2560+120
        cpu.setAccumulator(200);
        instr.execute(arg, false, false); 
        // should have added -2560+120 to accumulator
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200-2560+120,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the add is done correctly, when
    // addressing is indirect
    public void testADDindirect() throws DataAccessException{
        Instruction instr = new ADD(cpu, memory); 
        int arg = 260; // we know that address is -2560+2600 = 40
        // and the memory value is data[40] = -2560+400 = -2160 
        cpu.setAccumulator(200);
        instr.execute(arg, false, true); 
        // should have added -2560+400 to accumulator
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200-2560+400,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the subtract is done correctly, when
    // addressing is immediate
    public void testSUBimmediate() throws DataAccessException{
        Instruction instr = new SUB(cpu, memory); 
        int arg = 12; 
        cpu.setAccumulator(200);
        instr.execute(arg, true, false); 
        // should have subtracted 12 from accumulator
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200-12,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the subtract is done correctly, when
    // addressing is direct
    public void testSUB() throws DataAccessException{
        Instruction instr = new SUB(cpu, memory); 
        int arg = 12; // we know that memory value is -2560+120
        cpu.setAccumulator(200);
        instr.execute(arg, false, false); 
        // should have subtracted -2560+120 from accumulator
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200+2560-120,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the subtract is done correctly, when
    // addressing is indirect
    public void testSUBindirect() throws DataAccessException{
        Instruction instr = new SUB(cpu, memory); 
        int arg = 260; // we know that address is -2560+2600 = 40
        // and the memory value is data[40] = -2560+400 = -2160 
        cpu.setAccumulator(200);
        instr.execute(arg, false, true); 
        // should have subtracted -2560+400 from accumulator
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200+2560-400,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the multiplication is done correctly, when
    // addressing is immediate
    public void testMULimmediate() throws DataAccessException{
        Instruction instr = new MUL(cpu, memory); 
        int arg = 12; 
        cpu.setAccumulator(200);
        instr.execute(arg, true, false); 
        // should have multiplied accumulator by 12
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200*12,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the multiplication is done correctly, when
    // addressing is direct
    public void testMUL() throws DataAccessException{
        Instruction instr = new MUL(cpu, memory); 
        int arg = 12; // we know that memory value is -2560+120
        cpu.setAccumulator(200);
        instr.execute(arg, false, false); 
        // should have multiplied accumulator by -2560+120 
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200*(-2560+120),
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the multiplication is done correctly, when
    // addressing is indirect
    public void testMULindirect() throws DataAccessException{
        Instruction instr = new MUL(cpu, memory); 
        int arg = 260; // we know that address is -2560+2600 = 40
        // and the memory value is data[40] = -2560+400 = -2160 
        cpu.setAccumulator(200);
        instr.execute(arg, false, true); 
        // should have multiplied to accumulator -2560+400
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200*(-2560+400),
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the multiplication is done correctly, when
    // addressing is immediate
    public void testDIVimmediate() throws DataAccessException{
        Instruction instr = new DIV(cpu, memory); 
        int arg = 12; 
        cpu.setAccumulator(200);
        instr.execute(arg, true, false); 
        // should have divided accumulator by 12
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200/12,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the multiplication is done correctly, when
    // addressing is direct
    public void testDIV() throws DataAccessException{
        Instruction instr = new DIV(cpu, memory); 
        int arg = 12; // we know that memory value is -2560+120
        cpu.setAccumulator(200);
        instr.execute(arg, false, false); 
        // should have divided accumulator by -2560+120 
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200/(-2560+120),
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the multiplication is done correctly, when
    // addressing is indirect
    public void testDIVindirect() throws DataAccessException{
        Instruction instr = new DIV(cpu, memory); 
        int arg = 260; // we know that address is -2560+2600 = 40
        // and the memory value is data[40] = -2560+400 = -2160 
        cpu.setAccumulator(200);
        instr.execute(arg, false, true); 
        // should have divided to accumulator -2560+400
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit + 1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200/(-2560+400),
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the jump is done correctly, when
    // addressing is direct
    public void testJUMPdirect() throws DataAccessException{
        Instruction instr = new JUMP(cpu, memory); 
        int arg = 260;  
        cpu.setAccumulator(200);
        instr.execute(arg, false, false); 
        // should have set the program counter to 40
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was changed", 260,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the jump is done correctly, when
    // addressing is indirect
    public void testJUMPindirect() throws DataAccessException{
        Instruction instr = new JUMP(cpu, memory); 
        int arg = 260; // the memory value is data[260] = -2560+2600 = 40 
        cpu.setAccumulator(200);
        instr.execute(arg, false, true); 
        // should have set the program counter to 40
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was changed", 40,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 200,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the jump is done correctly, when
    // addressing is direct
    public void testJMPZdirectAccumZero() throws DataAccessException{
        Instruction instr = new JMPZ(cpu, memory); 
        int arg = 260;  
        cpu.setAccumulator(0);
        instr.execute(arg, false, false); 
        // should have set the program counter to 40
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was changed", 260,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 0,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether the jump is done correctly, when
    // addressing is indirect
    public void testJMPZindirectAccumZero() throws DataAccessException{
        Instruction instr = new JMPZ(cpu, memory); 
        int arg = 260; // the memory value is data[260] = -2560+2600 = 40 
        cpu.setAccumulator(0);
        instr.execute(arg, false, true); 
        // should have set the program counter to 40
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was changed", 40,
                cpu.getProgramCounter());
        assertEquals("Accumulator was changed", 0,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether no jump is done if accumulator is zero, 
    // when addressing is direct
    public void testJMPZdirectAccumNonZero() throws DataAccessException{
        Instruction instr = new JMPZ(cpu, memory); 
        int arg = 260;  
        cpu.setAccumulator(200);
        instr.execute(arg, false, false); 
        // should have set the program counter incremented
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit+1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was not changed", 200,
                cpu.getAccumulator());
    }

    @Test 
    // this test checks whether no jump is done if accumulator is zero, 
    // when addressing is indirect
    public void testJMPZindirectAccumNonZero() throws DataAccessException{
        Instruction instr = new JMPZ(cpu, memory); 
        int arg = 260; // the memory value is data[260] = -2560+2600 = 40 
        cpu.setAccumulator(200);
        instr.execute(arg, false, true); 
        // should have set the program counter incremented
        assertArrayEquals(dataCopy, memory.getData()); 
        assertEquals("Instruction pointer was incremented", ipInit+1,
                cpu.getProgramCounter());
        assertEquals("Accumulator was not changed", 200,
                cpu.getAccumulator());
    }

    @Test
    // Check CMPL when comparing less than 0 gives true
    public void testCMPLmemLT0() throws DataAccessException{
        Instruction instr = new CMPL(cpu, memory);
        int arg = 100;
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check CMPL when comparing equal to 0 gives false
    public void testCMPLmemEQ0() throws DataAccessException{
        Instruction instr = new CMPL(cpu, memory);
        int arg = 256;
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check CMPL when comparing greater than 0 gives false
    public void testCMPLmemGT0() throws DataAccessException{
        Instruction instr = new CMPL(cpu, memory);
        int arg = 300;
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check CMPZ when comparing less than 0 gives false
    public void testCMPZmemLT0() throws DataAccessException{
        Instruction instr = new CMPZ(cpu, memory);
        int arg = 100;
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check CMPZ when comparing equal to 0 gives true
    public void testCMPZmemEQ0() throws DataAccessException{
        Instruction instr = new CMPZ(cpu, memory);
        int arg = 256;
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check CMPZ when comparing greater than 0 gives false
    public void testCMPZmemGT0() throws DataAccessException{
        Instruction instr = new CMPZ(cpu, memory);
        int arg = 300;
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum and mem equal to 0 gives false
    public void testANDaccEQ0memEQ0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 256;
        cpu.setAccumulator(0);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum and mem pos gives true
    public void testANDaccGT0memGT0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 300;
        cpu.setAccumulator(10);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum and mem neg gives true
    public void testANDaccLT0memLT0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 200;
        cpu.setAccumulator(-10);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum neg and mem pos gives true
    public void testANDaccLT0memGT0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 300;
        cpu.setAccumulator(-10);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum pos and mem neg gives true
    public void testANDaccGT0memLT0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 200;
        cpu.setAccumulator(10);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum pos mem equal to zero gives false
    public void testANDaccGT0memEQ0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 256;
        cpu.setAccumulator(10);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum neg mem equal to zero gives false
    public void testANDaccLT0memEQ0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 256;
        cpu.setAccumulator(-10);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum equal to zero and mem pos gives false
    public void testANDaccEQ0memGT0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 300;
        cpu.setAccumulator(0);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check AND when accum equal to zero and mem neg gives false
    public void testANDaccEQ0memLT0() throws DataAccessException{
        Instruction instr = new AND(cpu, memory);
        int arg = 200;
        cpu.setAccumulator(0);
        instr.execute(arg, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check NOT greater than 0 gives false
    public void testNOTaccGT0() throws DataAccessException{
        Instruction instr = new NOT(cpu, memory);
        cpu.setAccumulator(10);
        instr.execute(0, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

    @Test
    // Check NOT equal to 0 gives false
    public void testNOTaccEQ0() throws DataAccessException{
        Instruction instr = new NOT(cpu, memory);
        cpu.setAccumulator(0);
        instr.execute(0, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 1", 1,
                cpu.getAccumulator());
    }

    @Test
    // Check NOT less than 0 gives false
    public void testNOTaccLT0() throws DataAccessException{
        Instruction instr = new NOT(cpu, memory);
        cpu.setAccumulator(-10);
        instr.execute(0, false, false);
        //Test memory is not changed
        assertArrayEquals(dataCopy, memory.getData()); 
        //Test program counter incremented
        assertEquals("Instruction pointer incremented", ipInit + 1,
                cpu.getProgramCounter());
        //Accumulator is 1
        assertEquals("Accumulator is 0", 0,
                cpu.getAccumulator());
    }

}