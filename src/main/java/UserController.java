import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class UserController
{
    private Board board;
    private Pane[][] tiles;

    private Checker source;
    private Cell target;

    private Move userMove;

    public UserController(Board board)
    {
        this.board = board;
        tiles = new Pane[board.getGrid().length][board.getGrid().length];
    }

    public Move getUserMove()
    {
        return userMove;
    }

    public void setTiles(Pane[][] tiles)
    {
        this.tiles = tiles;
    }

    public void onCheckerPressed(int row, int col)
    {
        source = board.getCellAt(row, col).getChecker();
        System.out.println("pressed at " + row + " " + col);
    }

    public void onCheckerDragged(int row, int col)
    {

    }

    public void onCheckerReleased(Circle checker)
    {
        Bounds boundsInScene = checker.localToScene(checker.getBoundsInLocal());
        double worldX = boundsInScene.getMaxY() - 35;
        double worldY = boundsInScene.getMaxX() - 35;

        target = getPaneLandedAt(worldX, worldY);

        userMove = new Move(source, target);
    }

    private Cell getPaneLandedAt(double worldX, double worldY)
    {
        int xIndex = (int) Math.floor(worldX / 70);
        int yIndex = (int) Math.floor(worldY / 70);

        System.out.println("landed at " + xIndex + " " + yIndex);

        return board.getCellAt(xIndex, yIndex);
    }
}