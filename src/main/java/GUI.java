import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Box;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.SelectableNode;
import jfxtras.labs.util.event.MouseControlUtil;

import java.util.stream.IntStream;

public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

    private BorderPane root;
    private GridPane gridPane;
    private BorderPane infoPane;

    private Button sourceCell;
    private Circle[][] grid;

    private Circle source, target;

    @Override
    public void start(Stage primaryStage)
    {
        root = new BorderPane();
        gridPane = new GridPane();
        infoPane = new BorderPane();

        generateGrid();
        generateInfoPane();
        //beginGame();

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

    private void beginGame()
    {
        getMove();
    }

    private Move getMove()
    {
        Move move = null;
        if (match.getTurn() == CheckersGame.Player.AI)
        {
            move = match.getAIController().getAIMove();
        }
        if (match.getTurn() == CheckersGame.Player.HUMAN)
        {
            match.getUserController().getUserMove();
        }
        return move;
    }

    private void generateGrid()
    {
        grid = new Circle[board.getGrid().length][board.getGrid().length];

        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                {
                    if (board.getCellAt(row, col).isBlack())
                    {
                        Pane pane = new Pane();
                        pane.setStyle("-fx-background-color: black");

                        if (board.getCellAt(row, col).isOccupied())
                        {
                            grid[row][col] = new Circle(35, 35, 30);
                            if (!board.getCellAt(row, col).getChecker().isBlack())
                            {
                                grid[row][col].setFill(Color.WHITE);
                            }
                            grid[row][col].setStroke(Color.WHITE);

                            MouseControlUtil.makeDraggable(grid[row][col]);

                            grid[row][col].addEventHandler(MouseEvent.MOUSE_PRESSED, event ->
                                System.out.println("pressed"));

                            grid[row][col].addEventHandler(MouseEvent.MOUSE_DRAGGED, event ->
                                System.out.println("dragged"));

                            grid[row][col].addEventHandler(MouseEvent.MOUSE_RELEASED, event ->
                            {
                                System.out.println("released");
                                Transform t = grid[row][col].getLocalToSceneTransform();
                                System.out.println(t.getTx());
                            });

                            pane.setMaxSize(70, 70);
                            pane.getChildren().add(grid[row][col]);
                        }
                        gridPane.add(pane, col, row);
                    }

                    if (!board.getCellAt(row, col).isBlack())
                    {
                        Box box = new Box(70, 70, 0);
                        gridPane.add(box, col, row);
                    }
                })
        );

        match.getUserController().setNodes(grid);
    }

    public Circle[][] getGridButtons()
    {
        return grid;
    }

    public void setGridButton(int row, int col, String value)
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