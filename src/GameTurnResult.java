public final class GameTurnResult {
    private final Coordinate playerTarget;
    private final ShotOutcome playerOutcome;
    private final Coordinate computerTarget;
    private final ShotOutcome computerOutcome;
    private final boolean gameOver;
    private final boolean playerWon;

    public GameTurnResult(Coordinate playerTarget, ShotOutcome playerOutcome,
                          Coordinate computerTarget, ShotOutcome computerOutcome,
                          boolean gameOver, boolean playerWon) {
        this.playerTarget = playerTarget;
        this.playerOutcome = playerOutcome;
        this.computerTarget = computerTarget;
        this.computerOutcome = computerOutcome;
        this.gameOver = gameOver;
        this.playerWon = playerWon;
    }

    public Coordinate getPlayerTarget() {
        return playerTarget;
    }

    public ShotOutcome getPlayerOutcome() {
        return playerOutcome;
    }

    public Coordinate getComputerTarget() {
        return computerTarget;
    }

    public ShotOutcome getComputerOutcome() {
        return computerOutcome;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPlayerWon() {
        return playerWon;
    }
}
