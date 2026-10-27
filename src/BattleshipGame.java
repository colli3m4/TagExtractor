import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class BattleshipGame {
    private final Random random = new Random();
    private Board playerBoard;
    private Board opponentBoard;
    private boolean gameOver;
    private boolean playerWon;

    public BattleshipGame() {
        newGame();
    }

    public void newGame() {
        playerBoard = createRandomBoard();
        opponentBoard = createRandomBoard();
        gameOver = false;
        playerWon = false;
    }

    public GameTurnResult fireAt(Coordinate target) {
        if (gameOver) {
            throw new IllegalStateException("The game is over. Start a new game to play again.");
        }

        ShotOutcome playerOutcome = opponentBoard.receiveShot(target);
        if (!opponentBoard.hasRemainingShips()) {
            gameOver = true;
            playerWon = true;
            return new GameTurnResult(target, playerOutcome, null, null, true, true);
        }

        List<Coordinate> available = new ArrayList<>(playerBoard.getUntargetedCoordinates());
        Collections.shuffle(available, random);
        Coordinate computerTarget = available.get(0);
        ShotOutcome computerOutcome = playerBoard.receiveShot(computerTarget);
        if (!playerBoard.hasRemainingShips()) {
            gameOver = true;
            playerWon = false;
        }

        return new GameTurnResult(target, playerOutcome, computerTarget, computerOutcome,
                gameOver, playerWon);
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getOpponentBoard() {
        return opponentBoard;
    }

    private Board createRandomBoard() {
        while (true) {
            Board board = new Board();
            boolean complete = true;
            for (ShipType type : ShipType.values()) {
                boolean placed = false;
                for (int attempt = 0; attempt < 1000 && !placed; attempt++) {
                    Coordinate start = new Coordinate(random.nextInt(Board.SIZE),
                            random.nextInt(Board.SIZE));
                    placed = board.placeShip(type, start, random.nextBoolean());
                }
                if (!placed) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                return board;
            }
        }
    }
}
