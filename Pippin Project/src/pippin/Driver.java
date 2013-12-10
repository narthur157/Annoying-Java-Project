package pippin;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {
    public static void main(String args[]) {
        File inFile = new File("test1.pasm");
        File outFile = new File("test1.pexe");
        Assembler.assemble(inFile, outFile);
        try {
            Scanner test = new Scanner(new File("test1.pexe"));
            while(test.hasNextLine()) {
                String line = test.nextLine();
                if(line.length() > 0) {
                    int num = Integer.parseInt(line);
                    String str =  "00000000000000000000000000000000" +
                            Integer.toBinaryString(num);
                    str = str.substring(str.length()-32);               
                    System.out.println(str);
                }
            }
            test.close();//
        } catch (NumberFormatException e) {
            System.out.println("You did not write ints to the file: " + e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}