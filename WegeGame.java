import java.util.LinkedList;
import javafx.geometry.Pos;

/**
 * Represents the class that will handle the logic of the Wege game.
 * This class will manage the game state, player turns, deck and determine the winner.
 * @author Mauro Nunez
 */
public class WegeGame {

    /** 2D array for the game board, where each cell can hold a WegeCard */
    private WegeCard[][] board;

    /** The number of rows on the board */
    private int rows;

    /** The number of columns on the board */
    private int columns;

    /** LinkedList to represent the deck of cards */
    private LinkedList<WegeCard> deck;

    /** True for Land Player's turn, false for Water Player's turn */
    private boolean isLandPlayerTurn;

    /**
     * Creates a Wege game with the given board size and deck.
     * @param rows the number of rows for the game board
     * @param columns the number of columns for the game board
     * @param deck the deck of cards to be used in the game
     */
    public WegeGame(int rows, int columns, LinkedList<WegeCard> deck) {
        this.rows = rows; // set the number of rows
        this.columns = columns; // set the number of columns
        this.deck = deck; // set the deck of cards
        this.board = new WegeCard[rows][columns]; // initialize the game board as a 2D array of WegeCards
        this.isLandPlayerTurn = true; // Land player starts first
    }

    /**
     * Returns the card at the specified location on the board.
     * @param row the row index of the card
     * @param column the column index of the card
     * @return the WegeCard at the specified location, or null if empty
     */
    public WegeCard getCardAt(int row, int column) {
        return board[row][column];
    }

    /**
     * Returns whether it is the land player's turn.
     * @return true if it is the land player's turn
     */
    public boolean isLandPlayerTurn() {
        return isLandPlayerTurn;
    }

    /**
     * Switches the turn to the other player.
     */
    public void switchTurn() {
        isLandPlayerTurn = !isLandPlayerTurn;
    }

    /**
     * Returns whether the board is completely filled.
     * @return true if the board is full, false otherwise
     */
    public boolean isBoardFull() {
        for (int r = 0; r < rows; r++) // loop through each row
            for (int c = 0; c < columns; c++) // loop through each column in the current row
                if (board[r][c] == null) // if any cell is null, the board is not full
                    return false;
        return true; // if we reach here, the board is full
    }

    /**
     * Draws a card from the deck and returns it.
     * @return the drawn WegeCard, or null if the deck is empty
     */
    public WegeCard drawCard() {
        if (deck.isEmpty()) // if the deck is empty, return null
            return null; // otherwise, remove and return the first card from the deck
        return deck.removeFirst(); // remove and return the first card from the deck
    }

    /**
     * Places a card on the board at the specified location.
     * @param card the WegeCard to be placed
     * @param row the row to place the card
     * @param column the column to place the card
     */
    public void placeCard(WegeCard card, int row, int column) {
        board[row][column] = card;
    }

    /**
     * Returns whether placing the given card at the specified location is a legal move.
     * @param card the WegeCard to be placed
     * @param row the row to place the card
     * @param column the column to place the card
     * @return true if the move is legal, false otherwise
     */
    public boolean isLegalMove(WegeCard card, int row, int column) {
        // you cannot place a card on an occupied cell
        if (board[row][column] != null)
            return false;

        // check if the board is empty, in which case any card can be placed
        boolean isBoardEmpty = true;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                if (board[r][c] != null)
                    isBoardEmpty = false;
        if (isBoardEmpty)
            return true;

        // must be placed adjacent to an existing card
        boolean hasNeighbor = false;

        // check the card above
        if (row > 0 && board[row - 1][column] != null) {
            hasNeighbor = true; // there is a card above
            if (card.isWater(Pos.TOP_LEFT) != board[row - 1][column].isWater(Pos.BOTTOM_LEFT))
                return false; 
        }

        // check the card below
        if (row < rows - 1 && board[row + 1][column] != null) {
            hasNeighbor = true; 
            if (card.isWater(Pos.BOTTOM_LEFT) != board[row + 1][column].isWater(Pos.TOP_LEFT))
                return false; 
        }

        // check the card to the left
        if (column > 0 && board[row][column-1] != null) {
            hasNeighbor = true;
            if (card.isWater(Pos.TOP_LEFT) != board[row][column - 1].isWater(Pos.TOP_RIGHT))
                return false;
        }

        // check the card to the right
        if (column < columns - 1 && board[row][column + 1] != null) {
            hasNeighbor = true;
            if (card.isWater(Pos.TOP_RIGHT) != board[row][column + 1].isWater(Pos.TOP_LEFT))
                return false;
        }
        return hasNeighbor; // the move is legal if there is at least one neighbor and all neighbors match
    }

    /**
     * Returns the number of cards remaining in the deck.
     * @return the number of cards remaining in the deck.
     */
    public int getDeckSize() {
        return deck.size();
    }
}