package rushhour.view;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import backtracker.Backtracker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import rushhour.model.Direction;
import rushhour.model.Move;
import rushhour.model.RushHour;
import rushhour.model.RushHourSolver;
import rushhour.model.RushHourException;
import rushhour.model.RushHourObserver;
import rushhour.model.Vehicle;

/**
 * RushHour Group Project
 * @author Daphne built the basic structure of the GUI as well as the Solve implementation
 * @author Emma & Lennard implemented methods providing the GUI functionality & changes
 * 
 * @GUI this GUI allows users to play the RushHour game
 * 
 */
@SuppressWarnings("unused")
public class RushHourGUI extends Application implements RushHourObserver {
    private BoardLevel gameLevel;
    private int gameLevelNum;
    private String levelFilename;
    // Create RushHour Board object
    private RushHour board;

    // Set universal font size
    private int fontsize = 15;
    // Set gameboard squares min sizes
    private int squareMinSize = 110;

    // Create a Reset Button - needs to be accessed outside of the GUI to be updated
    Button resetButton;
    // Create a Solve Button - needs to be accessed outside of the GUI to be updated
    Button solveButton;
    // Create a Game Difficulty Level Button - needs to be accessed outside of the GUI to be updated
    Button gameLevelButton;
    // Create a Hint Button - needs to be accessed outside of the GUI to be updated
    Button hintButton;
    // Create a Quit Button - needs to be accessed outside of the GUI to be updated
    Button quitButton;
    // Create Game Status Label - needs to be accessed outside of the GUI to be updated
    private Label gameStatus;
    // Create MoveCount Label - needs to be accessed outside of the GUI to be updated
    private Label moveCountLabel;
    // Create the grid of nodes to be used as the game board & its Accessor
    private GridPane gameBoard;
    public GridPane getGameBoard() {return gameBoard;}
    // Tracks the Vehicle Objects created in the GUI - needs to be accessed outside of the GUI to be updated
    private HashMap<Character, Node> vehicleGUIs;

    // Image Constants to be used in Vehicle Front/Back Buttons
    public static final Image UP_ARROW = 
            new Image("file:data/upArrow.png");
    public static final Image DOWN_ARROW = 
            new Image("file:data/downArrow.png");
    public static final Image LEFT_ARROW = 
            new Image("file:data/leftArrow.png");
    public static final Image RIGHT_ARROW = 
            new Image("file:data/rightArrow.png");


    /**********************************************************************/
    /* FACTORY METHODS FOR THE FREQUENTLY USED GUI NODES */

    /** FACTORY: Creates Buttons to be used as: 
     *          -- the 'Display Hit' button to display a randomly selected valid move.
     *          -- the 'Quit' button to end the game.
     *          -- the 'Reset' button that resets the entire gameboard.
     * @param buttonLabel (String): the text to be displayed on the label
     * @param-EventHandler the event action to be initiated when button is clicked
     * 
     * @author Daphne
     */
    private Button buttonFactory(String buttonLabel, EventHandler<ActionEvent> handler) {
        Button newButton = new Button();
        newButton.setText(buttonLabel);
        newButton.setAlignment(Pos.CENTER);
        newButton.setFont(new Font("ARIAL", fontsize));
        newButton.setPadding(new Insets(5));
        newButton.setTextFill(Color.BLACK);
        newButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        //Calling EventHandler & registering the Concrete Observer with each button
        newButton.setOnAction(handler);
        board.registerObserver(this);
        return newButton;
    }

    /** FACTORY: Display Labels
     * This method creates a label to be used to display information during game play
     * @param labelString: starting text to be displayed in the label
     * @return gameboard display label (Label)
     * 
     * @author Daphne
     */
    private Label makeDisplayLabel(String labelString) {
        Label newLabel = new Label();
        newLabel.setText(labelString);
        newLabel.setAlignment(Pos.CENTER);
        newLabel.setTextAlignment(TextAlignment.CENTER);
        newLabel.setFont(new Font("Arial", fontsize));
        newLabel.setPadding(new Insets(5));
        newLabel.setTextFill(Color.BLACK);
        newLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newLabel.setBorder(new Border(new BorderStroke(Color.DARKGRAY,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                            BorderStroke.THIN)));
        newLabel.setBackground(new Background(new BackgroundFill(
                            Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        return newLabel;
    }

    /** FACTORY: Creates Labels for the gameboard GRID
     * This method creates a label to be used to fill the gameboard Grid
     * @param labelString (String): the text to be displayed on the label
     * @return gameboard grid label (Label)
     * 
     * @author Daphne
     */
    private Label makeGridLabel(String labelString) {
        Label newLabel = new Label();
        newLabel.setText(labelString);
        newLabel.setAlignment(Pos.CENTER);
        newLabel.setTextAlignment(TextAlignment.CENTER);
        newLabel.setFont(new Font("IMPACT", 24));
        newLabel.setPadding(new Insets(5));
        newLabel.setTextFill(Color.WHITE);
        newLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newLabel.setMinSize(squareMinSize, squareMinSize);
        newLabel.setBorder(new Border(new BorderStroke(Color.GRAY,
                            BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                            BorderStroke.MEDIUM)));
        newLabel.setBackground(new Background(new BackgroundFill(
                            Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        return newLabel;
    }

    /**
     * OVERLOADED LABEL FACTORY: Grid Label Factory method, creates a gamegoard
     * Grid label with no text
     * @return gameboard grid label (Label)
     * 
     * @author Daphne
     */
    private Label makeGridLabel() {
        return makeGridLabel("");
    }

    /** FACTORY: Vehicle Front Buttons
     * This method creates the button to be used as the FRONT of a vehicle. The "front" of the vehicle
     * is rounded for visual effect, and moves the vehicle either DOWN or to the RIGHT.
     * @param isHorizontal: true if vehicle is horizontal, false if vertical
     * @param vehicle (Vehicle): the vehicle object associated with the created button
     * @return Button to function as the front of the vehicle.
     * 
     * @author Daphne
     */
    private Button vehicleFrontButton(Boolean isHorizontal,Vehicle vehicle) {
        // Varibles to bet set based on vehicle's orientation
        ImageView graphic;
        CornerRadii roundedEdge;
        // Determine which orientation vehicle is in (V or H) and which arrow image to use (UP/DOWN)
        if (isHorizontal) {
            graphic = new ImageView(RIGHT_ARROW);
            roundedEdge = new CornerRadii(0.0,40.0,40.0,0.0,false);
        } else {
            graphic = new ImageView(DOWN_ARROW);
            roundedEdge = new CornerRadii(0.0,0.0,40.0,40.0,false);
        }
        // Call the helper method to create the button
        return makeVehicleButton(graphic, vehicle, roundedEdge, (e) -> {moveVehicleForwards(vehicle);});
    }

    /** FACTORY: Vehicle Back Buttons
     * This method creates the button to be used as the BACK of a vehicle. The "back" of the vehicle
     * is flat for visual effect, and moves the vehicle either UP or to the LEFT.
     * @param isHorizontal: true if vehicle is horizontal, false if vertical
     * @param vehicle (Vehicle): the vehicle object associated with the created button
     * @return Button to function as the front of the vehicle.
     * 
     * @author Daphne
     */
    private Button vehicleBackButton(Boolean isHorizontal, Vehicle vehicle) {
        // Varibles to bet set based on vehicle's orientation
        CornerRadii notRounded = CornerRadii.EMPTY;
        ImageView graphic;
        // Determine which orientation vehicle is in (V or H) and which arrow image to use (UP/DOWN)
        if (isHorizontal) {
            graphic = new ImageView(LEFT_ARROW);
        } else {
            graphic = new ImageView(UP_ARROW);
        }
        // Call the helper method to create the button
        return makeVehicleButton(graphic, vehicle, notRounded, (e) -> {moveVehicleBackwards(vehicle);});
    }

    /**
     * This HELPER method creates the button to be used as the either the front or back of a vehicle.
     * @param graphic (ImageView): the arrow image to be displayed on the button indicating the direction
     *          the button will move the vehicle in
     * @param vehicle (Vehicle): the vehicle object associated with the created button
     * @param cornerEdges (CornderRadii): the degree to which the corners on the button will be rounded, if at all
     * @param-EventHandler the event action to be initiated when button is clicked
     * @return Button to function as the front of the vehicle
     * 
     * @author Daphne
     */
    private Button makeVehicleButton(
        ImageView graphic, Vehicle vehicle, CornerRadii cornerEdges, EventHandler<ActionEvent> handler) {
        // Get the color of the vehicle
        Color vehicleColor = VehicleColor.valueOf(String.format("%c", vehicle.getSymbol())).getColor();
        // Create the button
        Button newButton = new Button("", graphic);
        newButton.setAlignment(Pos.CENTER);
        newButton.setPadding(new Insets(5));
        newButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        newButton.setMinSize(squareMinSize, squareMinSize);
        newButton.setBorder(new Border(new BorderStroke(vehicleColor,
                            BorderStrokeStyle.SOLID, cornerEdges,
                            BorderWidths.EMPTY)));
        newButton.setBackground(new Background(new BackgroundFill(
                            vehicleColor, cornerEdges, Insets.EMPTY)));
        // Calling EventHandler & registering the Concrete Observer with each button
        newButton.setOnAction(handler);
        board.registerObserver((this));
        return newButton;
    }


    /*###########################################################################################################*/
    /** CREATING THE GUI - THE THING USERS WILL INTERACT WITH */
    /** @author Daphne build the basic structure of the GUI */

    @Override
    public void start(Stage primaryStage) throws Exception {
        /* LOAD NEW GAME FILE:  */
        // Create a new game object to be used to pull game state from
        // board = new RushHour(level1Filename); //REFACTORED - ORIGINAL CODE - 1 LEVEL PLAYING

        // Game Always Starts at Level 1
        gameLevelNum = 1;
        // get the filename for the new game level
        gameLevel = BoardLevel.valueOf(String.format("L%d",gameLevelNum));
        levelFilename = gameLevel.getFilename();
        board = new RushHour(levelFilename);
        /****************************************************************/
        /* BOTTOM STATUS BAR: 'Game Status' & 'Moves Counter' Labels and Reset, Quit, Solve & Hint Buttons */
        // BUTTON: Create "Reset" Button for StatusBar
        resetButton = buttonFactory("Reset", (e) -> { 
            try {
                resetGame();
            } catch (IOException ef) {
                System.out.println(ef);
            }
        });
        // BUTTON: Create "Solve" Button for StatusBar
        solveButton = buttonFactory("Solve", (e) -> {solveGame();});
        // BUTTON: Create "Level" Button for StatusBar
        gameLevelButton = buttonFactory("Level", (e) -> { 
            try {
                levelUp();
            } catch (IOException ef) {
                System.out.println(ef);
            }
        });
        // BUTTON: Create "Display Hint" Button for StatusBar
        hintButton = buttonFactory("Display Hint", (e) -> {getHint();});
        // BUTTON: Create "Quit" Button for StatusBar
        quitButton = buttonFactory("Quit", (e) -> {quit();});

        // DISPLAY LABEL: Create "Move Count" Label for StatusBar
        int moves = board.getMoveCount();
        moveCountLabel = makeDisplayLabel(String.format("Moves: %d", moves));

        // DISPLAY LABEL:Create Label to display game/move update messages in the StatusBar
        gameStatus = makeDisplayLabel("Let's Play RushHour! (Level 1)");

        // HBOX: Container for the bottom status bar
        HBox statusBar = new HBox();
        statusBar.getChildren().addAll(resetButton, solveButton, gameLevelButton, gameStatus,
                                            moveCountLabel, hintButton, quitButton);
        HBox.setHgrow(gameStatus, Priority.ALWAYS);

        /****************************************************************/
        /** GAMEBOARD GRID */
        //Create GridPane for the entire gameBoard to be built upon
        gameBoard = new GridPane();

        //add labels rest of the grid
        int col = 6;
        int row = 6;
        // loop to add a 6x6 grid of labels to the GUI
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (i==2 && j == 5){
                    Label backgroundLabel = makeGridLabel("EXIT");
                    gameBoard.add(backgroundLabel,j, i);
                    GridPane.setHgrow(backgroundLabel, Priority.ALWAYS);
                    GridPane.setVgrow(backgroundLabel, Priority.ALWAYS);
                } else {
                    Label backgroundLabel = makeGridLabel();
                    gameBoard.add(backgroundLabel,j, i);
                    GridPane.setHgrow(backgroundLabel, Priority.ALWAYS);
                    GridPane.setVgrow(backgroundLabel, Priority.ALWAYS);
                }
            }
        }

        //Create vehicle Nodes and add them to the gameBoard
        addVehiclesToBoard();

        /****************************************************************/
        /** GAME WINDOW: PUTTING ALL THE LAYOUTS TOGETHER TO BUILD THE GAME */
        /** Using a BoarderPane to combine the components for the RushHour game window.
         * 
         * Center Pane = game board (GridPane)
         * Bottom Pane = Status bar - displays status of moves made & Reset button
         */
        BorderPane gameWindow = new BorderPane();
        gameWindow.setBottom(statusBar);
        gameWindow.setCenter(gameBoard);
        gameWindow.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID,
                            CornerRadii.EMPTY, BorderStroke.THICK)));
        gameWindow.setBackground(new Background(new BackgroundFill(
                            Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));


        /****************************************************************/
        /** DISPLAY GUI */
        primaryStage.setTitle("RushHour");
        primaryStage.setScene(new Scene(gameWindow));
        primaryStage.show();
    }

    /*#####################################################################################################*/
    /** SETUP OF METHODS NEEDED FOR EVENT HANDLING/UPDATING THE GUI */

    /**This method creates & adds the vehicles (nodes) to the gameboard (GUI) by:
     * 1) Get vehicles from the RushHour Model & loop through the vehicles
     * 2) Create buttons for the front & back of the vehicle (using factory methods for each)
     *      >> Button label contains an arrow image (up, down, left, right) based on if it is
     *         the front or back & on the vehicle's orientation (vertical/horizontal)
     *      >> Images are set as CONSTANTS with tranparent backgrounds
     *      >> Set the background color for the buttons to the color of the vehicle (VehicleColor enum)
     * 3) Create HBox or VBox to represent the vehicle based on vehicles orientation. Add both
     *      buttons to the Box. Set parameters for V/H-Grow.
     * 4) Add BOX to the grid spanning the rows/cols per the vehicles front/back positions
     *      (ex: front row/col = 2,1 & back row/col = 2,3; then BOX spans 1 row (#2) & 3 cols (#1-3).)
     * 
     * @author Daphne Neale
     */
    private void addVehiclesToBoard() {
        // Get deep copies of the vehicles on the RushHour (model) board
        Collection<Vehicle> vehicles = this.board.getVehicles();
        vehicleGUIs = new HashMap<>();
        for (Vehicle vehicle: vehicles) {
            // Get specs needed for vehicle location & creation
            int frontRow = vehicle.getFront().getRow();
            int frontCol = vehicle.getFront().getCol();
            int backRow = vehicle.getBack().getRow();
            int backCol = vehicle.getBack().getCol();
            char vehicleChar = vehicle.getSymbol();
            // True if horizontal, false if vertical
            boolean isHorizontal = vehicle.isHorizontal();
            // create front & back labels
            Button frontLabel = vehicleFrontButton(isHorizontal, board.getVehiclesOnBoard().get(vehicle.getSymbol()));
            Button backLabel = vehicleBackButton(isHorizontal, board.getVehiclesOnBoard().get(vehicle.getSymbol()));
            // Add labels to the appropriate layout
            if (isHorizontal) {
                HBox horizontalVehicle = new HBox();
                horizontalVehicle.getChildren().addAll(backLabel, frontLabel);
                HBox.setHgrow(backLabel, Priority.ALWAYS);
                HBox.setHgrow(frontLabel, Priority.ALWAYS);
                gameBoard.add(horizontalVehicle, backCol, backRow, (frontCol-backCol)+1,(frontRow-backRow)+1);
                vehicleGUIs.put(vehicleChar, horizontalVehicle);
            } else {
                VBox verticalVehicle = new VBox();
                verticalVehicle.getChildren().addAll(backLabel, frontLabel);
                VBox.setVgrow(backLabel, Priority.ALWAYS);
                VBox.setVgrow(frontLabel, Priority.ALWAYS);
                gameBoard.add(verticalVehicle, backCol, backRow, (frontCol-backCol)+1,(frontRow-backRow)+1);
                vehicleGUIs.put(vehicleChar, verticalVehicle);
            }
        }
    }

    /**
     * When the front-arrow on a vehicle is pressed, this method creates a Move object
     * for the forwards move in the direction that the vehicle is trying to move in
     * (either DOWN or RIGHT). It then calls moveVehicle() to process the movement in
     * the RushHour Model.
     * 
     * NOTE! This implementation is accessing the original copies of the vehicle
     * objects in the model. NOT deep copies. 
     * 
     * @author Lennard
     * @refactored Daphne
     */
    public void moveVehicleForwards(Vehicle vehicle) {
        // Create direction variable to capture vehicle's move direction
        Direction direction;
        
        // figure out which direction to move
        if (vehicle.isHorizontal()) {
            direction = Direction.RIGHT;
        } else {
            direction = Direction.DOWN;
        }
        // create move object to be passed into model
        Move move = new Move(vehicle.getSymbol(), direction);
        moveVehicle(move);
    }

     /**
     * When the back-arrow on a vehicle is pressed, this method creates a Move object
     * for the backwards move in the direction that the vehicle is trying to move in
     * (either UP or LEFT). It then calls moveVehicle() to process the movement in the
     * RushHour Model.
     * 
     * NOTE! This implementation is accessing the original copies of the vehicle
     * objects in the model. NOT deep copies.
     * 
     * @author Lennard
     * @refactored Daphne
     */
    public void moveVehicleBackwards(Vehicle vehicle) {
        // Create direction variable to capture vehicle's move direction
        Direction direction;
        
        // figure out which direction to move
        if (vehicle.isHorizontal()) {
            direction = Direction.LEFT;
        } else {
            direction = Direction.UP;
        }
        // create move object to be passed into model
        Move move = new Move(vehicle.getSymbol(), direction);
        moveVehicle(move);
    }

    /**
     * This HELPER method calls the moveVehicle method in the RushHour Model to initiate an
     * attempt to move the vehicle in the model. If the move is invalid, the error message
     * is caught and displayed to the user
     * @param move: Move object containing the information for the attempted vehicle move
     * 
     * @author Daphne
     */
    private void moveVehicle(Move move) {
        try {
            board.moveVehicle(move);
        } catch (RushHourException e) {
            // display any error messages in the status area
            gameStatus.setText(e.getMessage());
        }
    }

    /**
     * When the hint button is pressed, a random hint is displayed in the game status area. 
     * 
     * @author Lennard
     */
    public void getHint() {
        // return a random move from the list of possible moves
        Random rand = new Random();
        List<Move> possibleMoves = board.getPossibleMoves();
        // get a random index & random possible Move
        int randomIndex = rand.nextInt(possibleMoves.size());
        Move randomMove = possibleMoves.get(randomIndex);
        // Get information to print hint (vehicle color & direction)
        String colorSymbol = String.format("%c",randomMove.getSymbol());
        String color = VehicleColor.valueOf(colorSymbol).getColorString();
        // Character symbol = randomMove.getSymbol();
        // String color = colorMap.get(symbol);
        Direction direction = randomMove.getDir();
        // display hint on gameStatus label
        gameStatus.setText("Try " + color + " " + direction);
    }

    /**
     * This method resets the gameBoard to the configuration of the given file
     * @param levelFilename (String): the filepath name for the game configuration file
     * @throws IOException if gameboard file cannot be found/read
     * 
     * @author Emma
     * @refactored Daphne - level-up play
     */
    public boolean resetGame(String levelFilename) throws IOException {
        // Create a new, reset board
        this.board = new RushHour(levelFilename);

        // Ensure all buttons are enabled
        this.hintButton.setDisable(false);
        this.solveButton.setDisable(false);
        this.resetButton.setDisable(false);
        this.gameBoard.setDisable(false);

        // Reset the move counter & update the moveCountLabel
        this.board.resetMoveCount();
        this.moveCountLabel.setText("Moves: " + this.board.getMoveCount());

        // Remove all current vehicles from the gameBoard (GUI)
        for (Node vgui : this.vehicleGUIs.values()) {
            gameBoard.getChildren().remove(vgui);
        }

        // Add the vehicles from the reset board to the gameBoard (GUI)
        addVehiclesToBoard();

        return true;
    }

    /** OVERLOADED METHOD FOR RESET GAME
     * This method resets the gameBoard back to the original configuration for the current
     * level being played, and updates the gameStatus Label if successfully reset.
     * @throws IOException if gameboard file cannot be found/read
     * 
     * @author Daphne
     */
    public void resetGame() throws IOException {
        // Reset the board with the current game level file
        if (resetGame(this.levelFilename)) {
            // Update gameStatus label indicating game has been reset
            gameStatus.setText("Game Reset. Let's play again!");
        }
    }

    /**
     * This method loads the game board for the next level-up form the current level being
     * played, and updates the gameStatus Label if successfully loaded.
     * @param levelFilename (String): the filepath name for the game configuration file
     * @throws IOException if gameboard file cannot be found/read
     * 
     * @author Daphne
     */
    private void levelUp() throws IOException {
        //Increase the level by 1 level
        BoardLevel[] maxLevels = BoardLevel.values();
        if (gameLevelNum < maxLevels.length){
            gameLevelNum += 1;
            //get the filename for the new game level
            gameLevel = BoardLevel.valueOf(String.format("L%d",gameLevelNum));
            levelFilename = gameLevel.getFilename();
            if (resetGame(levelFilename)) {
                gameStatus.setText(String.format("Starting Level %1$d - %2$d cars. Let's play!", gameLevel.getLevel() ,gameLevel.getNumOfCars()));
            }
        } 
        // Player has reached the hightest level - cycle back to the first level
        else{
            gameLevelNum = 1;
            //get the filename for the new game level
            gameLevel = BoardLevel.valueOf(String.format("L%d",gameLevelNum));
            levelFilename = gameLevel.getFilename();
            if (resetGame(levelFilename)) {
                gameStatus.setText(String.format("Return to Level %1$d - %2$d cars. Let's play!", gameLevel.getLevel() ,gameLevel.getNumOfCars()));
            }
        }

    }

    /**
     * This method acts as the Concrete Observer for the RushHour (model) class. When
     * an update occurs the vehicles on the 'board' in the RushHour class, this method
     * is called in order to update the state of the GUI.
     * @ConcreteObserver updates board after a vehicle has been moved in the model
     * @param vehicle: Vehicle object that has been moved on the board
     * 
     * @author Emma
     * @author Daphne - added remove & add vehiclGUI to board in new location
     */
    @Override
    public void vehicleMoved(Vehicle vehicle) {
        // Get char symbol for vehicle that moved
        Character vehicleSymbol = vehicle.getSymbol();
        // Get the vehicleGUI (object) that represents the vehicle
        Node vehicleGUI = vehicleGUIs.get(vehicleSymbol);
        // remove vehicleGUI from gameBoard (GridPane)
        gameBoard.getChildren().remove(vehicleGUI);

        /* Following the same process as when the vehicles were added to the GUI
         * (roughly lines 338-370), obtain the new position information for the 
         * vehicle that moved & add the vehicleGUI back to the board.
         * 
         * Location is set by vehicle's Back Position (col, row, spanning cols, spanning rows)
         */
        // Get the new row & col for BOTH the back & front of vehicle (Replicated lines 340-343)
        int frontRow = vehicle.getFront().getRow();
        int frontCol = vehicle.getFront().getCol();
        int backRow = vehicle.getBack().getRow();
        int backCol = vehicle.getBack().getCol();
        // add vehicleGUI back to the gameBoard at the new location 
        gameBoard.add(vehicleGUI, backCol, backRow, (frontCol-backCol)+1,(frontRow-backRow)+1);


        // Update moveCountLabel with the new move count
        int moveCount = board.getMoveCount();
        String messageUpdate = String.format("Moves: %d", moveCount);
        moveCountLabel.setText(messageUpdate);

        if (this.board.isGameOver()) {
            this.gameStatus.setText("You won, great job!");
            System.out.println("game won"); // added as a check from CLI that move completed
            this.gameBoard.setDisable(true);
            this.hintButton.setDisable(true);
        } else {
            System.out.println("Great move!");  // added as a check from CLI that move completed
            this.gameStatus.setText("Great move!");
        }
    }

    /**
     * This method disables all the buttons on the gameWindow
     * @Author Daphne
     */
    private void quit() {
        this.gameBoard.setDisable(true);
        this.resetButton.setDisable(true);
        this.solveButton.setDisable(true);
        this.gameLevelButton.setDisable(true);
        this.hintButton.setDisable(true);
        this.quitButton.setDisable(true);
        this.gameStatus.setText("Goodbye!");
    }

    /**
     * This method attempts to solve the RushHour game from the current board state.
     *   >> If a solution is found, the board updates the vehicles' moves one-by-one until
     *      the game is solved, and the gameStatus Label is updated indicating a solution found.
     *   >> If no solution is found, the gameStatus Label is updated indicating no solution found.
     * 
     * @author Daphne
     */
    private void solveGame() {
        // Initiate Backtracking to solve the game
        RushHourSolver solutionConfig;
        //Check to see if the current board is solvable before attempting to solve the board
        if (RushHourSolver.solvable(this.board)) {
            solutionConfig = RushHourSolver.solve(this.board);
        } else {
            solutionConfig = null;
        }
        // If configuration == null, display loser message in the StatusLabel
        if (solutionConfig == null) {
            this.gameStatus.setText("No solution found. Try again.");
        } else {
            // else (solution is not null), update the board with the solution configuration
            // Get the list of moves and work through/display each move until solved
            List<Move> winningMoves = solutionConfig.getMovesMade();
            // Use a Thread to make and display the moves to solve the game, one-by-one
            new Thread(() -> {
                // for each move
                for (Move move : winningMoves) {
                    System.out.println("platform.runLater"); // added for testing
                    Platform.runLater(() -> {
                            // make move
                            System.out.println(move); // added for testing
                            this.moveVehicle(move);
                            System.out.println("thread"); // added for testing
                            // Update the StatusLabel to indicate a valid solution found
                            this.gameStatus.setText("Solution found! (Moves: "+ solutionConfig.getBoard().getMoveCount()+")");
                    });
                    // sleep small amount time (~250ms)
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // nothing needs to be done
                    }
                }
            }).start();
        }
        // end gameBoard should be solved
    }


    public static void main(String[] args) {
        launch(args);
    }
}
