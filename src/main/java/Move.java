/**
 * The type Move.
 */
public class Move
{
    private Checker source;
    private Cell target;
    /**
     * The Captured checker.
     */
    public Checker capturedChecker;

    /**
     * Instantiates a new Move.
     *
     * @param source the source
     * @param target the target
     */
    public Move(Checker source, Cell target)
    {
        this.source = source;
        this.target = target;
    }

    /**
     * Getter for property 'source'.
     *
     * @return Value for property 'source'.
     */
    public Checker getSource() { return source; }

    /**
     * Getter for property 'target'.
     *
     * @return Value for property 'target'.
     */
    public Cell getTarget() { return target; }
}
