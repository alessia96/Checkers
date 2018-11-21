import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;

public class UserController
{
    private Board board;

    private Checker source;
    private Cell target;

    private Move userMove;

    public UserController(Board board)
    {
        this.board = board;
    }

    public Move getUserMove()
    {
        return userMove;
    }

    public void onCheckerPressed(int row, int col)
    {
        source = board.getCellAt(row, col).getChecker();
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

        if (xIndex < 0 || xIndex >= board.getGrid().length || yIndex < 0 || yIndex >= board.getGrid().length)
        {
            xIndex = source.getRow();
            yIndex = source.getColumn();
        }

        return board.getCellAt(xIndex, yIndex);
    }
}