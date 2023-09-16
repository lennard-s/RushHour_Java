package rushhour.view;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import rushhour.model.Direction;
import rushhour.model.Move;
import rushhour.model.RushHour;
import rushhour.model.RushHourSolver;
import rushhour.model.RushHourException;
import rushhour.model.Vehicle;


/**
 * A CLI for the game Rush Hour
 * 
 * @author Lennard
 */
public class RushHourCLI {

       public static void main(String[] args) throws IOException {
              // help menu
              String helpMenu = "Help Menu:\n\thelp - this menu\n\tsolve - attempts to solve the game for you\n\tquit - quit\n\thint - display a valid move\n\treset - reset the game\n\t<symbol> <UP|DOWN|LEFT|RIGHT> - move the vehicle one space in the given direction\n";
              // get filename from user
              Scanner scanner = new Scanner(System.in);
              System.out.print("Enter a Rush Hour filename (example: 'data/03_00.csv'):");
              String filename = scanner.nextLine().trim();
              File file = new File(filename);
              // Loop until a valid filename has been passed in
              while (!file.exists()) {
                     System.out.println("This filename is invalid.\nEnter a Rush Hour filename:");
                     filename = scanner.nextLine().trim();
                     file = new File(filename);
              }
              

              // generate RushHour board from file
              RushHour gameBoard = new RushHour(filename);

              // store gameBoard map <- lets me verify commands against values in map
              HashMap<Character, Vehicle> gameMap = gameBoard.getVehiclesOnBoard();

              System.out.println("\n---------- Welcome to Rush Hour ----------\n");
              System.out.println("Type 'help' for help menu.");
              boolean keepPlaying = true;
              while (keepPlaying != false) {
                     // inner while loop to run until game over
                     while (gameBoard.isGameOver() != true) {

                            System.out.println(gameBoard);

                            System.out.println("Moves: " + gameBoard.getMoveCount() + "\n");

                            // ASK USER FOR A COMMAND //
                            System.out.print("> ");
                            String command = scanner.nextLine().trim();
                            // loop until command is not an empty string
                            while (command == "") {
                                   System.out.print("> ");
                                   command = scanner.nextLine().trim();
                            }
                            // check if command is a menu command
                            String menuCommand = "";
                            if (command.equals("help") || 
                                command.equals("quit") || 
                                command.equals("hint") || 
                                command.equals("reset") || 
                                command.equals("solve")) 
                                {
                                   menuCommand = command;
                            }

                            // switch for menu commands 
                            boolean shouldContinue = false;
                            if (!menuCommand.equals("")) {
                                   switch (menuCommand) {
                                          case "help":
                                                 System.out.println(helpMenu);
                                                 shouldContinue = true;
                                                 break;
                                          case "quit":
                                                 System.out.println("GAME OVER");
                                                 System.exit(0);
                                          case "hint":
                                                 // return a random move from the list of possible moves
                                                 Random rand = new Random();
                                                 Collection<Move> possibleMoves = gameBoard.getPossibleMoves();
                                                 Object[] possibleMovesArray = possibleMoves.toArray();
                                                 // get a random index
                                                 int randomIndex = rand.nextInt(possibleMovesArray.length);
                                                 System.out.println("Try " + possibleMovesArray[randomIndex]);
                                                 shouldContinue = true;
                                                 break;
                                          case "reset":
                                                 System.out.println("New Game");
                                                 gameBoard = new RushHour(filename);
                                                 shouldContinue = true;
                                                 break;
                                          case "solve":
                                                 long start = System.currentTimeMillis();
                                                 RushHourSolver winningConfig;
                                                 if (RushHourSolver.solvable(gameBoard)) {
                                                        winningConfig = RushHourSolver.solve(gameBoard);
                                                 } else {
                                                        winningConfig = null;
                                                 }
                                                 long end = System.currentTimeMillis();  
                                                 
                                                 if (winningConfig != null) {
                                                        // get list of winning moves from the solve method
                                                        List<Move> winningMoves = winningConfig.getMovesMade();
                                                        // loop through each move
                                                        for (Move move : winningMoves) {
                                                               // plug each move into the CLI
                                                               try {
                                                                      // try and make the player's move
                                                                      System.out.print("\n" + "> " + move + "\n");
                                                                      gameBoard.moveVehicle(move);
                                                                      System.out.println(gameBoard);
                                                                      System.out.println("Moves: " + gameBoard.getMoveCount() + "\n");
                                                               } catch (RushHourException e) {
                                                                      System.out.println(e.getMessage());
                                                               }
                                                        }
                                                 } else {
                                                        System.out.println("No solution found");
                                                 }
                                                 System.out.println("Solution found in milliseconds: "+ (end-start));
                                                 shouldContinue = true;
                                                 break;
                                          default:
                                                 System.out.println("Invalid command: " + menuCommand + ", type 'help' for menu.\n");
                                                 shouldContinue = true;
                                                 break;
                                   }
                            }
                            if (shouldContinue) continue;
                            
                            // handle commands in the format | SYMBOL | DIRECTION |
                            // take the user's command and break it into the two components 
                            // | SYMBOL | DIRECTION |

                            String[] splitCommand = command.split(" ");
                            // check for invalid commands
                            if (splitCommand.length < 2) {
                                   System.out.println("Invalid command.  Type 'help' for menu.\n");
                                   continue;
                            }
                            Character vehicleSymbol = splitCommand[0].toUpperCase().charAt(0);

                            String moveDirection = splitCommand[1].toUpperCase();
                            // try to convert moveDirection into Direction Enum
                            Direction direction;
                            try {
                                   direction = Direction.valueOf(moveDirection);
                            } catch (IllegalArgumentException e) {
                                   System.out.println(moveDirection + " is not a valid direction\n");
                                   continue;
                            } 

                            // verify the command components match expected values
                            Move playerMove;
                            if (!gameMap.containsKey(vehicleSymbol)) {
                                   System.out.println("Invalid move: Vehicle does not exist\n");
                                   continue;
                            } else {
                                   // assign command values to move object
                                   playerMove = new Move(vehicleSymbol, direction);
                            }

                            // try and make the player's move
                            try {
                                   gameBoard.moveVehicle(playerMove);
                                   
                            } catch (RushHourException e) {
                                   System.out.println(e.getMessage());
                            }
                     }
                     // win message
                     System.out.println("\nWell done!\n");
                     String decision = "";
                     while (decision.equals("")) {
                            System.out.println("Type 'reset' to play again\nType 'quit' to end the program");
                            System.out.print("> ");
                            decision = scanner.nextLine().trim();   
                     }
                     if (decision.equals("reset")) {
                            System.out.println("\nNew Game\n");
                            gameBoard = new RushHour(filename);
                            keepPlaying = true;
                     } 
                     if (decision.equals("quit")) {
                            keepPlaying = false;     
                     }
              }
              System.out.println("Goodbye!");
              scanner.close();
       }
}