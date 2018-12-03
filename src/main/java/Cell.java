/**
 * The type Cell.
 */
public class Cell
{
    private boolean occupied;
    private boolean black;
    private Checker checkerOccupying;
    private int row;
    private int column;

    /**
     * Instantiates a new Cell.
     *
     * @param checker the checker
     * @param row     the row
     * @param column  the column
     * @param black   the black
     */
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

    /**
     * Occupy cell.
     *
     * @param checker the checker
     */
    public void occupyCell(Checker checker)
    {
        checkerOccupying = checker;
        occupied = true;
    }

    /**
     * Getter for property 'occupied'.
     *
     * @return Value for property 'occupied'.
     */
    public boolean isOccupied()
    {
        return occupied;
    }

    /**
     * Getter for property 'checker'.
     *
     * @return Value for property 'checker'.
     */
    public Checker getChecker()
    {
        return checkerOccupying;
    }

    /**
     * Empty cell.
     */
    public void emptyCell()
    {
        checkerOccupying = null;
        occupied = false;
    }

    /** {@inheritDoc} */
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

    /**
     * Getter for property 'row'.
     *
     * @return Value for property 'row'.
     */
    public int getRow() { return row; }

    /**
     * Getter for property 'column'.
     *
     * @return Value for property 'column'.
     */
    public int getColumn() { return column; }

    /**
     * Getter for property 'black'.
     *
     * @return Value for property 'black'.
     */
    public boolean isBlack() { return black; }
}
