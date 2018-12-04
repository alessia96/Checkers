/**
 * The Move class represents a single move.
 * Contains information about the checker that will be moved
 * and the target tile to which it will move.
 */
public class Move
{
    private Checker source;
    private Tile target;
    private Checker capturedChecker;

    /**
     * Instantiates a new Move object.
     *
     * @param source the checker that will be moved.
     * @param target the target tile to which it will move.
     */
    public Move(Checker source, Tile target)
    {
        this.source = source;
        this.target = target;
    }

    /**
     * Getter for the source checker.
     *
     * @return the source checker.
     */
    public Checker getSource() { return source; }

    /**
     * Getter for the target tile.
     *
     * @return the target tile.
     */
    public Tile getTarget() { return target; }

    /**
     * Getter for the captured checker.
     *
     * @return the captured checker.
     */
    public Checker getCapturedChecker() { return capturedChecker; }

    /**
     * Setter for captured checker.
     *
     * @param capturedChecker the checker to be assigned.
     */
    public void setCapturedChecker(Checker capturedChecker) { this.capturedChecker = capturedChecker; }
}

