// importing necessary libraries for the JavaFX application
import java.util.LinkedList;
import java.util.Collections;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;


/**
 * The main class for the Wege (LandLock) game. 
 * This class will set up the game board and handle user interactions.
 * @author Mauro Nunez
 */
public class Wege extends Application {

    private static int rows = 6; // number of rows in the game board
    private static int columns = 6; // number of columns in the game board
    private static int specialtyCount = 2; // number of each special card in the deck 
    private WegeButton[][] board; // 2D array to represent the game board.
    private WegeButton nextCardButton; // button to show the next card to be played. 
    private Label statusLabel; // Label to show the current status of the game (e.g., whose turn it is, who won, etc.)
    private WegeGame game; // instance of the game logic class to manage the game state and interactions.

    /**
     * Builds the game board and initialized the UI components.
     * @param primaryStage the main JavaFX window (stage)
     */
    public void start(Stage primaryStage) {

        // Get parameters passed from the main method for rows and columns, if any
        Parameters params = getParameters();
        java.util.List<String> args = params.getRaw();

        if (args.size() == 1) {
            try {
                specialtyCount = Integer.parseInt(args.get(0)); // parse the first argument as the specialty count
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument, using default settings.");
            }
        } else if (args.size() == 2) {
            try {
                rows = Integer.parseInt(args.get(0));    // parse the first argument as the number of rows
                columns = Integer.parseInt(args.get(1)); // parse the second argument as the number of columns
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument, using default 6x6 board.");
            }
        } else if (args.size() >= 3) {
            try {
                rows = Integer.parseInt(args.get(0));          // parse the first argument as the number of rows
                columns = Integer.parseInt(args.get(1));       // parse the second argument as the number of columns
                specialtyCount = Integer.parseInt(args.get(2)); // parse the third argument as the specialty count
            } catch (NumberFormatException e) {
                System.out.println("Invalid argument, using default settings.");
            }
        }        
        
        // set the colors for the buttons and the background to match a forest green theme
        WegeButton.setEmptyColor(Color.web("#3d5a3d")); // set the empty color for the buttons to match the forest green theme
        WegeButton.setBackgroundColor(Color.web("#3d5a3d")); // card background aswell to match the forest green theme

        // creates the board buttons
        board = new WegeButton[rows][columns];
        GridPane gridPane = new GridPane();
        gridPane.setHgap(4); // horizontal gap between the tiles
        gridPane.setVgap(4);// vertical gap between the tiles
        gridPane.setStyle("-fx-background-color: #2d1f0e; -fx-padding: 8px;");

        // loop to initialize the board with WegeButtons and add them to the grid pane
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                board[r][c] = new WegeButton(80,80);
                board[r][c].setStyle("-fx-background-color: #3d5a3d; -fx-border-color: #5c3d1e; -fx-border-radius: 4px; -fx-background-radius: 4px;"); // style the buttons with background color, border, and rounded corners
                gridPane.add(board[r][c], c, r);

                final int row = r; //final variable to be used in the event handler
                final int col = c; //final variable to be used in the event handler

                // set up the event handler for each button on the board to place a card when clicked
                board[r][c].setOnAction( e -> {
                    if (nextCardButton.getCard() == null)
                        return;
                    WegeCard cardToPlace = nextCardButton.getCard();
                    if (game.isLegalMove(cardToPlace, row, col)) {
                        board[row][col].setCard(cardToPlace);
                        game.placeCard(cardToPlace, row, col);
                        nextCardButton.setCard(null);
                        game.switchTurn();
                        if (game.isBoardFull())
                            statusLabel.setText("Game Over!"); 
                        else if (game.isLandPlayerTurn())
                            statusLabel.setText("Land Player's Turn");
                        else
                            statusLabel.setText("Water Player's Turn");
                    }
                });
            }
        }

        // create the next card button and the status label, and set up the top bar of the UI
        nextCardButton = new WegeButton(80, 80); // the next card button
        nextCardButton.setStyle("-fx-background-color: #3d5a3d; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 4px; -fx-background-radius: 4px;"); // style the next card button with background color, border, and rounded corners
        statusLabel = new Label("Land Player's Turn"); // initial status label message
        game = new WegeGame(rows, columns, createDeck(specialtyCount));

        // set up the event handler for the next card button to show the next card in the deck
        nextCardButton.setOnAction( e -> {
            if (nextCardButton.getCard() == null) {
                // draw a card from the deck and display it on the next card button
                WegeCard nextCard = game.drawCard();
                if (nextCard != null)
                    nextCardButton.setCard(nextCard);
            } else {
                nextCardButton.rotate(); // rotate the card on the next card button when clicked
            }
        }); 

        Button restartButton = new Button("Restart"); // button to restart the game
        restartButton.setStyle("-fx-background-color: #7a5230; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-border-color: white; -fx-border-width: 2px; -fx-border-radius: 4px; -fx-background-radius: 4px;"); // style the restart button with background color, text color, font, border, and rounded corners
        restartButton.setOnAction(e -> {
            game = new WegeGame(rows, columns, createDeck(specialtyCount)); // reset the game state by creating a new instance of the game logic class with a new deck
            nextCardButton.setCard(null); // clear the next card button to reset the game state
            statusLabel.setText("Land Player's Turn"); // reset the status label to the initial message
            for (int r = 0; r < rows; r++) // loop through each row of the board
                for (int c = 0; c < columns; c++) // loop through each column of the board
                    board[r][c].setCard(null); // clear the card from each button on the board to reset the game state
        }); 

        // top bar with a label and the next card button side by side
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;"); // style the status label
        Label nextCardLabel = new Label("Next Card:"); // label for the next card button
        nextCardLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;"); // style the next card label

        // create a vertical box to hold the next card label and button, and style it
        VBox nextCardBox = new VBox(4, nextCardLabel, nextCardButton); // vertical box to hold the next card label and button
        nextCardBox.setAlignment(Pos.CENTER); // center the next card box

        // create the top bar to hold the status label and the next card box, and style it
        HBox topBar = new HBox(20, statusLabel, nextCardBox, restartButton); // horizontal box to hold the status label and the next card box
        topBar.setStyle("-fx-background-color: #4a2e12; -fx-padding: 10px; -fx-border-color: #7a5230; -fx-border-width: 2px;"); // style the top bar with background color, padding, and border
        topBar.setAlignment(Pos.CENTER_LEFT); // align the top bar to the left

        // setup for the layout of the game using BorderPane
        BorderPane pane = new BorderPane();
        pane.setCenter(gridPane); // place the grid pane (game board) at the center of the border pane
        pane.setTop(topBar); // place the top bar at the top of the border pane

        // set the background color and padding for the border pane, and create the scene with the border pane as the root node
        pane.setStyle("-fx-background-color: #2d1f0e; -fx-padding: 16px;");
        Scene scene = new Scene(pane);
        primaryStage.setTitle("Wege (The Legend of LandLock) Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Creates and returns the default deck of cards for the wage game.
     * default deck has 40 cards
     * @return the LinkedList of WegeCards representing the default deck
     */
    private LinkedList<WegeCard> createDeck(int specialtyCount) {
        LinkedList<WegeCard> defaultDeck = new LinkedList<>();

        // add 2 cossack cards
        for (int i = 0; i < specialtyCount; i++) 
            defaultDeck.add(new WegeCard(WegeCard.CardType.COSSACK, false, false));

        // add 2 bridge cards
        for (int i = 0; i < specialtyCount; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.BRIDGE, false, false));

        // add 2 land cards with gnome on path
        for (int i = 0; i < specialtyCount; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.LAND, true, true));

        // add 2 land cards with gnome off path
        for (int i = 0; i < specialtyCount; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.LAND, true, false)); 

        // add 2 water card with gnome on the path
        for (int i = 0; i < specialtyCount; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.WATER, true, true));

        // add 2 water cards with a gnome off the path
        for (int i = 0; i < specialtyCount; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.WATER, true, false));

        // scale plain cards so the dech has atlest (rows * columns + 2) total cards
        int specialtyTotal = specialtyCount * 6; // total number of specialty cards in the deck
        int plainCardsNeeded = Math.max(28, (rows * columns + 2) - specialtyTotal); // calculate the number of plain cards needed to ensure the deck has enough cards for the game
        int plainEach = plainCardsNeeded / 2; // number of plain land and water cards needed

        for (int i = 0; i < plainEach; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.LAND, false, false)); 

        for (int i = 0; i < plainEach; i++)
            defaultDeck.add(new WegeCard(WegeCard.CardType.WATER, false, false));

        Collections.shuffle(defaultDeck); // shuffle the deck to randomize the order of the cards.
        return defaultDeck;
    }

    /**
     * Launcher for the game. Launches the game with optional command line arguments.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       Application.launch(args); // launch the JavaFX application with the provided command line arguments
    }
}