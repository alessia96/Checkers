public class Cell
{
    private boolean occupied;
    private Checker checkerOccupying;
    private int row;
    private int column;

    public Cell(Checker checker, int row, int column)
    {
        if (checker != null)
        {
            checkerOccupying = checker;
            occupied = true;
        }
        else
        {
            occupied = false;
        }

        this.row = row;
        this.column = column;
    }

    public void occupyCell(Checker checker)
    {
        checkerOccupying = checker;
        occupied = true;
    }

    public boolean isOccupied()
    {
        return occupied;
    }

    public Checker getCheckerOccupying()
    {
        return checkerOccupying;
    }

    public void emptyCell()
    {
        occupied = false;
    }

    public String toString()
    {
        if (occupied)
        {
            return "x";
        }
        else
        {
            return " ";
        }
    }
}
