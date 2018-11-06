import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.util.stream.IntStream;

public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

    private GridPane root;

    private Button sourceCell;

    @Override
    public void start(Stage primaryStage)
    {
        root = new GridPane();

        generateBoard();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/checkerfx.css").toExternalForm());

        // setting up stage
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void generateBoard()
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

                                    sourceCell = null;
                                }
                                else if (board.getCellAt(row, col).isOccupied()) // source cell
                                {
                                    sourceCell = btn;
                                    match.getUserController().sourceSelectEvt(board.getCellAt(row, col));
                                }
                                generateBoard();
                            });
                            btn.setPrefSize(30, 30);
                            root.add(btn, col, row);
                        }
                )
        );
    }

    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }
}