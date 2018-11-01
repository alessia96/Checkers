import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GUI extends Application
{
    private static CheckersGame match;
    private static Board board;

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
        for (int row = 0; row < 8; row++)
        {
            for (int col = 0; col < 8; col++)
            {
                root.add(new Label(board.getCellAt(row, col).toString()), col, row);
            }
        }

        Scene scene = new Scene(root);
        //scene.getStylesheets().add("/resources/com/guigarage/flatterfx/flatterfx.css");

        // setting up stage
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.setTitle("");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
