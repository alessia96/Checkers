public class Cell
{
    private boolean occupied;
    private boolean black;
    private Checker checkerOccupying;
    private int row;
    private int column;

    public Cell(Checker checker, int row, int column, boolean black)
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
        this.black = black;
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

    public Checker getChecker()
    {
        return checkerOccupying;
    }

    public void emptyCell()
    {
        checkerOccupying = null;
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

    public int getRow() { return row; }

    public int getColumn() { return column; }

    public boolean isBlack() { return black; }
}
