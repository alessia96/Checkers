import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import jfxtras.labs.util.event.MouseControlUtil;

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

    private void updateUI(Move move)
    {
        int targetRow = move.getTarget().getRow();
        int targetCol = move.getTarget().getColumn();
        int sourceRow = move.getSource().getRow();
        int sourceCol = move.getSource().getColumn();

        // update source tile
        tiles[sourceRow][sourceCol].getChildren().remove(checkers[sourceRow][sourceCol]);

        // update target tile
        updateTile(targetRow, targetCol);

        tiles[targetRow][targetCol] = new Pane();
        tiles[targetRow][targetCol].setMaxSize(70, 70);
        tiles[targetRow][targetCol].getChildren().add(checkers[targetRow][targetCol]);

        gridPane.add(tiles[targetRow][targetCol], targetCol, targetRow);
    }

    private void getMove()
    {
        Move move = null;
        if (match.getTurn() == CheckersGame.Player.AI)
        {
            //move = match.getAIController().getAIMove();
        }
        if (match.getTurn() == CheckersGame.Player.HUMAN)
        {
            move = match.getUserController().getUserMove();

            if (board.isMoveValid(move))
            {
                board.makeMove(move);
                movesLog.setText("From [" + move.getSource().getRow() + ", " + move.getSource().getColumn() +
                        "] to [" + move.getTarget().getRow() + ", " + move.getTarget().getColumn() + "]" +
                        "\n" + movesLog.getText());
            }

            if (!board.isMoveValid(move))
            {
                Cell target = move.getSource().getCell();
                move = new Move(move.getSource(), target);
                System.out.println("not valid");
            }

            updateUI(move);
            match.getUserController().setTiles(tiles);
        }
    }

    private void generateGrid()
    {
        checkers = new Circle[board.getGrid().length][board.getGrid().length];
        tiles = new Pane[board.getGrid().length][board.getGrid().length];

        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                {
                    if (board.getCellAt(row, col).isBlack())
                    {
                        tiles[row][col] = new Pane();
                        tiles[row][col].setStyle("-fx-background-color: black");

                        updateTile(row, col);

                        gridPane.add(tiles[row][col], col, row);
                    }

                    if (!board.getCellAt(row, col).isBlack())
                    {
                        Box box = new Box(70, 70, 0);
                        gridPane.add(box, col, row);
                    }
                })
        );

        match.getUserController().setTiles(tiles);
    }

    private void updateTile(int row, int col)
    {
        if (board.getCellAt(row, col).isOccupied())
        {
            checkers[row][col] = new Circle(35, 35, 30);
            if (!board.getCellAt(row, col).getChecker().isBlack())
            {
                checkers[row][col].setFill(Color.WHITE);
            }
            checkers[row][col].setStroke(Color.WHITE);

            MouseControlUtil.makeDraggable(checkers[row][col]);

            checkers[row][col].addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                    match.getUserController().onCheckerPressed(row, col));

            checkers[row][col].addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
                    match.getUserController().onCheckerDragged(row, col));

            checkers[row][col].addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
            {
                match.getUserController().onCheckerReleased(checkers[row][col]);
                getMove();
            });

            tiles[row][col].setMaxSize(70, 70);
            tiles[row][col].getChildren().add(checkers[row][col]);
        }
    }

    private void generateInfoPane()
    {
        VBox topPane = new VBox();
        topPane.setAlignment(Pos.CENTER);
        topPane.setPadding(new Insets(10, 10, 0, 10));
        Button newGame = new Button("New Game");
        newGame.setOnAction(event ->
        {
            match = new CheckersGame();
            board = match.getBoard();
            generateGrid();
            generateInfoPane();
        });
        CheckBox toggleHints = new CheckBox("Toggle Hints");
        toggleHints.setOnAction(event ->
        {
            System.out.println("toggled");
        });
        topPane.getChildren().addAll(newGame, toggleHints, new Label(" "));

        VBox movesLogPane = new VBox();
        movesLogPane.setPadding(new Insets(0, 10, 0, 10));
        Label movesLogLabel = new Label("Moves Log");
        movesLog = new TextArea();
        movesLog.setPrefSize(150, 400);
        movesLog.setEditable(false);
        Separator separator = new Separator();
        separator.setPadding(new Insets(15, 10, 10, 10));
        movesLogPane.getChildren().addAll(movesLogLabel, movesLog, separator);

        VBox bottomPane = new VBox();
        bottomPane.setPadding(new Insets(0, 10, 10, 10));
        Label blacksCaptured = new Label("Blacks Captured: " + (12 - match.getBoard().getCheckerCount(true)));
        Label whitesCaptures = new Label("Whites Captured: " + (12 - match.getBoard().getCheckerCount(false)));
        bottomPane.getChildren().addAll(blacksCaptured, whitesCaptures);

        infoPane.setTop(topPane);
        infoPane.setCenter(movesLogPane);
        infoPane.setBottom(bottomPane);
    }

    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }
}