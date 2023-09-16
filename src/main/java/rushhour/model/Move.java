package rushhour.model;

/** 
 * This class specifies which vehicle (by symbol) is moving and in what direction.
 * All moves are one space.
 */

public class Move {
    // Holds the symbol for the vehicle being moved
    private char symbol;
    // holds which direction the vehicle is moving in
    private Direction dir;

    // Constructor for class
    public Move(char symbol, Direction dir) {
        this.symbol = symbol;
        this.dir = dir;
    }


    // Accessors for Fields
    public char getSymbol() {
        return symbol;
    }

    public Direction getDir() {
        return dir;
    }

    /**
     * A method to get the string representation of a move object showing symbol - 
     * and direction.
     * @return a string representation of a move object 
     */
    @Override
    public String toString() {
        return "[Vehicle: " + symbol + ", Direction: " + dir + "]";
    }
}
