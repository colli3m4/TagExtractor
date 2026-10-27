import java.util.HashSet;
import java.util.Set;

public final class Ship {
    private final ShipType type;
    private final Set<Coordinate> locations;
    private final Set<Coordinate> hits = new HashSet<>();

    public Ship(ShipType type, Coordinate start, boolean horizontal) {
        this.type = type;
        this.locations = new HashSet<>();
        for (int offset = 0; offset < type.getLength(); offset++) {
            int row = start.getRow() + (horizontal ? 0 : offset);
            int column = start.getColumn() + (horizontal ? offset : 0);
            locations.add(new Coordinate(row, column));
        }
    }

    public ShipType getType() {
        return type;
    }

    public boolean occupies(Coordinate coordinate) {
        return locations.contains(coordinate);
    }

    public boolean isSunk() {
        return hits.size() == locations.size();
    }

    public void registerHit(Coordinate coordinate) {
        if (occupies(coordinate)) {
            hits.add(coordinate);
        }
    }
}
