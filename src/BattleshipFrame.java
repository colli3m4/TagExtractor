import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public final class BattleshipFrame extends JFrame {
    private static final Color WATER = new Color(195, 224, 240);
    private static final Color SHIP = new Color(115, 130, 145);
    private static final Color HIT = new Color(220, 75, 65);
    private static final Color MISS = new Color(245, 245, 245);

    private final BattleshipGame game = new BattleshipGame();
    private final JButton[][] playerCells = new JButton[Board.SIZE][Board.SIZE];
    private final JButton[][] opponentCells = new JButton[Board.SIZE][Board.SIZE];
    private final JLabel statusLabel = new JLabel("Your turn: choose a square on the opponent's board.");

    public BattleshipFrame() {
        super("Battleship");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        add(statusLabel, BorderLayout.NORTH);

        JPanel boards = new JPanel(new GridLayout(1, 2, 20, 0));
        boards.add(createBoardPanel("Your Fleet", playerCells, false));
        boards.add(createBoardPanel("Opponent", opponentCells, true));
        add(boards, BorderLayout.CENTER);

        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(event -> startNewGame());
        JPanel controls = new JPanel();
        controls.add(newGameButton);
        add(controls, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(790, 560));
        pack();
        setLocationRelativeTo(null);
        refreshBoards();
    }

    private JPanel createBoardPanel(String title, JButton[][] cells, boolean targetable) {
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(heading.getFont().deriveFont(Font.BOLD, 16f));
        panel.add(heading, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(Board.SIZE + 1, Board.SIZE + 1, 2, 2));
        grid.add(new JLabel(""));
        for (int column = 0; column < Board.SIZE; column++) {
            grid.add(axisLabel(String.valueOf((char) ('A' + column))));
        }
        for (int row = 0; row < Board.SIZE; row++) {
            grid.add(axisLabel(Integer.toString(row + 1)));
            for (int column = 0; column < Board.SIZE; column++) {
                final int targetRow = row;
                final int targetColumn = column;
                JButton cell = new JButton();
                cell.setMargin(new java.awt.Insets(0, 0, 0, 0));
                cell.setFocusable(false);
                cell.setPreferredSize(new Dimension(34, 34));
                cell.setBackground(WATER);
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
                if (targetable) {
                    cell.setToolTipText("Fire at " + (char) ('A' + column) + (row + 1));
                    cell.addActionListener(event -> takeTurn(targetRow, targetColumn));
                } else {
                    cell.setEnabled(false);
                }
                cells[row][column] = cell;
                grid.add(cell);
            }
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JLabel axisLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11f));
        return label;
    }

    private void takeTurn(int row, int column) {
        Coordinate target = new Coordinate(row, column);
        GameTurnResult result = game.fireAt(target);
        refreshBoards();

        String playerMessage = outcomeText("You", result.getPlayerOutcome());
        if (result.isGameOver()) {
            statusLabel.setText(result.isPlayerWon()
                    ? playerMessage + ". You sank the enemy fleet. You win!"
                    : outcomeText("Computer", result.getComputerOutcome())
                    + ". Your fleet was sunk. Game over.");
            return;
        }

        Coordinate computerTarget = result.getComputerTarget();
        String location = String.valueOf((char) ('A' + computerTarget.getColumn()))
                + (computerTarget.getRow() + 1);
        statusLabel.setText(playerMessage + ". Computer fired at " + location + ": "
                + outcomeText("Computer", result.getComputerOutcome()).toLowerCase() + ".");
    }

    private String outcomeText(String shooter, ShotOutcome outcome) {
        switch (outcome) {
            case HIT:
                return shooter + " hit a ship";
            case SUNK:
                return shooter + " sank a ship";
            default:
                return shooter + " missed";
        }
    }

    private void startNewGame() {
        game.newGame();
        statusLabel.setText("Your turn: choose a square on the opponent's board.");
        refreshBoards();
    }

    private void refreshBoards() {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int column = 0; column < Board.SIZE; column++) {
                Coordinate coordinate = new Coordinate(row, column);
                updateCell(playerCells[row][column], game.getPlayerBoard(), coordinate, true);
                updateCell(opponentCells[row][column], game.getOpponentBoard(), coordinate, false);
            }
        }
    }

    private void updateCell(JButton cell, Board board, Coordinate coordinate, boolean revealShips) {
        if (!board.wasShotAt(coordinate)) {
            cell.setText("");
            cell.setBackground(revealShips && board.getShipAt(coordinate) != null ? SHIP : WATER);
            cell.setEnabled(!revealShips && !isGameOver());
            return;
        }

        if (board.getShipAt(coordinate) == null) {
            cell.setText("•");
            cell.setBackground(MISS);
        } else {
            cell.setText("X");
            cell.setBackground(HIT);
        }
        cell.setEnabled(false);
    }

    private boolean isGameOver() {
        return !game.getPlayerBoard().hasRemainingShips()
                || !game.getOpponentBoard().hasRemainingShips();
    }
}
