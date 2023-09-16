# RushHour_Java
A CLI and GUI implementation of the Rush Hour puzzle game. Completed with two group members, Daphne and Emma. Code contributions are noted in the docstrings. 

This project was an excellent opportunity to learn about the challenges of completing a coding project with team members. 

Notable features of this project include: 
- GUI interface (\src\main\java\rushhour\view\RushHourGUI.java)
- CLI interface (\src\main\java\rushhour\view\RushHourCLI.java)
- Backtracking algorithm used for the "solve" button in the GUI, and "solve" command in the CLI 
- test code for all methods!
- adherence to the "Model View Controller" design pattern
- detailed docstrings for each method!
- highly detailed comments for all of the code

The CLI implementation of the Rush Hour game is one of the areas where I took ownership. 
Learning about all the various ways a user might input faulty commands, and creating a safe environment for the user to play were challenges I thoroughly enjoyed navigating. 
It was also quite rewarding to go through all of our code towards the end of the project, searching for any ways we might refactor our solutions to be more efficient. 

One of the biggest takeaways I have from this experience, is the importance of being absolutely meticulous with my code when I am writing the simpler, foundational code. 
We ended up running into a bug where moving a vehicle to the RIGHT would cause portions of the vehicle to disappear. 
I spent hours combing through the code to find the bug, only to discover that one of the first methods we wrote (rushhour.model.Vehicle.move(Direction dir)), had the classic error of (x,x) when we wanted (x,y).
A simple mistake that ended up costing hours! The silver lining of this bug-hunt was that I ended up discovering numerous other mistakes that might have gone unnoticed had I not combed through every line of code in the project, haha!

Thank you for taking a look at this project.
A big thank you to Emma and Daphne for their contributions as team members!

Lennard Szyperski
