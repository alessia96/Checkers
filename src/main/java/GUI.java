import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;

import javafx.scene.image.Image;
import org.reactfx.util.FxTimer;

import java.time.Duration;
import java.util.List;
import java.util.stream.IntStream;

/**
 * The Class GUI is used to visually represent the software.
 * It includes a board representation that gets displayed on the screen and a full graphical board display.
 * GUI updates the display after any completed move made by the User and the AI, with appropriate pauses to show
 * intermediate states in multi-step moves.
 * GUI is fully interactive, with mechanics of drag & drop of each checker.
 * Though GUI, users can access the rules of the games via a corresponding button opening a pop-up window.
 * GUI contains a help facility which provides hints about available moves, given the current game state.
 *
 * @author CandNo. 149112
 */
public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

    private BorderPane root;
    private Scene scene;
    private GridPane gridPane;
    private BorderPane infoPane;

    private Circle[][] checkers;
    private Pane[][] tiles;
    private boolean currentlyDraggable;

    private TextArea movesLog;
    private Slider difficultySlider;
    private Button newGame;

    private int selectedDifficulty = 0;
    private boolean hintsToggled;
    private boolean shownOnce;
    private Label blacksCaptured, redsCaptured;

    /**
     * Handles initialisation of scene and panes in the primary stage.
     * Sets checkers to currently be draggable.
     * Handles generation of grid and info pane.
     * Sets stylesheets for scene.
     * */
    @Override
    public void start(Stage primaryStage)
    {
        root = new BorderPane();
        gridPane = new GridPane();
        infoPane = new BorderPane();

        // provide users with interactivity with the checkers
        currentlyDraggable = true;

        // fill up gridPane and infoPane
        generateGrid();
        generateInfoPane();

        root.setLeft(gridPane);
        root.setRight(infoPane);

        // set up scene and stylesheet for the scene
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/checkerfx.css").toExternalForm());

        // set up stage
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Generates a new move depending on current turn in the match, by using the
     * successor function to validate user and AI moves.
     * Handles switching of currentlyDraggable variable for checkers interactivity.
     * */
    private void getMove()
    {
        Move move;
        // switch to allow a new move to be made
        boolean getNewMove = false;
        boolean hasJustCaptured = false;

        if (match.getTurn() == CheckersGame.Player.AI)
        {
            move = match.getAIController().getAIMove(selectedDifficulty);

            boolean valid = false;
            List<Move> states = board.getAvailableStates(match.getTurn());

            for (Move m : states)
            {
                if (m.getSource() == move.getSource() && m.getTarget() == move.getTarget())
                {
                    valid = true;
                }
            }

            if (valid)
            {
                movesLog.setText("Red moves from [" + move.getSource().getRow() + ", " + move.getSource().getColumn() +
                        "] to [" + move.getTarget().getRow() + ", " + move.getTarget().getColumn() + "]" +
                        "\n" + movesLog.getText());
                if (board.makeMove(move, false))
                {
                    hasJustCaptured = true;
                }

                // keep turn to be human turn if capture was made and another capture is available
                if (hasJustCaptured && board.canCheckerCapture(move.getTarget().getChecker(), CheckersGame.Player.AI))
                {
                    match.setTurn(CheckersGame.Player.AI);
                    getNewMove = true;
                }
                else
                {
                    currentlyDraggable = true;
                }
                generateGrid();
            }

            gameFinishedCheck();

            if (getNewMove)
            {
                // delay of 0.5 seconds and get a new move from the AI
                Runnable getMoveTask = () -> { getMove(); };
                FxTimer.runLater(Duration.ofMillis(500), getMoveTask);
            }
        }
        else if (match.getTurn() == CheckersGame.Player.HUMAN)
        {
            // get user move from user controller
            move = match.getUserController().getUserMove();
            boolean valid = false;

            // use successor function to validate user moves
            List<Move> states = board.getAvailableStates(match.getTurn());

            for (Move m : states)
            {
                // check user move against available moves
                if (m.getSource() == move.getSource() && m.getTarget() == move.getTarget())
                {
                    valid = true;
                }
            }

            if (valid)
            {
                movesLog.setText("Black moves from [" + move.getSource().getRow() + ", " + move.getSource().getColumn() +
                        "] to [" + move.getTarget().getRow() + ", " + move.getTarget().getColumn() + "]\n" + movesLog.getText());

                // request board to make move
                if (board.makeMove(move, false))
                {
                    hasJustCaptured = true;
                }

                // keep turn to be human turn if capture was made and another capture is available
                if (hasJustCaptured && board.canCheckerCapture(move.getTarget().getChecker(), CheckersGame.Player.HUMAN))
                {
                    match.setTurn(CheckersGame.Player.HUMAN);
                }
                else
                {
                    // allow player to interact
                    currentlyDraggable = false;
                    getNewMove = true;
                }
            }
            // if move is not valid, display why
            else
            {
                if (board.isSourceSameAsTarget(move))
                {
                    movesLog.setText("Move not valid: target tile cannot be source tile.\n" + movesLog.getText());
                }
                else if (board.isTargetWhite(move))
                {
                    movesLog.setText("Move not valid: target tile cannot be a white tile.\n" + movesLog.getText());
                }
                else if (board.isMovingBackwards(move))
                {
                    movesLog.setText("Move not valid: regular checkers can only move forward.\n" + movesLog.getText());
                }
                else if (board.isTargetOccupied(move))
                {
                    movesLog.setText("Move not valid: target tile is already occupied.\n" + movesLog.getText());
                }
                else if (!board.isValidJump(move))
                {
                    movesLog.setText("Move not valid: you can only capture the opponent's checker.\n" + movesLog.getText());
                }
                else if (board.getCheckerInBetween() != null)
                {
                    movesLog.setText("Move not valid: you must capture the opponent's checker.\n" + movesLog.getText());
                }
            }

            // set checker invisible (for cases in which it falls outside of the grid)
            checkers[move.getSource().getRow()][move.getSource().getColumn()].setVisible(false);

            generateGrid();

            if (getNewMove)
            {
                if (!gameFinishedCheck())
                {
                    // delay of 0.5 seconds and get a new move from the AI
                    Runnable getMoveTask = () -> { getMove(); };
                    FxTimer.runLater(Duration.ofMillis(500), getMoveTask);
                }
            }
        }

        // update labels
        blacksCaptured.setText("Blacks Captured: " + (12 - match.getBoard().getCheckers(Checker.Colour.BLACK).size()));
        redsCaptured.setText("Reds Captured: " + (12 - match.getBoard().getCheckers(Checker.Colour.RED).size()));
    }

    /**
     * Checks if the game is finished. If it is, displays alert and returns true.
     *
     * @return true if the game is finished, false otherwise.
     * */
    private boolean gameFinishedCheck()
    {
        // use successor function to check if there are moves available
        List<Move> movesAvailable = board.getAvailableStates(match.getTurn());

        // check if any move is available for the human player
        if (movesAvailable.isEmpty() && match.getTurn() == CheckersGame.Player.HUMAN) // blacks are finished
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You lost.", ButtonType.CLOSE);
            alert.setTitle("Defeat");
            alert.setHeaderText("Defeat");
            alert.show();
            currentlyDraggable = false;
            generateGrid();
            return true;
        }
        // check if any move is available for the AI player
        else if (movesAvailable.isEmpty() && match.getTurn() == CheckersGame.Player.AI)   // reds are finished
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You won.", ButtonType.CLOSE);
            alert.setTitle("Victory");
            alert.setHeaderText("Victory");
            alert.show();
            currentlyDraggable = false;
            generateGrid();
            return true;
        }
        return false;
    }

    /**
     * Generates a grid that displays all checkers and tiles.
     * Adds action listeners to checkers.
     * */
    private void generateGrid()
    {
        if (checkers == null) checkers = new Circle[board.getGrid().length][board.getGrid().length];
        if (tiles == null) tiles = new Pane[board.getGrid().length][board.getGrid().length];

        // add white tiles to the gridPane
        IntStream.range(0, 8).forEach((row) ->
            IntStream.range(0, 8).forEach((col) ->
            {
                if (!board.getTileAt(row, col).isBlack())
                {
                    Rectangle box = new Rectangle(70, 70);
                    box.setFill(Color.WHITE);
                    gridPane.add(box, col, row);
                }
            }
        ));

        IntStream.range(0, 8).forEach((row) ->
            IntStream.range(0, 8).forEach((col) ->
            {
                if (board.getTileAt(row, col).isBlack())
                {
                    // add black tiles to the pane
                    tiles[row][col] = new Pane();
                    tiles[row][col].setStyle("-fx-background-color: #181818");

                    if (board.getTileAt(row, col).isOccupied())
                    {
                        // add circles to represent checkers
                        checkers[row][col] = new Circle(37, 35, 30);

                        Image img;
                        if (board.getTileAt(row, col).getChecker().getColour() == Checker.Colour.RED)
                        {
                            if (board.getTileAt(row, col).getChecker().isKing())
                            {
                                img = new Image("red_king.png");
                            }
                            else
                            {
                                img = new Image("red_checker.png");
                            }

                            checkers[row][col].setFill(new ImagePattern(img));
                        }
                        else
                        {
                            if (board.getTileAt(row, col).getChecker().isKing())
                            {
                                img = new Image("black_king.png");
                            }
                            else
                            {
                                img = new Image("black_checker.png");
                            }

                            checkers[row][col].setFill(new ImagePattern(img));

                            // if player is allowed interaction, make checkers draggable
                            if (currentlyDraggable)
                                MouseControlUtil.makeDraggable(checkers[row][col]);

                            // add event handler for mouse pressed to record source
                            checkers[row][col].addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                                    match.getUserController().onCheckerPressed(row, col));

                            // add event handler for mouse released to record target and get new move
                            checkers[row][col].addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
                            {
                                match.getUserController().onCheckerReleased(checkers[row][col]);
                                getMove();
                            });
                        }

                        tiles[row][col].setMaxSize(70, 70);
                        tiles[row][col].getChildren().addAll(checkers[row][col]);

                        if (hintsToggled)
                        {
                            showHints();
                        }
                    }

                    gridPane.add(tiles[row][col], col, row);
                }
            }
        ));
    }

    /**
     * Generates side pane containing information about the game state
     * (log of the moves made, checkers captured, current difficulty, hints toggled)
     * and commands to start a new game or read game rules.
     * */
    private void generateInfoPane()
    {
        GridPane topPane = topInfoPane();

        VBox movesLogPane = new VBox();
        movesLogPane.setPadding(new Insets(0, 10, 0, 10));
        Label movesLogLabel = new Label("Moves Log");
        if (movesLog == null) movesLog = new TextArea();
        movesLog.setWrapText(true);
        movesLog.setPrefSize(200, 380);
        movesLog.setEditable(false);
        Separator separator = new Separator();
        separator.setPadding(new Insets(15, 10, 10, 10));
        movesLogPane.getChildren().addAll(movesLogLabel, new Label(" "), movesLog, separator);

        VBox bottomPane = new VBox();
        bottomPane.setPadding(new Insets(0, 10, 10, 10));
        blacksCaptured = new Label("Blacks Captured: " + (12 - match.getBoard().getCheckers(Checker.Colour.BLACK).size()));
        redsCaptured = new Label("Reds Captured: " + (12 - match.getBoard().getCheckers(Checker.Colour.RED).size()));
        bottomPane.getChildren().addAll(blacksCaptured, redsCaptured);

        infoPane.setTop(topPane);
        infoPane.setCenter(movesLogPane);
        infoPane.setBottom(bottomPane);

        root.setRight(infoPane);
    }

    /**
     * Creates top part of the information pane, containing buttons to
     * start a new game and read the rules, checkbox to toggle hints,
     * and slider to set the game difficulty.
     * Adds a button listener to each button and handles clearing the current game and
     * opening a new window containing the game rules.
     *
     * @return topInfoPane.
     * */
    private GridPane topInfoPane()
    {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(10, 10, 10, 10));
        newGame = new Button("New Game");
        newGame.setOnAction(event ->
        {
            match = new CheckersGame();
            board = match.getBoard();
            movesLog.clear();
            currentlyDraggable = true;
            generateGrid();
            generateInfoPane();
        });
        Button readRules = new Button("Read Rules");
        readRules.setOnAction(event ->
        {
            openRulesWindow();
        });
        CheckBox toggleHints = new CheckBox("Toggle Hints");
        if (hintsToggled)
        {
            toggleHints.setSelected(true);
        }
        toggleHints.setPadding(new Insets(10, 10, 10, 10));
        toggleHints.setOnAction(event ->
        {
            hintsToggled = !hintsToggled;
            showHints();
        });
        HBox sliderBox = new HBox();
        Label difficultyLabel = new Label("Difficulty:\n" + selectedDifficulty);
        difficultyLabel.setTextAlignment(TextAlignment.CENTER);
        if (difficultySlider == null) difficultySlider = new Slider();
        difficultySlider.setSnapToTicks(true);
        difficultySlider.setValue(difficultySlider.getValue());
        difficultySlider.setOnMouseDragged(event ->
        {
            selectedDifficulty = (int) difficultySlider.getValue();
            difficultyLabel.setText("Difficulty:\n" + (int) difficultySlider.getValue());
            if (selectedDifficulty == 6 && !shownOnce)
            {
                shownOnce = true;
                Alert alert = new Alert(Alert.AlertType.WARNING, "The higher the difficulty, the more time is required to process AI moves!", ButtonType.OK);
                alert.setTitle("Warning!");
                alert.setHeaderText("Warning!");
                alert.showAndWait();
            }
        });
        difficultySlider.setPadding(new Insets(10, 10, 10, 10));
        difficultySlider.setMaxWidth(100);
        difficultySlider.setMin(0);
        difficultySlider.setMax(10);
        sliderBox.getChildren().addAll(difficultySlider, difficultyLabel);
        pane.add(newGame, 0, 0);
        pane.add(readRules, 0, 1);
        pane.add(toggleHints, 1, 0);
        pane.add(sliderBox, 1, 1);

        return pane;
    }

    /**
     * Opens a new stage containing the rules of the game.
     * */
    private void openRulesWindow()
    {
        // creating new window for rules
        Stage rulesWindow = new Stage();

        // borderpane containing options to display rules
        BorderPane rulesPane = new BorderPane();
        rulesPane.setPrefHeight(450);
        rulesPane.setPrefWidth(400);
        rulesWindow.setResizable(false);
        rulesPane.setPadding(new Insets(15, 15, 15, 15));

        Label title = new Label("The Rules of Checkers");
        title.setPadding(new Insets(0, 0, 15, 0));
        title.setStyle("-fx-font-size: 24;");
        rulesPane.setTop(title);

        ScrollPane rulesDescriptionPane = new ScrollPane();
        rulesDescriptionPane.setFitToWidth(true);
        Label description = new Label("Objective\nEliminate all opposing checkers or to create a " +
                "situation in which it is impossible for the opponent to make any move. Normally, the " +
                "victory will be due to complete elimination.\n\n" +
                "Rules\n" +
                "• Black moves first and play proceeds alternately.\n" +
                "• From their initial positions, checkers may only move forward.\n" +
                "• There are two types of moves that can be made, capturing moves and non-capturing moves." +
                " Non-capturing moves are simply a diagonal move forward from one square to an adjacent " +
                "square. Note that the white squares are never used. Capturing moves occur when a player " +
                "\"jumps\" an opposing piece. This is also done on the diagonal and can only happen when the " +
                "square behind (on the same diagonal) is also open. This means that you may not jump an " +
                "opposing piece around a corner.\n" +
                "• On a capturing move, a piece may make multiple jumps.\n" +
                "• If after a jump a player is in a position to make another jump then he may do so. " +
                "This means that a player may make several jumps in succession, capturing several pieces " +
                "on a single turn.\n" +
                "• When a player is in a position to make a capturing move, he must make a capturing move." +
                " When he has more than one capturing move to choose from he may take whichever move suits him.\n" +
                "• When a checker achieves the opponent's edge of the board (called the \"king's row\") it is" +
                " crowned with another checker. This signifies that the checker has been made a king. The king " +
                "now gains an added ability to move backward. The king may now also jump in either direction or " +
                "even in both directions in one turn (if he makes multiple jumps).\n" +
                "• If the player gets an uncrowned checker on the king's row because of a capturing move then he " +
                "must stop to be crowned even if another capture seems to be available. He may then use his new " +
                "king on his next move.");
        description.setPadding(new Insets(10, 10, 10, 10));
        description.setWrapText(true);
        rulesDescriptionPane.setContent(description);
        rulesPane.setCenter(rulesDescriptionPane);

        // set up and style scene for rules window
        Scene rulesScene = new Scene(rulesPane);
        rulesScene.getStylesheets().add(getClass().getResource("/css/checkerfx.css").toExternalForm());

        rulesWindow.sizeToScene();
        rulesWindow.setTitle("The Rules of Checkers");
        rulesWindow.setScene(rulesScene);
        rulesWindow.show();
    }

    /**
     * Toggles hints.
     * */
    private void showHints()
    {
        // get all movable checkers
        List<Checker> movableUserCheckers = board.getMovableCheckers(CheckersGame.Player.HUMAN);

        for (int i = 0; i < movableUserCheckers.size(); i++)
        {
            int row = movableUserCheckers.get(i).getRow();
            int col = movableUserCheckers.get(i).getColumn();

            // change colour of tiles containing movable checkers
            if (checkers[row][col] != null)
            {
                if (hintsToggled)
                {
                    tiles[row][col].setStyle("-fx-background-color: #2F4F4F");
                }
                else
                {
                    tiles[row][col].setStyle("-fx-background-color: #181818");
                }
            }
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }
}