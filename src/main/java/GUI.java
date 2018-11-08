import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.stream.IntStream;

public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

    private BorderPane root;
    private GridPane grid;
    private BorderPane infoPane;

    private Button sourceCell;

    @Override
    public void start(Stage primaryStage)
    {
        root = new BorderPane();
        grid = new GridPane();
        infoPane = new BorderPane();

        generateGrid();
        generateInfoPane();

        root.setLeft(grid);
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

    private void generateGrid()
    {
        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                        {
                            Button btn = new Button(board.getCellAt(row, col).toString());
                            if (board.getCellAt(row, col).isBlack())
                            {
                                btn.getStyleClass().add("blackCell");
                            }
                            if (board.getCellAt(row, col).isOccupied() && !board.getCellAt(row, col).getChecker().isBlack())
                            {
                                btn.getStyleClass().add("whiteChecker");
                            }

                            btn.setOnAction((event) ->
                            {
                                if (sourceCell != null)   // target cell
                                {
                                    match.getUserController().targetSelectEvt(board.getCellAt(row, col));

                                    if (match.isMoveValid())
                                    {
                                        match.getUserController().makeMove();
                                    }
                                    else
                                    {
                                        System.out.println("Move not valid");
                                    }

                                    if (!match.isCaptureAvailable())
                                    {
                                        sourceCell = null;
                                    }
                                }
                                else if (board.getCellAt(row, col).isOccupied()) // source cell
                                {
                                    sourceCell = btn;
                                    match.getUserController().sourceSelectEvt(board.getCellAt(row, col));
                                }
                                generateGrid();
                                generateInfoPane();
                            });
                            btn.setPrefSize(30, 30);
                            grid.add(btn, col, row);
                        }
                )
        );
    }

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