package rushhour.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rushhour.view.VehicleColor;

/**
 * This class represents the Rush Hour game. The object of the game is to try to get the
 * red vehicle out the one-and-only exit by moving around the vehicles on the board.
 * 
 * Vehicles are aligned either horizontally or vertically and can only move in the directions
 * they are oriented in. (ie: vertical = up/down movements, horizontal = right/left movements)
 * 
 * @ModelClass the main class that implements the game rules and maintains the game state.
 * @FinalStaticStates BOARD_DIM (int), RED_SYMBOL (char), EMPTY_SYMBOL (char), EXIT_POS (Position)
 * 
 * @author Daphne
 * @author Lennard
 * @author Emma
 */

public class RushHour {
    /* Per UML: Static & Final states used for the Rush Hour game */
    // Holds the board dimensions (ie: 6rows x 6cols)
    public static final int BOARD_DIM = 6;
    // Holds the symbol identifier for the RED VEHICLE
    public static final char RED_SYMBOL = 'R';
    // Holds the symbol to identify an EMPTY SPACE on the board
    public static final char EMPTY_SYMBOL = '-';
    // Hold the coordinates for the ONLY EXIT SPACE on the board
    public static final Position EXIT_POS = new Position(2,5);

    /* Added States while building class */
    // A counter that holds the number of moves made in the game
    private int moveCount;
    // Holds the data structure (2D Array) for the game board
    private Object[][] gameBoard;
    // Holds the data structure (Map) for Vehicles created on the board & their locations
    private HashMap<Character, Vehicle> vehiclesOnBoard;

    // Observer for the Javafx GUI
    private RushHourObserver observer;


    /** Constructor for a RushHour object (ie: game board)
     * Parses a CSV file to construct a RushHour object (2D Array game board) with vehciles
     *  located in various positions & location per the patter in the given CSV file.
     * @param filename (String) for the CSV game board file
     * @throws IOException
     * 
     * @author Lennard
     * @Tested
     */
    public RushHour(String filename) throws IOException {
        // create 2D Array 'board' - 6x6 grid
        gameBoard = new Object[6][6];

        // create map to store vehicle objects & thier locations
        vehiclesOnBoard = new HashMap<>();

        // set up file IO
        FileReader fileReader = new FileReader(filename);
        BufferedReader reader = new BufferedReader(fileReader);

        // loop through lines in file
        String line;
        while ((line = reader.readLine()) != null) {
            // >>line format: vehicle_symbol,back_row,back_col,front_row,front_col<<
             
            // tokenize the line on ','
            String[] lineBundle = line.split(",");
            // tokens[0] = vehicle symbol <- use token[0].charAt(0) to cast to char
            Character vehicleSymbol = lineBundle[0].charAt(0);
            // tokens[1] = back_row <- use Integer.valueOf(tokens[1])
            int backRow = Integer.valueOf(lineBundle[1]);
            // tokens[2] = back_col
            int backCol = Integer.valueOf(lineBundle[2]);
            // tokens[3] = front_row
            int frontRow = Integer.valueOf(lineBundle[3]);
            // tokens[4] = front_col
            int frontCol = Integer.valueOf(lineBundle[4]);

            // Create the front & back Position objects
            Position frontPosition = new Position(frontRow, frontCol);
            Position backPosition = new Position(backRow, backCol);
            // create vehicle object for each line: symbol, back Position, front Position
            Vehicle vehicle = new Vehicle(vehicleSymbol, backPosition, frontPosition);
            // add Vehicle to Map (char/Vehicle object pair)
            vehiclesOnBoard.put(vehicleSymbol, vehicle);
        }
        // add vehicle symbols (chars) to appropriate location on the board
        updateBoard(vehiclesOnBoard);

        reader.close();
        // END: All vehicles are created, map contain Vehicle informaion, 
        // 2D Array has vehicle chars & null if empty space
    }


    /**
     * Deep copy constructor for RushHourConfiguration
     * @param previous
     * 
     * @author Emma
     * @refactored by Daphne - wasn't making deep copies of the objects
     * @Tested
     */
    public RushHour (RushHour previous) {
        this.gameBoard = Arrays.copyOf(previous.gameBoard, 6);
        this.moveCount = previous.moveCount;
        this.vehiclesOnBoard = new HashMap<Character, Vehicle>();
        for (Character key : previous.getVehiclesOnBoard().keySet()) {
            Vehicle addVehicle = new Vehicle(previous.getVehiclesOnBoard().get(key));
            vehiclesOnBoard.put(key, addVehicle);
        }
    }

    /**
     * Helper method to update the 2D 'board' any time a vehicle is moved
     * @param vehicleMap (HashMap)
     * 
     * @author Lennard
     * @Tested
     */
    public void updateBoard(HashMap<Character, Vehicle> vehicleMap) {
        // WIPE THE MAP >:]
        gameBoard = new Object[6][6];
        
        // iterate over each entry in the vehicleMap
        for (Map.Entry<Character, Vehicle> entry : vehicleMap.entrySet()) {
            // pull out the vehicle from the map
            Vehicle vehicle = entry.getValue();
            // populate front and back position
            gameBoard[vehicle.getFront().getRow()][vehicle.getFront().getCol()] = vehicle.getSymbol();
            gameBoard[vehicle.getBack().getRow()][vehicle.getBack().getCol()] = vehicle.getSymbol();
            
            if (vehicle.isVertical()) { 
                // if vehicle longer than 2
                if (vehicle.getBack().getRow() < vehicle.getFront().getRow()-1) {
                    // populate front row position - 1
                    gameBoard[vehicle.getFront().getRow()-1][vehicle.getFront().getCol()] = vehicle.getSymbol();
                }    
            } else { // vehicle is Horizontal:
                // if vehicle longer than 2
                if (vehicle.getBack().getCol() < vehicle.getFront().getCol()-1) {
                    // populate front col position - 1 
                    gameBoard[vehicle.getFront().getRow()][vehicle.getFront().getCol()-1] = vehicle.getSymbol();
                } 
            }
        }
    }


    /** This class attempts to move a vehicle 1-space based on the given Move information.
     * @MoveValidity:
     *      >> An invalid vehicle symbol was given
     *      >> The vehicle would move off the game board
     *      >> Another vehicle is already in the new Position
     *      >> A horizontal vehicle can move only left or right (checked for in Vehicle class)
     *      >> A vertical vehicle can move only up or down (checked for in Vehicle class)
     * Note: If Vehicle orientation is Vertical (up/down) = facing down; Horizontal (L/R) = facing right
     * @param move (Move object) which provides the moving vehicle's identifier (char) and Direction (enum)
     * @throws RushHourException if the given Move is invalid
     * 
     * @author Daphne
     * @Tested
     */
    public void moveVehicle(Move move) throws RushHourException {
        // Pull out from move: vehicle char & Direction
        char vehicleSymbol = move.getSymbol();
        Direction moveDirection = move.getDir();

        // Get String for Vehicle Color to be used when printing exception messages
        String vehicleColorString = VehicleColor.valueOf(String.format("%c",vehicleSymbol)).toString();

        // Call Vehicle object from map with vehicle char
        Vehicle vehicleToMove = vehiclesOnBoard.get(vehicleSymbol);
        if (vehicleToMove == null) {
            // Vehicle Symbol (char) not in map >> THROW EXCEPTION - "vehicle not on board"
            throw new RushHourException("Invalid move: Vehicle " + vehicleColorString + " is not on the board.\n");
        }

        // Check to see if the vehicle is in the correct orientation for the given moveDirection
        // If Vehicle orientation is Vertical (up/down) = facing down; Horizontal (L/R) = facing right
        if ((moveDirection == Direction.UP || moveDirection == Direction.DOWN) && vehicleToMove.isHorizontal()) {
            // Invalid Orientation >> THROW EXCEPTION - "vehicle is horizontal and cannot move up/down"
            throw new RushHourException("Invalid move: Vehicle is horizontal and cannot move " + moveDirection + ".\n");
        }
        else if ((moveDirection == Direction.RIGHT || moveDirection == Direction.LEFT) && vehicleToMove.isVertical()) {
            // Invalid Orientation >> THROW EXCEPTION - "vehicle is horizontal and cannot move up/down"
            throw new RushHourException("Invalid move: Vehicle is vertical and cannot move " + moveDirection + ".\n");
        }

        /*Get Coordinates for the potential move based on the Direction provided
            If Up >> to evaluate the space behind the vehicle
            If Down >> to evaluate the space in front of the vehicle
            If Left >> to evaluate the space behind the vehicle
            If Right >> to evaluate the space in front of the vehicle*/
        Position futurePosition = null;
        switch (moveDirection) {
            case UP:
                futurePosition = vehicleToMove.spaceInBack();
                break;
            case DOWN:
                futurePosition = vehicleToMove.spaceInFront();
                break;
            case LEFT:
                futurePosition = vehicleToMove.spaceInBack();
                break;
            case RIGHT:
                futurePosition = vehicleToMove.spaceInFront();
                break;
        }
        int newRow = futurePosition.getRow();
        int newCol = futurePosition.getCol();

        // Check if vehicle will move off the board (ie: if either newRow or newCol is outside of the to 0-5 indexes)
        if (newRow < 0 || 5 < newRow || newCol < 0 || 5 < newCol) {
            // Invalid index >> THROW EXCEPTION - "vehicle is at the edge of the board"
            throw new RushHourException("Invalid move: Vehicle " + vehicleColorString + " is at the edge of the board.\n");
        }

        // Check for empty space: get value of space on board, empty = null
        Object moveSpace = gameBoard[newRow][newCol];
        if (moveSpace != null) {
            // Space is not empty (ie: vehicle is there) >> THROW EXCEPTION - "there is a vehicle alredy there"
            // Get String for Vehicle that is in the way, to be used when printing exception messages
            String obstructingVehicle = VehicleColor.valueOf(String.format("%c", moveSpace)).toString();
            throw new RushHourException("Invalid move: Another vehicle, "+ obstructingVehicle +", is in the way!\n");
        }

        // Preliminary checks are OK >> call Vehicle.move() for final check & to move vehcile
        // Vehicle.move() checks if the vehicle is the correct orientation & makes move if valid
        vehicleToMove.move(moveDirection);
        // Check vehicle's position, again, to see if it matches the calculated futurePosition (if equal, move made)
        if (vehicleToMove.getBack().equals(futurePosition) || vehicleToMove.getFront().equals(futurePosition)) {
            // Move was completed, increment the moveCount
            this.moveCount += 1;
            updateBoard(vehiclesOnBoard);

            // DEBUG FIX: can't call observer if it is null (ie: playing from CLI only)
            if (observer != null) {
                // Notify Observer that the vehicle moved
                notifyObserver(vehicleToMove);
            }
        } else {
            throw new RushHourException("Invalid move: move logic failed");
        }
    }


    /**
     * This class identifies if the game is over or not. The game is over when the front of the
     * RED VEHICLE occupies the EXIT_POSITION.
     * @return true if RED VEHICLE is at the EXIT_POSITION, and false otherwise
     * 
     * @author Daphne
     * @Tested
     */
    public boolean isGameOver() {
        // If not EMPTY_SYMBOL, get the vehicle located at the EXIT_POS of the game board
        if (gameBoard[2][5] != null) { 
            char exitSpaceVehicle = (char)gameBoard[2][5];
            // If symbol in the space is 'R' = the RED vehicle should be in that space
            if (exitSpaceVehicle == RED_SYMBOL) {
                Vehicle exitVehicle = vehiclesOnBoard.get(exitSpaceVehicle);
                // Confirm the RED vehicle's FRONT is at the EXIT_POSITION, return true
                return (exitVehicle.getFront().equals(EXIT_POS));
            } else {
                // Vehicle at the exit space is NOT the RED vehicle - game is not over return false
                return false;
            }
        } else {
            // EXIT_POS is empty - game is not over return false
            return false;
        }
    }


    /**
     * This method provides the player with valid moves that any vehicle can make at the time called.
     * @return possibleMoves (Collection<Move>) a collection of valid moves that can be made
     * 
     * @author Daphne
     * //Emma to complete - Daphne Completed
     * @Tested
     */
    public List<Move> getPossibleMoves() {
        // create a possibleMoves Map<Character , Collection<Move>> (vehicle symbol as key, list of viable Move objects as value)
        List<Move> possibleMoves = new LinkedList<>();

        // go through the vehicles Map, get all the coordinates that are in front/after each vehicle
        for (Object vehicleChar : vehiclesOnBoard.keySet().toArray()) {
            // Pull vehilce from map
            Vehicle vehicle = vehiclesOnBoard.get(vehicleChar);

            // Check space behind vehicle: off the edge? or is the space behind null?
            Position spaceBehind = vehicle.spaceInBack();
            if ((0 <= spaceBehind.getRow() && spaceBehind.getRow() < 6)
                && (0 <= spaceBehind.getCol() && spaceBehind.getCol() < 6)
                && gameBoard[spaceBehind.getRow()][spaceBehind.getCol()] == null) {
                // Found a valid move. Check vehicle's orientation and add the Move to the moveList
                if (vehicle.isHorizontal()) {
                    // Vehicle is Horizontal a backwards move = to LEFT
                    Move validMoveBack = new Move((char)vehicleChar, Direction.LEFT);
                    possibleMoves.add(validMoveBack);
                } else {
                    // Vehicle is Vertical a backwards move = to UP
                    Move validMoveBack = new Move((char)vehicleChar, Direction.UP);
                    possibleMoves.add(validMoveBack);
                }
            }
            // Check space in front of vehicle: off the edge? or is the space behind null?
            Position spaceInFront = vehicle.spaceInFront();
            if ((0 <= spaceInFront.getRow() && spaceInFront.getRow() < 6)
                && (0 <= spaceInFront.getCol() && spaceInFront.getCol() < 6)
                && gameBoard[spaceInFront.getRow()][spaceInFront.getCol()] == null) {
                // Found a valid move. Check vehicle's orientation and add the Move to the moveList
                if (vehicle.isHorizontal()) {
                    // Vehicle is Horizontal a forwards move = to RIGHT
                    Move validMoveForward = new Move((char)vehicleChar, Direction.RIGHT);
                    possibleMoves.add(validMoveForward);
                } else {
                    // Vehicle is Vertical a backwards move = to DOWN
                    Move validMoveForward = new Move((char)vehicleChar, Direction.DOWN);
                    possibleMoves.add(validMoveForward);
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Accessor for moveCount field
     * @return moveCount (integer) the number of moves for this game thus far
     * @Tested
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Accessor for vehiclesOnBoard field
     * @return map of vehiclesOnBoard
     * @Tested
     */
    public HashMap<Character, Vehicle> getVehiclesOnBoard() {
        return this.vehiclesOnBoard;
    }

    /**
     * Accessor for gameBoard 2D Array
     * @return Object[][]
     * @Tested
     */
    public Object[][] getGameBoard() {
        return this.gameBoard;
    }

    /**
     * This method assigns the Concrete Observer to process changes to be made
     * to the GUI in the MVC Design model.
     * @param observer: Observer interface that observes the game board
     * 
     * @author Emma
     * @NotTesting
     */
    public void registerObserver (RushHourObserver observer) {
        this.observer = observer;
    }

    /**
     * This method calls the Concrete Observer, and initiates the processing of
     * changes made in the Model to be reflected in the GUI.
     * @param vehicle: Vehicle that was moved on the game board
     * 
     * @author Emma
     * @NotTesting
     */
    private void notifyObserver (Vehicle vehicle) {
        this.observer.vehicleMoved(new Vehicle(vehicle));
    }

    /**
     * This method creates a deep copy of all the vehilces currently on the gameboard
     * @return vehicles: Collection of copies of the vehicles on the board
     * 
     * @author Emma
     * //TODO Emma to test
     */
    public Collection<Vehicle> getVehicles () {
        HashSet<Vehicle> vehicles = new HashSet<>();
        for (Map.Entry<Character, Vehicle> entry : this.vehiclesOnBoard.entrySet()) {
            vehicles.add(new Vehicle(entry.getValue()));
        }
        return vehicles;
    }

    /**
     * This method set the moveCount state to 0 (Used in the RushHourGUI)
     * 
     * @author Emma
     * //TODO Emma to test
     */
    public void resetMoveCount () {
        this.moveCount = 0;
    }
    
    /**
     * Provides a string representation of the RushHour game board
     * @return string representation of 2D array 'board'
     * 
     * @author Lennard
     * @Tested
     */
    @Override
    public String toString() {
        // Each character in the board should be RushHour.EMPTY_SPACE 
        // or an alphabetic letter representing the symbol of a vehicle, including the RED_SYMBOL.

        String boardRepr = "";
        int rowTracker = 0;
        // loop through rows
        for (Object[] row : gameBoard) {
            // loop through cells in row
            for (Object cell : row) {
                if (cell == null) {
                    boardRepr += EMPTY_SYMBOL;
                } else {
                    boardRepr += String.valueOf(cell);
                }
            }
            if (rowTracker == 2) {
                boardRepr += "< EXIT";
            }
            // start new line after every row
            boardRepr += "\n";
            rowTracker ++;
        }
        return boardRepr;
    }

    /**
     * Compares two RushHour objects to see if they are equal based on the
     * String representations of each object
     * 
     * @Author Lennard
     * @refactored by Daphne - to compare strings
     * @Tested
     */
    @Override
    public boolean equals (Object o) {
        if (o instanceof RushHour) {
            RushHour other = (RushHour) o;
            return this.toString().equals(other.toString());
        }
        return false;
    }

}
