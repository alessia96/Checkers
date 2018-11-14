import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

    public Circle[][] getGridButtons()
    {
        return checkers;
    }

    public void setChecker(int row, int col, String value)
    {
        //grid[row][col].setText(value);
    }

    /*private void generateGrid()
    {
        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                        {
                            if (board.getCellAt(row, col).isBlack())
                            {
                                Button btn = new Button(board.getCellAt(row, col).toString());
                                if (board.getCellAt(row, col).isOccupied() && !board.getCellAt(row, col).getChecker().isBlack())
                                {
                                    btn.getStyleClass().add("whiteChecker");
                                }

                                if (match.getTurn() == CheckersGame.Player.HUMAN) {
                                    btn.setOnAction((event) ->
                                    {
                                        System.out.println("BTn");
                                        if (sourceCell != null)   // target cell
                                        {
                                            target = board.getCellAt(row, col);
                                            Move move = new Move(source, target);

                                            if (board.isMoveValid(move)) {
                                                board.makeMove(move);
                                            } else {
                                                System.out.println("Move not valid");
                                            }

                                            if (!board.isCaptureAvailable()) {
                                                sourceCell = null;
                                            }
                                        } else if (board.getCellAt(row, col).isOccupied()) // source cell
                                        {
                                            sourceCell = btn;
                                            source = board.getCellAt(row, col);
                                        }
                                        //generateGrid();
                                        generateInfoPane();
                                        match.setTurn(CheckersGame.Player.AI);
                                    });
                                }
                                btn.setPrefSize(30, 30);
                                grid.add(btn, col, row);
                            }
                        }
                )
        );
    }*/

    private void generateInfoPane()
    {
        Button newGame = new Button("New Game");
        TextArea movesLog = new TextArea("Moves Log:\n");
        movesLog.setPrefSize(150, 200);
        movesLog.setEditable(false);
        Label blacksCaptured = new Label("Blacks Captured: " + (12 - match.getBoard().getCheckerCount(true)));
        Label whitesCaptures = new Label("Whites Captured: " + (12 - match.getBoard().getCheckerCount(false)));
        GridPane bottom = new GridPane();
        bottom.add(blacksCaptured, 0, 0);
        bottom.add(new Label(" "), 1, 0);
        bottom.add(whitesCaptures, 2, 0);
        infoPane.setTop(newGame);
        infoPane.setCenter(movesLog);
        infoPane.setBottom(bottom);
    }

    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }
}