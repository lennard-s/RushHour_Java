package rushhour.model;

/**
 * This custom exception is created for the RushHour game. It provides a
 * custom message which is given when the exception is thrown.
 * 
 * @Tested - with RushHour (class) invalid moves tests
 */
public class RushHourException extends Exception {
    /**
     * Constructor for when a RushHourException is thrown
     * @param message (String) - custom message to be displayed when
     * the exception is thrown
     */
    public RushHourException(String message) {
        super(message);
    }
    
}
