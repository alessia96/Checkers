import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.stream.IntStream;

public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

    private BorderPane root;
    private GridPane gridPane;
    private BorderPane infoPane;

    private Circle[][] checkers;
    private Pane[][] tiles;

    private TextArea movesLog;
    private Slider difficultySlider;

    private int selectedDifficulty = 0;
    private boolean hintsToggled;
    private boolean shownOnce;
    private Label blacksCaptured, whitesCaptured;

    private Move lastUserMove;

    @Override
    public void start(Stage primaryStage)
    {
        root = new BorderPane();
        gridPane = new GridPane();
        infoPane = new BorderPane();

        generateGrid();
        generateInfoPane();

        root.setLeft(gridPane);
        root.setRight(infoPane);

        Scene scene = new Scene(root);
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
        Move move = null;
        boolean getNewMove = false;

        if (match.getTurn() == CheckersGame.Player.AI)
        {
            match.getAIController().updateBoard(board);
            move = match.getAIController().getAIMove(selectedDifficulty);

            System.out.println("seCount " + match.getAIController().seCount + ", deCount " + match.getAIController().deCount +
                    ", pCount " + match.getAIController().pCount);
            for (MovesAndScores mas : match.getAIController().successorEvaluations)
            {
                System.out.println("Move: " + mas.getMove().getSource().getRow() + " "
                        + mas.getMove().getSource().getColumn() + " to " + mas.getMove().getTarget().getRow() + " " +
                        mas.getMove().getTarget().getColumn() + ", scores " + mas.getScore());
            }

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
                generateGrid();
            }
        }
        else if (match.getTurn() == CheckersGame.Player.HUMAN)
        {
            //System.out.println(board.getCellAt(5, 0).isOccupied());
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
                board.makeMove(move, false);
                lastUserMove = move;
                movesLog.setText("Black moves from [" + move.getSource().getRow() + ", " + move.getSource().getColumn() +
                    "] to [" + move.getTarget().getRow() + ", " + move.getTarget().getColumn() + "]\n" + movesLog.getText());
                getNewMove = true;
            }
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

            generateGrid();

            if (getNewMove)
            {
                getNewMove = false;
                getMove();
            }
        }
    }

    private void generateGrid()
    {
        if (checkers == null) checkers = new Circle[board.getGrid().length][board.getGrid().length];
        if (tiles == null) tiles = new Pane[board.getGrid().length][board.getGrid().length];

        IntStream.range(0, 8).forEach((row) ->
            IntStream.range(0, 8).forEach((col) ->
            {
                if (board.getCellAt(row, col).isBlack())
                {
                    tiles[row][col] = new Pane();
                    tiles[row][col].setStyle("-fx-background-color: black");

                    if (board.getCellAt(row, col).isOccupied())
                    {
                        checkers[row][col] = new Circle(35, 35, 30);

                        if (board.getCellAt(row, col).getChecker().isKing())
                        {
                            //TODO
                        }
                        if (board.getCellAt(row, col).getChecker().getColour() == Checker.Colour.WHITE)
                        {
                            checkers[row][col].setFill(Color.WHITE);
                        }
                        else
                        {
                            MouseControlUtil.makeDraggable(checkers[row][col]);

                            checkers[row][col].addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                                    match.getUserController().onCheckerPressed(row, col));

                            checkers[row][col].addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
                            {
                                match.getUserController().onCheckerReleased(checkers[row][col]);
                                getMove();
                            });
                        }
                        checkers[row][col].setStroke(Color.WHITE);

//                        BufferedImage b = null;
//                        try {
//                            b = ImageIO.read(getClass().getClassLoader().getResource("crown.png"));
//                        } catch(Exception e)
//                        {
//
//                        }
//                        WritableImage i = SwingFXUtils.toFXImage(b, null);
//                        ImageView v = new ImageView(i);
//                        v.setScaleX(0.1);
//                        v.setScaleY(0.1);
                        tiles[row][col].setMaxSize(70, 70);
                        tiles[row][col].getChildren().add(checkers[row][col]);

                        if (hintsToggled) {
                            showHints();
                        }
                    }


                    tiles[row][col].addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                            System.out.println(board.getCellAt(row, col).isOccupied()));

                    gridPane.add(tiles[row][col], col, row);
                }

                if (!board.getCellAt(row, col).isBlack())
                {
                    Rectangle box = new Rectangle(70, 70);
                    box.setFill(Color.WHITE);
                    gridPane.add(box, col, row);
                }
            })
        );

        generateInfoPane();
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
        movesLogPane.getChildren().addAll(movesLogLabel, movesLog, separator);

        VBox bottomPane = new VBox();
        bottomPane.setPadding(new Insets(0, 10, 10, 10));
        blacksCaptured = new Label("Blacks Captured: " + (12 - match.getBoard().getCheckers(true).size()));
        whitesCaptured = new Label("Whites Captured: " + (12 - match.getBoard().getCheckers(false).size()));
        bottomPane.getChildren().addAll(blacksCaptured, whitesCaptured);

        infoPane.setTop(topPane);
        infoPane.setCenter(movesLogPane);
        infoPane.setBottom(bottomPane);
    }

    private GridPane topInfoPane()
    {
        GridPane pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(10, 10, 10, 10));
        Button newGame = new Button("New Game");
        newGame.setOnAction(event ->
        {
            match = new CheckersGame();
            board = match.getBoard();
            movesLog.clear();
            generateGrid();
            generateInfoPane();
        });
        Button readRules = new Button("Read Rules");
        readRules.setOnAction(event ->
        {
            try
            {
                Desktop.getDesktop().browse(new URI("http://www.indepthinfo.com/checkers/play.shtml"));
            }
            catch (Exception e)
            {
                //
            }
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
                    checkers[row][col].setStroke(Color.RED);
                }
                else
                {
                    checkers[row][col].setStroke(Color.WHITE);
                }
            }
        }
    }

    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }
}