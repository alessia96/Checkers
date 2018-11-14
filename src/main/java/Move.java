public class Move
{
    private Cell source, target;

    public Move(Cell source, Cell target)
    {
        this.source = source;
        this.target = target;
    }

    public Cell getSource() { return source; }

    public Cell getTarget() { return target; }
}
