package pippin;


public class DivideByZeroException extends RuntimeException {

    private static final long serialVersionUID =-3870343547745128405L;

     public DivideByZeroException() {
        super();
    }

     public DivideByZeroException(String arg0) {
        super(arg0);
    }
}