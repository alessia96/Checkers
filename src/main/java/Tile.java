/**
 * The Tile class represents a tile in the board.
 * Tile handles whether each tile is occupied by a checker, what checker
 * occupies it and in what row and column it is located.
 */
public class Tile
{
    private boolean occupied;
    private boolean black;
    private Checker checkerOccupying;
    private int row;
    private int column;

    /**
     * Instantiates a new Tile object.
     *
     * @param checker the checker on the tile (if any).
     * @param row     the row.
     * @param column  the column.
     * @param black   whether the tile is black (and therefore can be occupied by a checker).
     */
    public Tile(Checker checker, int row, int column, boolean black)
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
     * Occupies a tile with a given checker.
     *
     * @param checker the checker with which to occupy the tile.
     */
    public void occupyTile(Checker checker)
    {
        checkerOccupying = checker;
        occupied = true;
    }

    /**
     * Getter for whether the tile is occupied or not.
     *
     * @return true if tile is occupied, false otherwise.
     */
    public boolean isOccupied()
    {
        return occupied;
    }

    /**
     * Getter for the checker occupying the tile.
     *
     * @return the checker occupying the tile.
     */
    public Checker getChecker()
    {
        return checkerOccupying;
    }

    /**
     * Empties the tile.
     */
    public void emptyTile()
    {
        checkerOccupying = null;
        occupied = false;
    }

    /**
     * Getter for the row.
     *
     * @return the row of the tile.
     */
    public int getRow() { return row; }

    /**
     * Getter for the column.
     *
     * @return the column of the tile.
     */
    public int getColumn() { return column; }

    /**
     * Getter for whether the tile is black (and therefore can be occupied).
     *
     * @return true if the tile can be occupied, false otherwise.
     */
    public boolean isBlack() { return black; }
}
