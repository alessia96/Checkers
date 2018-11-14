public class Move
{
    private Checker source;
    private Cell target;

    public Move(Checker source, Cell target)
    {
        this.source = source;
        this.target = target;
    }

    public Checker getSource() { return source; }

    public Cell getTarget() { return target; }
}
