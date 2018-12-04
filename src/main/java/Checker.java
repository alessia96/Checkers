/**
 * The Checker class represents the checker object and describes its
 * characteristics, such as colour, row, column and whether it is a king or not.
 */
public class Checker
{
    public enum Colour { BLACK, RED }
    private Colour colour;
    private int currentRow;
    private int currentCol;
    private Board board;
    private boolean isKing;

    /**
     * Instantiates a new Checker.
     *
     * @param board  the board
     * @param colour the colour
     * @param row    the row
     * @param column the column
     * @param isKing whether the checker is king or not
     */
    public Checker(Board board, Colour colour, int row, int column, boolean isKing)
    {
        this.board = board;
        this.colour = colour;
        currentRow = row;
        currentCol = column;
        this.isKing = isKing;
    }

    /**
     * Getter for the colour of the checker.
     *
     * @return Checkers.Colour.BLACK if black, Checkers.Colour.RED if red.
     */
    public Colour getColour()
    {
        return colour;
    }

    /**
     * Getter for the row.
     *
     * @return the row.
     */
    public int getRow()
    {
        return currentRow;
    }

    /**
     * Getter for the column.
     *
     * @return the column.
     */
    public int getColumn()
    {
        return currentCol;
    }

    /**
     * Getter for the tile.
     *
     * @return the tile.
     */
    public Tile getTile()
    {
        return board.getTileAt(currentRow, currentCol);
    }

    /**
     * Getter for whether the checker is a king.
     *
     * @return true if checker is king, false otherwise.
     */
    public boolean isKing()
    {
        return isKing;
    }

    /**
     * Crowns the checker to king.
     */
    public void setKing()
    {
        isKing = true;
    }
}
