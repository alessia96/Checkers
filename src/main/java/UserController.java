import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;

/**
 * The type User controller.
 */
public class UserController
{
    private Board board;

    private Checker source;
    private Cell target;

    private Move userMove;

    /**
     * Instantiates a new User controller.
     *
     * @param board the board
     */
    public UserController(Board board)
    {
        this.board = board;
    }

    /**
     * Getter for property 'userMove'.
     *
     * @return Value for property 'userMove'.
     */
    public Move getUserMove()
    {
        return userMove;
    }

    /**
     * On checker pressed.
     *
     * @param row the row
     * @param col the col
     */
    public void onCheckerPressed(int row, int col)
    {
        source = board.getCellAt(row, col).getChecker();
    }

    /**
     * On checker released.
     *
     * @param checker the checker
     */
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