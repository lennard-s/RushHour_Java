package rushhour.model;

/**
 * Enum class of different direction types for vehicles to move in.
 * A horizontal vehicle can move only left or right.
 * A vertical vehicle can move only up or down.
 * 
 * @author Lennard
 */
public enum Direction {
    UP("UP"),
    RIGHT("RIGHT"),
    DOWN("DOWN"),
    LEFT("LEFT");

    private String name;

    private Direction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name; 
    }
}
