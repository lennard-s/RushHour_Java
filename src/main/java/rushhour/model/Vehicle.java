package rushhour.model;

/**
 * This class represents a Vehicle on the Rush Hour game board.
 * @states symbol (char), back (Position) and front (Position)
 * 
 * @author Daphne
 */
public class Vehicle {
    //holds the symbol identifier for the specific Vehicle object
    private char symbol;
    //holds the location of the BACK of the vehicle on the board
    private Position back;
    //holds the location of the FRONT of the vehicle on the board
    private Position front;


     /**
     * Constructor for class
     * @param vehicle symbol (char)
     * @param vehicle's back (Position)
     * @param vehicle's front (Position)
     * 
     * @Tested
     */
    public Vehicle(char symbol, Position back, Position front) {
        this.symbol = symbol;
        this.back = back;
        this.front = front;
    }

    /** Copy constructor for class
     * This method initializes a new vehicle from an instance of another (copies)
     * @param other: Vehicle to be copied
     * 
     * @refactored Daphne - wasn't making actual copies of the objects
     * @Tested
     */
    public Vehicle(Vehicle other) {
        this.symbol = other.getSymbol();
        this.back = new Position(other.getBack().getRow(),other.getBack().getCol());
        this.front = new Position(other.getFront().getRow(), other.getFront().getCol());
    }

    /**
     * This method moves a vehicle 1-space in the given direction, if valid.
     * @MoveValidity:
     *      >> A horizontal vehicle can move only left or right.
     *      >> A vertical vehicle can move only up or down.
     *      >> Another vehicle is not already in the new Position (per Rush Hour class)
     *      >> The vehicle would move off the grid (per Rush Hour class)
     *      >> An invalid vehicle symbol was given (per Rush Hour class)
     * @param dir (Direction) - direction of the attempted move
     * @throws RushHourException
     * 
     * @author Daphne
     * @Tested
     */
    public void move (Direction dir) throws RushHourException {
        // Check for move validity in rush hour main class
        // Move the vehicle 1-space
        switch (dir) {
            case UP:
                    this.back = new Position(this.back.getRow() - 1, this.back.getCol());
                    this.front = new Position(this.front.getRow() - 1, this.front.getCol());
                break;
            case DOWN:
                    this.back = new Position(this.back.getRow() + 1, this.back.getCol());
                    this.front = new Position(this.front.getRow() + 1, this.front.getCol());
                break;
            case RIGHT:
                    this.back = new Position(this.back.getRow(), this.back.getCol() + 1);
                    this.front = new Position(this.front.getRow(), this.front.getCol() + 1);
                break;
            case LEFT:
                    this.back = new Position(this.back.getRow(), this.back.getCol() - 1);
                    this.front = new Position(this.front.getRow(), this.front.getCol() - 1);
        }
    }

    /** Identifies whether a Vehicle is Horizontal on the board or not. A Vehicle is
     * horizontal if its back (back Position) is closer to the left side of the board.
     * @return true if horizontal, and false otherwise
     * 
     * @author Daphne
     * @Tested
     */
    public boolean isHorizontal() {
        return (this.back.getRow() == this.front.getRow() && this.back.getCol() < this.front.getCol());
    }

    /** Identifies whether a Vehicle is Vertical on the board or not. A Vehicle is
     * vertical if its back (back Position) is closer to the top of the board.
     * @return true if vertical, and false otherwise
     * 
     * @author Daphne
     * @Tested
     */
    public boolean isVertical() {
        return (this.back.getRow() < this.front.getRow() && this.back.getCol() == this.front.getCol());
    }

    /**
     * Returns the potential space in front of the vehicle
     * @return Position with coordiantes for the space in front of the vehicle
     * 
     * @author Daphne
     * @Tested
     */
    public Position spaceInFront() {
        Position spaceInFront;
        if (isHorizontal()){
            //vehicle is facing Right - space in front = (row, col+1)
            spaceInFront = new Position(this.front.getRow(), this.front.getCol()+1);
        }
        else {
            //vehicle Vertical, facing Down - space in front = (row+1, col)
            spaceInFront = new Position(this.front.getRow()+1, this.front.getCol());
        }
        return spaceInFront;
    }

    /**
     * Returns the potential space in back of (behind) the vehicle
     * @return Position with coordiantes for the space in back of the vehicle
     * 
     * @author Daphne
     * @Tested
     */
    public Position spaceInBack() {
        Position spaceInBack;
        if (isHorizontal()){
            // vehicle is facing Right - space in back = (row, col-1)
            spaceInBack = new Position(this.back.getRow(), this.back.getCol()-1);
        }
        else {
            // vehicle Vertical, facing Down - space in bacl = (row-1, col)
            spaceInBack = new Position(this.back.getRow()-1, this.back.getCol());
        }
        return spaceInBack;
    }

    // Accessors for class fields
    /**@Tested with constructor tests */
    public char getSymbol() {
        return symbol;
    }
    public Position getBack() {
        return back;
    }
    public Position getFront() {
        return front;
    }

    /**
     * Returns a string representation of a vehicle object
     * @return string representation of vehicle object
     * 
     * @author Lennard
     * @Tested
     */
    @Override
    public String toString() {
        return "[Vehicle: " + this.symbol + "]";
    }
}
