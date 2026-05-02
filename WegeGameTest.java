// importation of necessary libraries for testing
import org.junit.*;
import static org.junit.Assert.*;
import java.util.List;
import java.util.LinkedList;
import javafx.geometry.Pos;

/**
 * JUnit tests for the WegeGame class.
 * @author Mauro Nunez
 */
public class WegeGameTest {

    private LinkedList<WegeCard> deck;
    private WegeGame game;

    /**
     * Sets up a fresh game before each test is run.
     */
    @Before
    public void setUp() {
        deck = new LinkedList<>();
        // Add some cards to the deck for testing
        deck.add(new WegeCard(WegeCard.CardType.LAND, false, false));
        deck.add(new WegeCard(WegeCard.CardType.WATER, false, false));
        deck.add(new WegeCard(WegeCard.CardType.LAND, false, false));
        deck.add(new WegeCard(WegeCard.CardType.WATER, false, false));
        deck.add(new WegeCard(WegeCard.CardType.LAND, false, false));
        game = new WegeGame(2, 2, deck);
    }
    
    // ----------------------------------------------
    // Tests for isBoardFull()
    // ----------------------------------------------

    // Test 0: empty board is not full
    @Test
    public void testIsBoardFullEmpty() {
        assertFalse(game.isBoardFull());
    }

    // Test 1: board with one card is not full
    @Test
    public void testIsBoardFullOnecard() {
        WegeCard card = new WegeCard(WegeCard.CardType.LAND, false, false);
        game.placeCard(card, 0, 0);
        assertFalse(game.isBoardFull());
    }

    // Test many: fully filled 2x2 board is full
    @Test
    public void testIsBoardFullFilled() {
        WegeCard card = new WegeCard(WegeCard.CardType.LAND, false, false);
        game.placeCard(card, 0, 0);
        game.placeCard(card, 0, 1);
        game.placeCard(card, 1, 0);
        game.placeCard(card, 1, 1);
        assertTrue(game.isBoardFull());
    }

    // ----------------------------------------------
    // Tests for drawCard()
    // ----------------------------------------------

    // Test: drawing from a non-empty deck returns a card
    @Test
    public void testDrawCardNonEmpty() {
        assertNotNull(game.drawCard());
    }

    // Test: drawing from an empty deck returns null
    @Test
    public void testDrawCardEmpty() {
        LinkedList<WegeCard> emptyDeck = new LinkedList<>();
        WegeGame emptyGame = new WegeGame(2, 2, emptyDeck);
        assertNull(emptyGame.drawCard());
    }

    // Test: drawing removes the card from the deck
    @Test
    public void testDrawCardRemoves() {
        game.drawCard();
        game.drawCard();
        game.drawCard();
        game.drawCard();
        game.drawCard();
        assertNull(game.drawCard());
    }

    // -------------------------------------------------------
    // Tests for switchTurn()
    // -------------------------------------------------------

    // Test: game starts as land player's turn
    @Test
    public void testInitialTurn() {
        assertTrue(game.isLandPlayerTurn());
    }

    // Test: after one switch it is water player's turn
    @Test
    public void testSwitchTurnOnce() {
        game.switchTurn();
        assertFalse(game.isLandPlayerTurn());
    }

    // Test: after two switches it is land player's turn again 
    @Test
    public void testSwitchTurnTwice() {
        game.switchTurn();
        game.switchTurn();
        assertTrue(game.isLandPlayerTurn());
    }

    // -------------------------------------------------------
    // Tests for placeCard()
    // -------------------------------------------------------

    // Test: placing a card stores it correctly
    @Test
    public void testPlaceCard() {
        WegeCard card = new WegeCard(WegeCard.CardType.LAND, false, false);
        game.placeCard(card, 0, 0);
        assertEquals(card, game.getCardAt(0, 0));
    }

    // Test: empty cell returns null
    @Test
    public void testGetCardAtEmpty() {
        assertNull(game.getCardAt(0, 0));
    }

    // -------------------------------------------------------
    // Tests for isLegalMove()
    // -------------------------------------------------------

    // Test: first card can go anywhere on empty board */
    @Test
    public void testIsLegalMoveFirstCard() {
        WegeCard card = new WegeCard(WegeCard.CardType.LAND, false, false);
        assertTrue(game.isLegalMove(card, 0, 0));
        assertTrue(game.isLegalMove(card, 1, 1));
    }

    // Test: cannot place card on occupied cell
    @Test
    public void testIsLegalMoveOccupied() {
        WegeCard card = new WegeCard(WegeCard.CardType.LAND, false, false);
        game.placeCard(card, 0, 0);
        assertFalse(game.isLegalMove(card, 0, 0));
    }

    // Test: cannot place card on non-adjacent cell
    @Test
    public void testIsLegalMoveNotAdjacent() {
        WegeCard card = new WegeCard(WegeCard.CardType.LAND, false, false);
        game.placeCard(card, 0, 0);
        assertFalse(game.isLegalMove(card, 1, 1));
    }

    // Test: can place matching card adjacent to existing card */
    /** Test: can place matching card adjacent to existing card */
    @Test
    public void testIsLegalMoveAdjacentMatch() {
        // place a water card at 0,0 with default orientation (TOP_LEFT)
        WegeCard card1 = new WegeCard(WegeCard.CardType.WATER, false, false);
        game.placeCard(card1, 0, 0);
        // place another water card to the right - should match since both have water on TOP_RIGHT/TOP_LEFT
        WegeCard card2 = new WegeCard(WegeCard.CardType.WATER, false, false);
        assertFalse(game.isLegalMove(card2, 0, 1));
    }

    // Test: land card matched water card side by side
    @Test
    public void testIsLegalMoveAdjacentLandNextToWater() {
        WegeCard water = new WegeCard(WegeCard.CardType.WATER, false, false);
        game.placeCard(water, 0, 0);
        WegeCard land = new WegeCard(WegeCard.CardType.LAND, false, false);
        assertTrue(game.isLegalMove(land, 0, 1));
    }

    // Test: placing a card above an existing card with matching sides
    @Test
    public void testIsLegalMoveAdjacentAbove() {
        WegeCard card1 = new WegeCard(WegeCard.CardType.WATER, false, false);
        game.placeCard(card1, 1, 0);
        WegeCard card2= new WegeCard(WegeCard.CardType.LAND, false, false);
        assertTrue(game.isLegalMove(card2, 0, 0));
    }

    // Test: placing a card below an existing card with matching sides
    @Test
    public void testIsLegalMoveBelow() {
        WegeCard water = new WegeCard(WegeCard.CardType.WATER, false, false);
        game.placeCard(water, 0, 0);
        WegeCard land = new WegeCard(WegeCard.CardType.LAND, false, false);
        assertTrue(game.isLegalMove(land, 1, 0));
    }

    // Test: placing a to the left of an existing card 
    @Test
    public void testIsLegalMoveLeft() {
        WegeCard water = new WegeCard(WegeCard.CardType.WATER, false, false);
        game.placeCard(water, 0, 1);
        WegeCard land = new WegeCard(WegeCard.CardType.LAND, false, false);
        assertTrue(game.isLegalMove(land, 0, 0));
    }
}

