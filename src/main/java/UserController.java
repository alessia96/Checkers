import javafx.scene.shape.Circle;
import jfxtras.labs.util.event.MouseControlUtil;

import java.util.stream.IntStream;

public class UserController
{
    private Board board;
    public Circle[][] grid;

    public UserController(Board board)
    {
        this.board = board;
        grid = new Circle[board.getGrid().length][board.getGrid().length];
    }

    public void getUserMove()
    {
        IntStream.range(0, 8).forEach((row) ->
                IntStream.range(0, 8).forEach((col) ->
                {
                    MouseControlUtil.makeDraggable(grid[row][col]);
                }
            )
        );
    }

    public void setNodes(Circle[][] grid)
    {
        this.grid = grid;
    }
}