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

    private Button sourceCell;
    private Button targetCell;

    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        GridPane root = new GridPane();

        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                        {
                            Button btn = new Button(board.getCellAt(row, col).toString());
                            btn.setOnAction((event) ->
                            {
                                if (board.getCellAt(row, col).isOccupied()) // source cell
                                {
                                    sourceCell = btn;
                                    match.getUserController().sourceSelectEvt(board.getCellAt(row, col));
                                }
                                else if (sourceCell != null && !board.getCellAt(row, col).isOccupied())   // target cell
                                {
                                    targetCell = btn;
                                    match.getUserController().targetSelectEvt(board.getCellAt(row, col));
                                    sourceCell.setText(" ");
                                    targetCell.setText(board.getCellAt(row, col).toString());
                                    sourceCell = null;
                                    targetCell = null;
                                }
                            });
                            btn.setPrefSize(30, 30);
                            root.add(btn, col, row);
                        }
                )
        );

        Scene scene = new Scene(root);
        //scene.getStylesheets().add("/resources/com/guigarage/flatterfx/flatterfx.css");

        // setting up stage
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("Checkers");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}