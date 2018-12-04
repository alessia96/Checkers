import javafx.geometry.Bounds;
import javafx.scene.shape.Circle;

/**
 * The UserController class controls the handling of new user moves.
 */
public class UserController
{
    private Board board;
    private Checker source;
    private Tile target;
    private Move userMove;

    /**
     * Instantiates a new UserController object.
     *
     * @param board the board linked to the controller.
     */
    public UserController(Board board)
    {
        this.board = board;
    }

    /**
     * Getter for the user move.
     *
     * @return the user move.
     */
    public Move getUserMove()
    {
        return userMove;
    }

    /**
     * Event for a checker being pressed in the GUI.
     *
     * @param row the row at which the checker is pressed.
     * @param col the column at which the checker is pressed.
     */
    public void onCheckerPressed(int row, int col)
    {
        // information about the source are stored in the class
        source = board.getTileAt(row, col).getChecker();
    }

    /**
     * Event for a checker being released in the GUI.
     *
     * @param checker the checker being released.
     */
    public void onCheckerReleased(Circle checker)
    {
        // information about the position in the scene where the checker was released are computed
        Bounds boundsInScene = checker.localToScene(checker.getBoundsInLocal());
        double worldX = boundsInScene.getMaxY() - 35;
        double worldY = boundsInScene.getMaxX() - 35;

        // target location is calculated from scene coordinates
        target = getPaneLandedAt(worldX, worldY);

        // user move is saved as a new move between previously source and target
        userMove = new Move(source, target);
    }

    /**
     * Computes what tile is at coordinates worldX and worldY.
     *
     * @param worldX value of X in scene coordinates.
     * @param worldY value of Y in scene coordinates.
     * @return Tile the exact tile corresponding to the given inputs.
     */
    private Tile getPaneLandedAt(double worldX, double worldY)
    {
        int xIndex = (int) Math.floor(worldX / 70);
        int yIndex = (int) Math.floor(worldY / 70);

        if (xIndex < 0 || xIndex >= board.getGrid().length || yIndex < 0 || yIndex >= board.getGrid().length)
        {
            xIndex = source.getRow();
            yIndex = source.getColumn();
        }

        return board.getTileAt(xIndex, yIndex);
    }
}