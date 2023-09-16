package rushhour.view;

import javafx.scene.paint.Color;

/**
 * Enum class which sets the colors of the Vehicles in the RushHour game
 * based on the String symbol assocaited with the vehicle.
 * 
 * @author Daphne
 */
public enum VehicleColor {
    // Vehicle chars per the CSV files
    R (Color.RED, "Red"), // redVehicle
    A (Color.AQUA, "Aqua"),
    B (Color.BLUE, "Blue"),
    C (Color.CHOCOLATE, "Brown"),
    D (Color.PLUM, "Plum"),
    E (Color.BLUEVIOLET, "Blue Violet"),
    F (Color.FORESTGREEN, "Forest Green"),
    G (Color.GREENYELLOW, "Green Yellow"),
    H (Color.HOTPINK, "Pink"),
    I (Color.IVORY, "Ivory"),
    O (Color.ORANGE, "Orange"),
    P (Color.PURPLE, "Purple"),
    Q (Color.YELLOW, "Yellow");


    // Attributes for the enum
    private final Color vehicleColor;
    private final String colorString;

    // Constructor
    private VehicleColor(Color color, String colorString) {
        this.vehicleColor = color;
        this.colorString = colorString;
    }

    // Accessors for enum attributes
    public Color getColor() {
        return this.vehicleColor;
    }

    public String getColorString() {
        return colorString;
    }

    // String representation of the enum
    @Override
    public String toString() {
        return String.format("(%1$s) %2$s", name(), colorString);
    }

}