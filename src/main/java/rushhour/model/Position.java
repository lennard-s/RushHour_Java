package rushhour.model;

/**
 * This class represents a a row and column space on the game board.
 */
public class Position {
    private int row;
    private int col;

    
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    } 

    /**
     * Compares row and col of two position objects
     * @returns true if row and col are same
     * 
     * @author Lennard
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position other = (Position)o;
            return (this.row == other.row &&
            this.col == other.col);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[Row: " + this.row + " Col: " + this.col + "]";
    }
}
