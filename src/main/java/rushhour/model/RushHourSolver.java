package rushhour.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import backtracker.Backtracker;
import backtracker.Configuration;

/**
 * This class represents a Configuration of the Rush Hour game board, to be used when attempting
 * to solve the game using the Backtracking algorithm.
 * 
 * @states board (RushHour), configurationsMade (Collection) and movesMade (List)
 * 
 * @author Emma - setup foundation code for constructors, getSuccessors, isGoal and toString
 * @refactoring completed as group effort
 */
public class RushHourSolver implements Configuration<RushHourSolver> {
    /* The current board (and its states) used for this configuration */
    protected RushHour board;
    /* A Collection of Configurations that preceed this current configuration*/
    protected Set<RushHourSolver> configurationsMade;
    /* A List of the moves made by preceeding configurations */
    protected List<Move> movesMade;
      
    
    public RushHourSolver(RushHour board, Set<RushHourSolver> configurationsMade, List<Move> movesMade) {
        this.board = board;
        this.configurationsMade = configurationsMade;
        this.movesMade = movesMade;
    }

    /** OVERLOADED Constructor for the class
     * Creates an instance of a RushHourSolver (Configuration) from a given RushHour object
     * @param board (RushHour)
     */
    public RushHourSolver (RushHour board) {
        this.board = board;
        this.configurationsMade = new HashSet<>();
        this.movesMade = new ArrayList<>();
    }

    /**
     * This method generates the Successor Configuration for the current Configuration. Each
     * successor contains one valid possible next move.
     * @refactored by Daphne - added deep copy of the movesMade list, else it was possing a legacy list of moves to each new successor
     * @refactored group effort - GOT IT WORKING!!!
     */
    @Override
    public Collection<RushHourSolver> getSuccessors() {
        HashSet<RushHourSolver> successors = new HashSet<>();
        
        for (Move possibleMove: this.board.getPossibleMoves()) {
            RushHour possible = new RushHour (this.board);
            try {
                possible.moveVehicle(possibleMove);
            } catch (RushHourException e) {
                // catch should never actually be needed 
                // because getPossilbeMoves should only provide valid moves
                continue;
            }
            
            List<Move> successorMoves = new ArrayList<>();
            successorMoves.addAll(this.movesMade);
            successorMoves.add(possibleMove);
            this.configurationsMade.add(this);
            RushHourSolver successorConfiguration = new RushHourSolver(possible, this.configurationsMade, successorMoves);
            successors.add(successorConfiguration);
        }
        return successors;
    }

    /**
     * Determines whether the current Configuration is a Valid configuration or not.
     * Valid Configurations:
     *      >> Do not have another horizontal Vehicle on the same row as the RED Vehicle
     *      >> Do not repeat a previously seen Configuration
     * @return true if valid, false otherwise
     * 
     * @author group effort - GOT IT WORKING!!!
     */
    @Override
    public boolean isValid() {
        return !configurationsMade.contains(this);
    }

    /**
     * Helper method that checks if a board is solvable, to be called in CLI & GUI prior to 
     * calling the solve() method. See line 107 in CLI for example.
     * @param board
     * @return boolean
     * 
     * @author Daphne
     * @refactored Lennard
     */
    public static boolean solvable(RushHour board) {
        // If there is another horizontal vehicle on the same row as the RED vehicle, return false
        HashMap<Character, Vehicle> checkBoard = board.getVehiclesOnBoard();
        Vehicle REDvehicle = checkBoard.get('R');
        int RedVehicleRow = REDvehicle.getBack().getRow();
        int RedVehicleFrontCol = REDvehicle.getFront().getCol();
        for (Object key : checkBoard.keySet()){
            if ((Character)key != 'R') {
                Vehicle checkVehicle = checkBoard.get(key);
                if ((checkVehicle.isHorizontal())
                     && (checkVehicle.getBack().getRow() == RedVehicleRow) 
                     && (checkVehicle.getBack().getCol() > RedVehicleFrontCol)
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines whether the current Configuration is the Goal solution
     * @return true if Goal, false otherwise
     * 
     * @author Lennard
     */
    @Override
    public boolean isGoal() {
        return board.isGameOver();
    }

    /**
     * String reqpresentation of a RushHour Configuration to include all the moves made (thus far)
     * in the configuration and the current state of the game board
     * @author Emma
     * @refactored Daphne
     */
    @Override
    public String toString () {
        String print = String.format("Moves: %1$s\n Board:\n%2$s", this.movesMade.toString(), this.board.toString());
        return print;
    }

    // Accessors for Configuration States
    public Collection<RushHourSolver> getConfigurationsMade() {
        return configurationsMade;
    }

    public List<Move> getMovesMade() {
        return movesMade;
    }
    public RushHour getBoard() {
        return this.board;
    }

    /**
     * Given a RushHour game board, returns a winning configuration, or null
     * @param game
     * @return RushHourConifguration of a winning config, or null
     * @author Lennard 
     */
    public static RushHourSolver solve(RushHour game) {
        RushHourSolver configuration = new RushHourSolver(new RushHour(game));
        Backtracker<RushHourSolver> backtracker = new Backtracker<>(false);
        RushHourSolver winningConfig = backtracker.solve(configuration);
        return winningConfig;
    }

    /**
     * Compares to two instances of RushHourConfigurations for equality based on the String representations
     * of their boards' states. (ie: checks to see if the vehicles on the boards are in the same positions.)
     * @return true or false whether the states of the boards match
     * @author group effort
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof RushHourSolver) {
            RushHourSolver other = (RushHourSolver) o;
            return this.board.toString().equals(other.getBoard().toString());
        } else {
            return false;
        }
    }

    /**
     * Generates hashCode to be used by the configurationsMade (HashSet)
     * @author Daphne
     * @refactored Lennard
     */
    @Override
    public int hashCode() {
        return this.board.toString().hashCode();
    }


}

