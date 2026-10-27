import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Board {
    public static final int SIZE = 10;

    private final Ship[][] ships = new Ship[SIZE][SIZE];
    private final boolean[][] shots = new boolean[SIZE][SIZE];
    private final List<Ship> fleet = new ArrayList<>();

    public boolean placeShip(ShipType type, Coordinate start, boolean horizontal) {
        int endRow = start.getRow() + (horizontal ? 0 : type.getLength() - 1);
        int endColumn = start.getColumn() + (horizontal ? type.getLength() - 1 : 0);
        if (!isInBounds(start.getRow(), start.getColumn())
                || !isInBounds(endRow, endColumn)) {
            return false;
        }

        for (int offset = 0; offset < type.getLength(); offset++) {
            int row = start.getRow() + (horizontal ? 0 : offset);
            int column = start.getColumn() + (horizontal ? offset : 0);
            if (ships[row][column] != null) {
                return false;
            }
        }

        Ship ship = new Ship(type, start, horizontal);
        for (int offset = 0; offset < type.getLength(); offset++) {
            int row = start.getRow() + (horizontal ? 0 : offset);
            int column = start.getColumn() + (horizontal ? offset : 0);
            ships[row][column] = ship;
        }
        fleet.add(ship);
        return true;
    }

    public ShotOutcome receiveShot(Coordinate coordinate) {
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        if (!isInBounds(row, column)) {
            throw new IllegalArgumentException("Shot is outside the board.");
        }
        if (shots[row][column]) {
            throw new IllegalArgumentException("That position has already been targeted.");
        }
        shots[row][column] = true;

        Ship ship = ships[row][column];
        if (ship == null) {
            return ShotOutcome.MISS;
        }
        ship.registerHit(coordinate);
        return ship.isSunk() ? ShotOutcome.SUNK : ShotOutcome.HIT;
    }

    public boolean wasShotAt(Coordinate coordinate) {
        return shots[coordinate.getRow()][coordinate.getColumn()];
    }

    public Ship getShipAt(Coordinate coordinate) {
        return ships[coordinate.getRow()][coordinate.getColumn()];
    }

    public boolean hasRemainingShips() {
        for (Ship ship : fleet) {
            if (!ship.isSunk()) {
                return true;
            }
        }
        return false;
    }

    public List<Coordinate> getUntargetedCoordinates() {
        List<Coordinate> coordinates = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (!shots[row][column]) {
                    coordinates.add(new Coordinate(row, column));
                }
            }
        }
        return Collections.unmodifiableList(coordinates);
    }

    private boolean isInBounds(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }
}
