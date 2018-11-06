import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.awt.*;
import java.util.stream.IntStream;

public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

    private Button sourceCell;
    private Button targetCell;

    @Override
    public void start(Stage primaryStage)
    {
        GridPane root = new GridPane();

        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                        {
                            Button btn = new Button(board.getCellAt(row, col).toString());
                            if (board.getCellAt(row, col).isBlack())
                            {
                                btn.setStyle("-fx-background-color: #000000");
                            }
                            btn.setOnAction((event) ->
                            {
                                if (sourceCell != null)   // target cell
                                {
                                    targetCell = btn;
                                    match.getUserController().targetSelectEvt(board.getCellAt(row, col));

                                    if (match.isMoveValid())
                                    {
                                        match.getUserController().makeMove();
                                        sourceCell.setText(" ");
                                        targetCell.setText(board.getCellAt(row, col).toString());
                                    }
                                    else
                                    {
                                        System.out.println("Move not valid");
                                    }

                                    sourceCell = null;
                                    targetCell = null;
                                }
                                else if (board.getCellAt(row, col).isOccupied()) // source cell
                                {
                                    sourceCell = btn;
                                    match.getUserController().sourceSelectEvt(board.getCellAt(row, col));
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

    public static void main(String[] args)
    {
        match = new CheckersGame();
        board = match.getBoard();
        launch(args);
    }
}