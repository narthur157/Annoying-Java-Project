package pippin;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import pippin.instructions.MVDN;

public class MVDNTest {

    @Before
    public void setUp() throws Exception {
    }

    Processor cpu = new Processor();
    Memory memory = new Memory(new GUIMachine());
    int accInit;
    int ipInit;

    @Before
    public void setup() {
        for (int i = 0; i < Memory.DATA_SIZE; i++) {
            try {
                memory.setData(i, i%2);
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
    public void testMVDN() {
    	try {
    		memory.setData(0,  10);
    		memory.setData(1, 11);
    		memory.setData(2, 10);
    		for(int i = 10; i<30; i++) {
    			memory.setData(i,i*2);
    		}
    		Instruction instr = new MVDN(cpu, memory);
    		int[] before = new int[11];
    		for (int i = 11; i<=21; i++) {
    			before[i]=memory.getData(i);
    		}
    		instr.execute(0, false, false);
    		int[] after = new int[11];

    		for (int i = 10; i<=20; i++) {
    			after[i]=memory.getData(i);
    		}
    		assertArrayEquals("The contents of mem[11-21] should be moved 1 down",
    				before, after);
    		
    		assertEquals("The contents of mem[21] should be 0", 0, memory.getData(21));
    		
    		//	{memory.getData(0),memory.getData(1),memory.getData(2)}	
    	}
    	catch (Exception e) {}
    	
    }

}
