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
 *
 * @author Alessia Nigretti
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
     *
     *
     * */
    @Override
    public void start(Stage primaryStage)
    {
        root = new BorderPane();
        gridPane = new GridPane();
        infoPane = new BorderPane();

        currentlyDraggable = true;
        generateGrid();
        generateInfoPane();

        root.setLeft(gridPane);
        root.setRight(infoPane);

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/checkerfx.css").toExternalForm());

        // setting up stage
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void getMove()
    {
        Move move;
        boolean getNewMove = false;
        board.resetCapturingMove();

        if (match.getTurn() == CheckersGame.Player.AI)
        {
            try
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
                    movesLog.setText("White moves from [" + move.getSource().getRow() + ", " + move.getSource().getColumn() +
                            "] to [" + move.getTarget().getRow() + ", " + move.getTarget().getColumn() + "]" +
                            "\n" + movesLog.getText());
                    board.makeMove(move, false);
                    currentlyDraggable = true;
                    generateGrid();
                }

                gameFinishedCheck();
            }
            catch(Exception e)
            {
                //
            }
        }
        else if (match.getTurn() == CheckersGame.Player.HUMAN)
        {
            move = match.getUserController().getUserMove();
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
                movesLog.setText("Black moves from [" + move.getSource().getRow() + ", " + move.getSource().getColumn() +
                        "] to [" + move.getTarget().getRow() + ", " + move.getTarget().getColumn() + "]\n" + movesLog.getText());
                board.makeMove(move, false);
                if (board.wasCapturingMove() && board.isCaptureAvailable())
                {
                    match.setTurn(CheckersGame.Player.HUMAN);
                }
                else
                {
                    currentlyDraggable = false;
                }
                getNewMove = true;
            }
            else
            {
                if (board.isSourceSameAsTarget(move))
                {
                    movesLog.setText("Move not valid: target tile cannot be source tile.\n" + movesLog.getText());
                }
                else if (board.isTargetRed(move))
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

            checkers[move.getSource().getRow()][move.getSource().getColumn()].setVisible(false);

            generateGrid();

            if (getNewMove)
            {
                getNewMove = false;
                if (!gameFinishedCheck())
                {
                    Runnable getMoveTask = () -> { getMove(); };
                    FxTimer.runLater(Duration.ofMillis(500), getMoveTask);
                }
            }
        }

        blacksCaptured.setText("Blacks Captured: " + (12 - match.getBoard().getCheckers(true).size()));
        redsCaptured.setText("Reds Captured: " + (12 - match.getBoard().getCheckers(false).size()));
    }

    private boolean gameFinishedCheck()
    {
        if (match.getBoard().getCheckers(true).size() == 0) // blacks are finished
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You lost.", ButtonType.CLOSE);
            alert.setTitle("Defeat");
            alert.setHeaderText("Defeat");
            alert.show();
            currentlyDraggable = false;
            generateGrid();
            return true;
        }
        else if (match.getBoard().getCheckers(false).size() == 0)   // reds are finished
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

    private void generateGrid()
    {
        if (checkers == null) checkers = new Circle[board.getGrid().length][board.getGrid().length];
        if (tiles == null) tiles = new Pane[board.getGrid().length][board.getGrid().length];

        IntStream.range(0, 8).forEach((row) ->
            IntStream.range(0, 8).forEach((col) ->
            {
                if (!board.getCellAt(row, col).isBlack())
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
                if (board.getCellAt(row, col).isBlack())
                {
                    tiles[row][col] = new Pane();
                    tiles[row][col].setStyle("-fx-background-color: #181818");

                    if (board.getCellAt(row, col).isOccupied())
                    {
                        checkers[row][col] = new Circle(37, 35, 30);

                        Image img;
                        if (board.getCellAt(row, col).getChecker().getColour() == Checker.Colour.RED)
                        {
                            if (board.getCellAt(row, col).getChecker().isKing())
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
                            if (board.getCellAt(row, col).getChecker().isKing())
                            {
                                img = new Image("black_king.png");
                            }
                            else
                            {
                                img = new Image("black_checker.png");
                            }

                            checkers[row][col].setFill(new ImagePattern(img));

                            if (currentlyDraggable)
                                MouseControlUtil.makeDraggable(checkers[row][col]);

                            checkers[row][col].addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                                    match.getUserController().onCheckerPressed(row, col));

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
        blacksCaptured = new Label("Blacks Captured: " + (12 - match.getBoard().getCheckers(true).size()));
        redsCaptured = new Label("Reds Captured: " + (12 - match.getBoard().getCheckers(false).size()));
        bottomPane.getChildren().addAll(blacksCaptured, redsCaptured);

        infoPane.setTop(topPane);
        infoPane.setCenter(movesLogPane);
        infoPane.setBottom(bottomPane);

        root.setRight(infoPane);
    }

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

        // setting up and styling scene for rules window
        Scene rulesScene = new Scene(rulesPane);
        rulesScene.getStylesheets().add(getClass().getResource("/css/checkerfx.css").toExternalForm());

        rulesWindow.sizeToScene();
        rulesWindow.setTitle("The Rules of Checkers");
        rulesWindow.setScene(rulesScene);
        rulesWindow.show();
    }

    private void showHints()
    {
        List<Checker> movableUserCheckers = board.getMovableCheckers(CheckersGame.Player.HUMAN);

        for (int i = 0; i < movableUserCheckers.size(); i++) {
            int row = movableUserCheckers.get(i).getRow();
            int col = movableUserCheckers.get(i).getColumn();

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