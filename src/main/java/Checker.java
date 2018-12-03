/**
 * The type Checker.
 */
public class Checker
{
    /**
     * The enum Colour.
     */
    public enum Colour {
        /**
         * Black colour.
         */
        BLACK,
        /**
         * Red colour.
         */
        RED }
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
     * @param isKing the is king
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
     * Getter for property 'colour'.
     *
     * @return Value for property 'colour'.
     */
    public Colour getColour()
    {
        return colour;
    }

    /**
     * Getter for property 'row'.
     *
     * @return Value for property 'row'.
     */
    public int getRow()
    {
        return currentRow;
    }

    /**
     * Getter for property 'column'.
     *
     * @return Value for property 'column'.
     */
    public int getColumn()
    {
        return currentCol;
    }

    /**
     * Getter for property 'cell'.
     *
     * @return Value for property 'cell'.
     */
    public Cell getCell()
    {
        return board.getCellAt(currentRow, currentCol);
    }

    /**
     * Getter for property 'king'.
     *
     * @return Value for property 'king'.
     */
    public boolean isKing()
    {
        return isKing;
    }

    /**
     * Sets king.
     */
    public void setKing()
    {
        isKing = true;
    }
}
